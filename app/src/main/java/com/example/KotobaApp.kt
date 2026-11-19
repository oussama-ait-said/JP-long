package com.example

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.audio.JapaneseTtsHelper
import com.example.data.curriculum.Lesson
import com.example.data.firebase.KotobaRepository
import com.example.data.model.JlptLevel
import com.example.data.model.UiState
import com.example.ui.auth.AuthScreen
import com.example.ui.auth.authStateFlow
import com.example.ui.auth.signOut
import com.example.ui.screens.CurriculumScreen
import com.example.ui.screens.FlashcardsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.KanaDojoScreen
import com.example.ui.screens.LessonDetailScreen
import com.example.ui.screens.ProfileSheet
import com.example.ui.screens.QuizScreen
import com.example.viewmodel.KotobaViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

const val ROUTE_HOME = "home"
const val ROUTE_PATH = "path"
const val ROUTE_FLASHCARDS = "flashcards"
const val ROUTE_QUIZ = "quiz"

private data class NavDestination(
  val route: String,
  val labelArabic: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
)

private val TopDestinations = listOf(
  NavDestination(ROUTE_HOME, "الرئيسية", Icons.Filled.Home, Icons.Outlined.Home),
  NavDestination(ROUTE_PATH, "المسار", Icons.Filled.MenuBook, Icons.Outlined.MenuBook),
  NavDestination(ROUTE_FLASHCARDS, "المراجعة", Icons.Filled.Style, Icons.Outlined.Style),
  NavDestination(ROUTE_QUIZ, "الاختبارات", Icons.Filled.Quiz, Icons.Outlined.Quiz),
)

@Composable
fun KotobaAppRoot(auth: FirebaseAuth = Firebase.auth) {
  CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
    val user = currentUser

    if (user == null) {
      AuthScreen(onAuthSuccess = {})
    } else {
      key(user.uid) {
        KotobaAuthenticatedShell(
          currentUserId = user.uid,
          initialDisplayName = user.displayName ?: "متعلّم كوتوبا",
        )
      }
    }
  }
}

