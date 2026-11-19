package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.curriculum.CurriculumN4N3Data
import com.example.data.curriculum.Lesson
import com.example.data.model.JlptLevel
import com.example.ui.components.HankoLevelBadge
import com.example.ui.components.JapaneseText

@Composable
fun CurriculumScreen(
  selectedLevel: JlptLevel?,
  completedLessonIds: Set<String>,
  onSelectLevel: (JlptLevel?) -> Unit,
  onOpenLesson: (Lesson) -> Unit,
  onOpenKanaDojo: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val lessons = remember(selectedLevel) {
    val all = CurriculumN4N3Data.allLessons
    if (selectedLevel == null || selectedLevel == JlptLevel.KANA) {
      all
    } else {
      all.filter { it.level == selectedLevel }
    }
  }

  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.TopCenter,
  ) {
    LazyColumn(
      modifier = Modifier
        .widthIn(max = 720.dp)
        .fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      item {
        Text(
          text = "مسار التعلّم المتكامل",
          style = MaterialTheme.typography.headlineMedium,
          color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
          text = "دروس منظّمة بالعربية من الحروف حتى مستوى JLPT N3",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          FilterChip(
            selected = selectedLevel == null,
            onClick = { onSelectLevel(null) },
            label = { Text("الكل") },
            modifier = Modifier.testTag("filter_all"),
          )
          listOf(JlptLevel.KANA, JlptLevel.N5, JlptLevel.N4, JlptLevel.N3).forEach { level ->
            FilterChip(
              selected = selectedLevel == level,
              onClick = { onSelectLevel(level) },
              label = { Text(level.titleArabic) },
              modifier = Modifier.testTag("filter_${level.code}"),
            )
          }
        }
      }

      if (selectedLevel == null || selectedLevel == JlptLevel.KANA) {
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable(onClick = onOpenKanaDojo)
              .testTag("curriculum_kana_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f),
            ),
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
              ) {
                Surface(
                  shape = RoundedCornerShape(14.dp),
                  color = MaterialTheme.colorScheme.secondary,
                  modifier = Modifier.size(54.dp),
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    JapaneseText(
                      text = "あア",
                      fontSize = 20.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color.White,
                    )
                  }
                }
                Column {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                  ) {
                    Icon(
                      imageVector = Icons.Rounded.Brush,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.secondary,
                      modifier = Modifier.size(18.dp),
                    )
                    Text(
                      text = "مختبر الهيراغانا والكاتاكانا",
                      style = MaterialTheme.typography.titleMedium,
                      color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                  }
                  Spacer(Modifier.height(4.dp))
                  Text(
                    text = "٩٢ حرفاً أساسياً • حيل ذاكرة عربية • تلفظ صوتي • لوحة كتابة باليد",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                }
              }
              Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = "فتح المختبر",
                tint = MaterialTheme.colorScheme.secondary,
              )
            }
          }
        }
      }

      items(lessons, key = { it.id }) { lesson ->
        val isDone = completedLessonIds.contains(lesson.id)
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenLesson(lesson) }
            .testTag("lesson_card_${lesson.id}"),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                HankoLevelBadge(level = lesson.level)
                Text(
                  text = "الدرس ${lesson.order}",
                  style = MaterialTheme.typography.labelLarge,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
              if (isDone) {
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = MaterialTheme.colorScheme.tertiaryContainer,
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                  ) {
                    Icon(
                      imageVector = Icons.Rounded.CheckCircle,
                      contentDescription = "مكتمل",
                      tint = MaterialTheme.colorScheme.tertiary,
                      modifier = Modifier.size(15.dp),
                    )
                    Text(
                      text = "مكتمل",
                      style = MaterialTheme.typography.labelMedium,
                      color = MaterialTheme.colorScheme.tertiary,
                    )
                  }
                }
              } else {
                Text(
                  text = "+${lesson.xpReward} XP",
                  style = MaterialTheme.typography.labelLarge,
                  color = MaterialTheme.colorScheme.primary,
                )
              }
            }

            Spacer(Modifier.height(10.dp))

            Text(
              text = lesson.titleArabic,
              style = MaterialTheme.typography.titleLarge,
              color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(4.dp))
            Text(
              text = lesson.subtitleArabic,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(12.dp))

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Icon(
                imageVector = Icons.Rounded.MenuBook,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp),
              )
              Text(
                text = "${lesson.grammarPoints.size} قواعد • ${lesson.vocabulary.size} مفردات • ${lesson.kanji.size} كانجي",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }
        }
      }
    }
  }
}
