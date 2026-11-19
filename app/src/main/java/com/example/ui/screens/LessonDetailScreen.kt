package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.BookmarkAdded
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.curriculum.GrammarPoint
import com.example.data.curriculum.KanjiItem
import com.example.data.curriculum.Lesson
import com.example.data.curriculum.VocabItem
import com.example.data.model.JlptLevel
import com.example.ui.components.HankoLevelBadge
import com.example.ui.components.JapaneseText
import com.example.ui.components.TtsSpeakButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonDetailScreen(
  lesson: Lesson,
  isCompleted: Boolean,
  savedFlashcardIds: Set<String>,
  onSpeak: (String) -> Unit,
  onBookmarkItem: (id: String, jp: String, reading: String, romaji: String, meaning: String, level: JlptLevel) -> Unit,
  onCompleteLesson: () -> Unit,
  onBack: () -> Unit,
) {
  BackHandler(onBack = onBack)

  var selectedTab by remember(lesson.id) { mutableIntStateOf(0) }
  val tabs = listOf(
    "القواعد (${lesson.grammarPoints.size})",
    "المفردات (${lesson.vocabulary.size})",
    "الكانجي (${lesson.kanji.size})",
  )

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = lesson.titleArabic,
              style = MaterialTheme.typography.titleMedium,
            )
            Text(
              text = lesson.level.titleArabic,
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("lesson_back_button"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
              contentDescription = "رجوع",
            )
          }
        },
        actions = {
          HankoLevelBadge(
            level = lesson.level,
            modifier = Modifier.padding(end = 12.dp),
          )
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.background,
        ),
      )
    },
    bottomBar = {
      Surface(
        tonalElevation = 3.dp,
        color = MaterialTheme.colorScheme.surface,
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          contentAlignment = Alignment.Center,
        ) {
          Button(
            onClick = onCompleteLesson,
            modifier = Modifier
              .widthIn(max = 600.dp)
              .fillMaxWidth()
              .height(50.dp)
              .testTag("complete_lesson_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isCompleted) {
                MaterialTheme.colorScheme.tertiary
              } else {
                MaterialTheme.colorScheme.secondary
              },
            ),
          ) {
            Icon(Icons.Rounded.CheckCircle, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(
              text = if (isCompleted) {
                "تم إتمام الدرس — مراجعة (+10 XP)"
              } else {
                "إتمام الدرس والحصول على +${lesson.xpReward} XP"
              },
              style = MaterialTheme.typography.titleMedium,
            )
          }
        }
      }
    },
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentAlignment = Alignment.TopCenter,
    ) {
      Column(
        modifier = Modifier
          .widthIn(max = 720.dp)
          .fillMaxSize(),
      ) {
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = MaterialTheme.colorScheme.background,
        ) {
          tabs.forEachIndexed { index, title ->
            Tab(
              selected = selectedTab == index,
              onClick = { selectedTab = index },
              text = { Text(title, style = MaterialTheme.typography.labelLarge) },
              modifier = Modifier.testTag("lesson_tab_$index"),
            )
          }
        }

        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
          when (selectedTab) {
            0 -> items(lesson.grammarPoints, key = { it.id }) { grammar ->
              GrammarCard(grammar = grammar, onSpeak = onSpeak)
            }
            1 -> items(lesson.vocabulary, key = { it.id }) { vocab ->
              val isSaved = savedFlashcardIds.contains(vocab.id)
              VocabCard(
                vocab = vocab,
                isSaved = isSaved,
                onSpeak = onSpeak,
                onBookmark = {
                  onBookmarkItem(
                    vocab.id,
                    vocab.japanese,
                    vocab.reading,
                    vocab.romaji,
                    vocab.arabicMeaning,
                    vocab.level,
                  )
                },
              )
            }
            else -> items(lesson.kanji, key = { it.id }) { kanji ->
              val isSaved = savedFlashcardIds.contains(kanji.id)
              KanjiCard(
                kanji = kanji,
                isSaved = isSaved,
                onSpeak = onSpeak,
                onBookmark = {
                  onBookmarkItem(
                    kanji.id,
                    kanji.character,
                    "${kanji.kunyomi} / ${kanji.onyomi}",
                    kanji.sampleReading,
                    "${kanji.arabicMeaning} — مثال: ${kanji.sampleWord} (${kanji.sampleMeaning})",
                    kanji.level,
                  )
                },
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun GrammarCard(
  grammar: GrammarPoint,
  onSpeak: (String) -> Unit,
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
      ) {
        JapaneseText(
          text = grammar.pattern,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
      }
      Spacer(Modifier.height(10.dp))
      Text(
        text = grammar.arabicTitle,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
      )
      Spacer(Modifier.height(6.dp))
      Text(
        text = grammar.explanationArabic,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Spacer(Modifier.height(10.dp))
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
      ) {
        Text(
          text = "التركيب: ${grammar.formulaArabic}",
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.padding(12.dp),
        )
      }
      Spacer(Modifier.height(12.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
      Spacer(Modifier.height(10.dp))
      Text(
        text = "أمثلة تطبيقية:",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.secondary,
      )
      Spacer(Modifier.height(6.dp))
      grammar.examples.forEach { ex ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Column(modifier = Modifier.weight(1f)) {
            JapaneseText(
              text = ex.japanese,
              fontSize = 17.sp,
              fontWeight = FontWeight.SemiBold,
            )
            Text(
              text = ex.romaji,
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
              text = ex.arabicTranslation,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.primary,
            )
          }
          TtsSpeakButton(onClick = { onSpeak(ex.japanese) })
        }
      }
    }
  }
}

@Composable
private fun VocabCard(
  vocab: VocabItem,
  isSaved: Boolean,
  onSpeak: (String) -> Unit,
  onBookmark: () -> Unit,
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          JapaneseText(
            text = vocab.japanese,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
          )
          JapaneseText(
            text = "${vocab.reading}  •  ${vocab.romaji}",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Spacer(Modifier.height(4.dp))
          Text(
            text = vocab.arabicMeaning,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
          )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
          TtsSpeakButton(onClick = { onSpeak(vocab.japanese) })
          IconButton(
            onClick = onBookmark,
            modifier = Modifier
              .size(48.dp)
              .testTag("bookmark_vocab_${vocab.id}"),
          ) {
            Icon(
              imageVector = if (isSaved) Icons.Rounded.BookmarkAdded else Icons.Rounded.BookmarkAdd,
              contentDescription = if (isSaved) "محفوظة في البطاقات" else "إضافة إلى البطاقات",
              tint = if (isSaved) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary,
            )
          }
        }
      }

      Spacer(Modifier.height(10.dp))
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Column(modifier = Modifier.weight(1f)) {
            JapaneseText(text = vocab.exampleJp, fontSize = 15.sp)
            Text(
              text = vocab.exampleAr,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
          TtsSpeakButton(onClick = { onSpeak(vocab.exampleJp) })
        }
      }
    }
  }
}

@Composable
private fun KanjiCard(
  kanji: KanjiItem,
  isSaved: Boolean,
  onSpeak: (String) -> Unit,
  onBookmark: () -> Unit,
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.size(76.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          JapaneseText(
            text = kanji.character,
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
          )
        }
      }

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = kanji.arabicMeaning,
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
          text = "قراءة أون: ${kanji.onyomi} | قراءة كون: ${kanji.kunyomi}",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        Text(
          text = "مثال: ${kanji.sampleWord} (${kanji.sampleReading}) — ${kanji.sampleMeaning} • ${kanji.strokeCount} خطوات",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.primary,
        )
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TtsSpeakButton(onClick = { onSpeak(kanji.sampleWord) })
        IconButton(
          onClick = onBookmark,
          modifier = Modifier
            .size(48.dp)
            .testTag("bookmark_kanji_${kanji.id}"),
        ) {
          Icon(
            imageVector = if (isSaved) Icons.Rounded.BookmarkAdded else Icons.Rounded.BookmarkAdd,
            contentDescription = if (isSaved) "محفوظة في البطاقات" else "إضافة إلى البطاقات",
            tint = if (isSaved) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary,
          )
        }
      }
    }
  }
}