@Composable
private fun KotobaAuthenticatedShell(
  currentUserId: String,
  initialDisplayName: String,
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val credentialManager = remember(context) { CredentialManager.create(context) }

  val ttsHelper = remember(context) { JapaneseTtsHelper(context) }
  DisposableEffect(ttsHelper) {
    onDispose { ttsHelper.shutdown() }
  }

  val viewModel: KotobaViewModel = viewModel(
    key = currentUserId,
    factory = viewModelFactory {
      initializer {
        val app = checkNotNull(this[APPLICATION_KEY]) {
          "APPLICATION_KEY missing from CreationExtras"
        }
        val databaseId = app.getString(R.string.firestore_database_id)
        val db = FirebaseFirestore.getInstance(databaseId)
        KotobaViewModel(
          repository = KotobaRepository(db),
          currentUserId = currentUserId,
          initialDisplayName = initialDisplayName,
        )
      }
    },
  )

  val profileState by viewModel.profileState.collectAsStateWithLifecycle()
  val flashcardsState by viewModel.flashcardsState.collectAsStateWithLifecycle()
  val quizResultsState by viewModel.quizResultsState.collectAsStateWithLifecycle()
  val selectedLevelFilter by viewModel.selectedLevelFilter.collectAsStateWithLifecycle()
  val quizSession by viewModel.quizSession.collectAsStateWithLifecycle()
  val isBusy by viewModel.isBusy.collectAsStateWithLifecycle()

  val snackbarHostState = remember { SnackbarHostState() }
  LaunchedEffect(viewModel) {
    viewModel.snackbarEvents.collect { msg ->
      snackbarHostState.showSnackbar(msg)
    }
  }

  val speakJapanese: (String) -> Unit = { jpText ->
    val ok = ttsHelper.speak(jpText)
    if (!ok) {
      // Engine may not have Japanese voice data on bare emulator, still no crash
    }
  }

  var activeLesson by remember { mutableStateOf<Lesson?>(null) }
  var isKanaDojoOpen by remember { mutableStateOf(false) }
  var isProfileOpen by remember { mutableStateOf(false) }

  val profile = (profileState as? UiState.Success)?.data
  val flashcards = (flashcardsState as? UiState.Success)?.data ?: emptyList()
  val savedFlashcardIds = remember(flashcards) {
    flashcards.map { it.itemId.ifBlank { it.id } }.toSet()
  }
  val completedLessonIds = remember(profile) {
    profile?.completedLessonIds?.toSet() ?: emptySet()
  }

  if (isKanaDojoOpen) {
    KanaDojoScreen(
      savedFlashcardIds = savedFlashcardIds,
      onSpeak = speakJapanese,
      onBookmarkKana = { ch ->
        viewModel.bookmarkFlashcard(
          itemId = ch.id,
          japanese = ch.symbol,
          reading = ch.symbol,
          romaji = ch.romaji,
          arabicMeaning = "${if (ch.isKatakana) "كاتاكانا" else "هيراغانا"} — يُنطق: ${ch.arabicPhonetic}",
          level = JlptLevel.KANA,
        )
      },
      onBack = { isKanaDojoOpen = false },
    )
    return
  }

  val lessonToShow = activeLesson
  if (lessonToShow != null) {
    LessonDetailScreen(
      lesson = lessonToShow,
      isCompleted = completedLessonIds.contains(lessonToShow.id),
      savedFlashcardIds = savedFlashcardIds,
      onSpeak = speakJapanese,
      onBookmarkItem = { id, jp, reading, romaji, meaning, lvl ->
        viewModel.bookmarkFlashcard(id, jp, reading, romaji, meaning, lvl)
      },
      onCompleteLesson = { viewModel.completeLesson(lessonToShow) },
      onBack = { activeLesson = null },
    )
    return
  }

  val navController = rememberNavController()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route ?: ROUTE_HOME

  val navigateTop: (String) -> Unit = { route ->
    navController.navigate(route) {
      popUpTo(ROUTE_HOME) { saveState = true }
      launchSingleTop = true
      restoreState = true
    }
  }

  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val useRail = maxWidth >= 600.dp

    Scaffold(
      containerColor = MaterialTheme.colorScheme.background,
      contentWindowInsets = WindowInsets.safeDrawing,
      snackbarHost = { SnackbarHost(snackbarHostState) },
      bottomBar = {
        if (!useRail) {
          NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            windowInsets = WindowInsets.navigationBars,
          ) {
            TopDestinations.forEach { dest ->
              val selected = currentRoute == dest.route
              NavigationBarItem(
                selected = selected,
                onClick = { navigateTop(dest.route) },
                icon = {
                  Icon(
                    imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                    contentDescription = dest.labelArabic,
                  )
                },
                label = { Text(dest.labelArabic) },
                modifier = Modifier.testTag("nav_${dest.route}"),
              )
            }
          }
        }
      },
    ) { innerPadding ->
      Row(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding),
      ) {
        if (useRail) {
          NavigationRail(
            modifier = Modifier.fillMaxHeight(),
            containerColor = MaterialTheme.colorScheme.surface,
          ) {
            TopDestinations.forEach { dest ->
              val selected = currentRoute == dest.route
              NavigationRailItem(
                selected = selected,
                onClick = { navigateTop(dest.route) },
                icon = {
                  Icon(
                    imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                    contentDescription = dest.labelArabic,
                  )
                },
                label = { Text(dest.labelArabic) },
                modifier = Modifier.testTag("rail_${dest.route}"),
              )
            }
          }
        }

        Box(modifier = Modifier.weight(1f).fillMaxSize()) {
          NavHost(
            navController = navController,
            startDestination = ROUTE_HOME,
          ) {
            composable(ROUTE_HOME) {
              HomeScreen(
                profile = profile,
                flashcards = flashcards,
                quizResults = (quizResultsState as? UiState.Success)?.data ?: emptyList(),
                onOpenLesson = { activeLesson = it },
                onOpenKanaDojo = { isKanaDojoOpen = true },
                onOpenLevelInPath = { lvl ->
                  viewModel.setLevelFilter(lvl)
                  navigateTop(ROUTE_PATH)
                },
                onOpenFlashcards = { navigateTop(ROUTE_FLASHCARDS) },
                onOpenProfile = { isProfileOpen = true },
              )
            }
            composable(ROUTE_PATH) {
              CurriculumScreen(
                selectedLevel = selectedLevelFilter,
                completedLessonIds = completedLessonIds,
                onSelectLevel = { viewModel.setLevelFilter(it) },
                onOpenLesson = { activeLesson = it },
                onOpenKanaDojo = { isKanaDojoOpen = true },
              )
            }
            composable(ROUTE_FLASHCARDS) {
              FlashcardsScreen(
                flashcardsState = flashcardsState,
                isBusy = isBusy,
                onSeedDeck = { viewModel.seedStarterDeck(it) },
                onReviewCard = { card, quality -> viewModel.reviewFlashcard(card, quality) },
                onDeleteCard = { viewModel.deleteFlashcard(it) },
                onSpeak = speakJapanese,
              )
            }
            composable(ROUTE_QUIZ) {
              QuizScreen(
                quizSession = quizSession,
                quizResultsState = quizResultsState,
                onStartQuiz = { viewModel.startQuiz(it) },
                onSelectOption = { viewModel.answerQuizOption(it) },
                onAdvanceQuiz = { viewModel.advanceQuiz() },
                onCloseQuiz = { viewModel.closeQuiz() },
                onSpeak = speakJapanese,
              )
            }
          }
        }
      }
    }
  }

  if (isProfileOpen) {
    ProfileSheet(
      profile = profile,
      onUpdatePreferences = { lvl, goal -> viewModel.updatePreferences(lvl, goal) },
      onSignOut = {
        isProfileOpen = false
        signOut(credentialManager = credentialManager, scope = scope)
      },
      onDismiss = { isProfileOpen = false },
    )
  }
}
