package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.curriculum.CurriculumN4N3Data
import com.example.data.curriculum.Lesson
import com.example.data.curriculum.QuizQuestion
import com.example.data.firebase.KotobaRepository
import com.example.data.model.FlashcardProgress
import com.example.data.model.JlptLevel
import com.example.data.model.QuizResult
import com.example.data.model.SrsQuality
import com.example.data.model.UiState
import com.example.data.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class QuizSessionState(
  val levelFilter: JlptLevel?, // null = MIXED
  val questions: List<QuizQuestion>,
  val currentIndex: Int = 0,
  val selectedOptionIndex: Int? = null,
  val correctCount: Int = 0,
  val isFinished: Boolean = false,
  val lastXpEarned: Long? = null,
)

class KotobaViewModel(
  private val repository: KotobaRepository,
  private val currentUserId: String,
  private val initialDisplayName: String,
) : ViewModel() {

  private val _selectedLevelFilter = MutableStateFlow<JlptLevel?>(null)
  val selectedLevelFilter: StateFlow<JlptLevel?> = _selectedLevelFilter.asStateFlow()

  private val _quizSession = MutableStateFlow<QuizSessionState?>(null)
  val quizSession: StateFlow<QuizSessionState?> = _quizSession.asStateFlow()

  private val _isBusy = MutableStateFlow(false)
  val isBusy: StateFlow<Boolean> = _isBusy.asStateFlow()

  private val _snackbarEvents = MutableSharedFlow<String>(extraBufferCapacity = 4)
  val snackbarEvents = _snackbarEvents.asSharedFlow()

  val profileState: StateFlow<UiState<UserProfile?>> = repository.observeUserProfile(currentUserId)
    .map<UserProfile?, UiState<UserProfile?>> { UiState.Success(it) }
    .catch { error ->
      val firestoreError = generateSequence(error) { it.cause }
        .filterIsInstance<FirebaseFirestoreException>()
        .firstOrNull()
      if (firestoreError == null && error is CancellationException) throw error
      val rootError = firestoreError ?: error
      Log.w(TAG, "Error observing profile", rootError)
      emit(UiState.Error(rootError.localizedMessage ?: "تعذّر تحميل ملف المتعلّم"))
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
      initialValue = UiState.Loading,
    )

  val flashcardsState: StateFlow<UiState<List<FlashcardProgress>>> =
    repository.observeFlashcards(currentUserId)
      .map<List<FlashcardProgress>, UiState<List<FlashcardProgress>>> { cards ->
        UiState.Success(cards.sortedBy { it.nextReviewAtMs })
      }
      .catch { error ->
        val firestoreError = generateSequence(error) { it.cause }
          .filterIsInstance<FirebaseFirestoreException>()
          .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val rootError = firestoreError ?: error
        Log.w(TAG, "Error observing flashcards", rootError)
        emit(UiState.Error(rootError.localizedMessage ?: "تعذّر تحميل البطاقات التعليمية"))
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = UiState.Loading,
      )

  val quizResultsState: StateFlow<UiState<List<QuizResult>>> =
    repository.observeQuizResults(currentUserId)
      .map<List<QuizResult>, UiState<List<QuizResult>>> { UiState.Success(it) }
      .catch { error ->
        val firestoreError = generateSequence(error) { it.cause }
          .filterIsInstance<FirebaseFirestoreException>()
          .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val rootError = firestoreError ?: error
        Log.w(TAG, "Error observing quiz results", rootError)
        emit(UiState.Error(rootError.localizedMessage ?: "تعذّر تحميل سجل الاختبارات"))
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = UiState.Loading,
      )

  init {
    viewModelScope.launch {
      repository.ensureUserProfile(currentUserId, initialDisplayName)
        .onFailure { err ->
          _snackbarEvents.tryEmit(err.localizedMessage ?: "تعذّر تهيئة ملف المتعلّم")
        }
    }
  }

  fun setLevelFilter(level: JlptLevel?) {
    _selectedLevelFilter.value = level
  }

  fun completeLesson(lesson: Lesson) {
    viewModelScope.launch {
      _isBusy.value = true
      repository.completeLesson(currentUserId, lesson.id, lesson.xpReward)
        .onSuccess {
          _snackbarEvents.tryEmit("أحسنت! تم إتمام الدرس «${lesson.titleArabic}» (+${lesson.xpReward} XP)")
        }
        .onFailure { err ->
          _snackbarEvents.tryEmit(err.localizedMessage ?: "تعذّر حفظ تقدّم الدرس")
        }
      _isBusy.value = false
    }
  }

  fun bookmarkFlashcard(
    itemId: String,
    japanese: String,
    reading: String,
    romaji: String,
    arabicMeaning: String,
    level: JlptLevel,
  ) {
    viewModelScope.launch {
      repository.saveOrBookmarkFlashcard(
        userId = currentUserId,
        itemId = itemId,
        japanese = japanese,
        reading = reading,
        romaji = romaji,
        arabicMeaning = arabicMeaning,
        level = level,
      ).onSuccess {
        _snackbarEvents.tryEmit("تمت إضافة «$japanese» إلى بطاقات المراجعة")
      }.onFailure { err ->
        _snackbarEvents.tryEmit(err.localizedMessage ?: "تعذّر حفظ البطاقة")
      }
    }
  }

  fun seedStarterDeck(level: JlptLevel) {
    viewModelScope.launch {
      _isBusy.value = true
      repository.seedStarterDeck(currentUserId, level)
        .onSuccess { count ->
          if (count > 0) {
            _snackbarEvents.tryEmit("تم تحميل $count بطاقات لمستوى ${level.titleArabic}")
          } else {
            _snackbarEvents.tryEmit("كل بطاقات هذا المستوى محفوظة بالفعل في مجموعتك")
          }
        }
        .onFailure { err ->
          _snackbarEvents.tryEmit(err.localizedMessage ?: "تعذّر تحميل البطاقات")
        }
      _isBusy.value = false
    }
  }

  fun reviewFlashcard(card: FlashcardProgress, quality: SrsQuality) {
    viewModelScope.launch {
      repository.reviewFlashcard(currentUserId, card, quality)
        .onFailure { err ->
          _snackbarEvents.tryEmit(err.localizedMessage ?: "تعذّر تحديث جدول المراجعة")
        }
    }
  }

  fun deleteFlashcard(cardId: String) {
    viewModelScope.launch {
      repository.deleteFlashcard(currentUserId, cardId)
        .onSuccess {
          _snackbarEvents.tryEmit("تم حذف البطاقة من المجموعة")
        }
        .onFailure { err ->
          _snackbarEvents.tryEmit(err.localizedMessage ?: "تعذّر حذف البطاقة")
        }
    }
  }

  fun updatePreferences(targetLevel: JlptLevel, dailyGoal: Long) {
    viewModelScope.launch {
      _isBusy.value = true
      repository.updatePreferences(currentUserId, targetLevel, dailyGoal)
        .onSuccess {
          _snackbarEvents.tryEmit("تم تحديث تفضيلات التعلّم بنجاح")
        }
        .onFailure { err ->
          _snackbarEvents.tryEmit(err.localizedMessage ?: "تعذّر حفظ التفضيلات")
        }
      _isBusy.value = false
    }
  }

  fun startQuiz(level: JlptLevel?) {
    val pool = if (level == null) {
      CurriculumN4N3Data.quizQuestions.shuffled().take(6)
    } else {
      CurriculumN4N3Data.quizQuestions.filter { it.level == level }.shuffled()
    }
    if (pool.isEmpty()) return
    _quizSession.value = QuizSessionState(
      levelFilter = level,
      questions = pool,
    )
  }

  fun answerQuizOption(optionIndex: Int) {
    val session = _quizSession.value ?: return
    if (session.selectedOptionIndex != null || session.isFinished) return
    val currentQ = session.questions[session.currentIndex]
    val isCorrect = optionIndex == currentQ.correctIndex
    _quizSession.value = session.copy(
      selectedOptionIndex = optionIndex,
      correctCount = session.correctCount + if (isCorrect) 1 else 0,
    )
  }

  fun advanceQuiz() {
    val session = _quizSession.value ?: return
    if (session.currentIndex + 1 < session.questions.size) {
      _quizSession.value = session.copy(
        currentIndex = session.currentIndex + 1,
        selectedOptionIndex = null,
      )
    } else {
      _quizSession.value = session.copy(isFinished = true)
      val levelCode = session.levelFilter?.code ?: "MIXED"
      viewModelScope.launch {
        repository.recordQuizResult(
          userId = currentUserId,
          levelCode = levelCode,
          score = session.correctCount.toLong(),
          totalQuestions = session.questions.size.toLong(),
        ).onSuccess { xp ->
          _quizSession.value = _quizSession.value?.copy(lastXpEarned = xp)
        }.onFailure { err ->
          _snackbarEvents.tryEmit(err.localizedMessage ?: "تعذّر حفظ نتيجة الاختبار")
        }
      }
    }
  }

  fun closeQuiz() {
    _quizSession.value = null
  }

  private companion object {
    const val TAG = "KotobaVM"
    const val STOP_TIMEOUT_MS = 5000L
  }
}
