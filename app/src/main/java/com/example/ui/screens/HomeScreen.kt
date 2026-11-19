package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.curriculum.CurriculumN4N3Data
import com.example.data.curriculum.Lesson
import com.example.data.model.FlashcardProgress
import com.example.data.model.JlptLevel
import com.example.data.model.QuizResult
import com.example.data.model.UserProfile
import com.example.ui.components.HankoLevelBadge
import com.example.ui.components.JapaneseText
import com.example.ui.components.StatPill
import com.example.ui.theme.KintsugiContainer
import com.example.ui.theme.KintsugiGold

@Composable
fun HomeScreen(
  profile: UserProfile?,
  flashcards: List<FlashcardProgress>,
  quizResults: List<QuizResult>,
  onOpenLesson: (Lesson) -> Unit,
  onOpenKanaDojo: () -> Unit,
  onOpenLevelInPath: (JlptLevel) -> Unit,
  onOpenFlashcards: () -> Unit,
  onOpenProfile: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val completedIds = profile?.completedLessonIds?.toSet() ?: emptySet()
  val allLessons = CurriculumN4N3Data.allLessons
  val nextLesson = remember(completedIds) {
    allLessons.firstOrNull { !completedIds.contains(it.id) } ?: allLessons.first()
  }
  val nowMs = System.currentTimeMillis()
  val dueCardsCount = remember(flashcards, nowMs) {
    flashcards.count { it.nextReviewAtMs <= nowMs }
  }
  val displayName = profile?.displayName?.ifBlank { "متعلّم كوتوبا" } ?: "متعلّم كوتوبا"
  val xp = profile?.xp ?: 0L
  val streak = profile?.streakDays ?: 1L
  val dailyGoal = (profile?.dailyGoal ?: 10L).coerceAtLeast(1L)
  val reviewedTodayApprox = flashcards.count { it.reviewCount > 0L }.coerceAtMost(dailyGoal.toInt())
  val dailyProgress = (reviewedTodayApprox.toFloat() / dailyGoal.toFloat()).coerceIn(0f, 1f)

  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.TopCenter,
  ) {
    LazyColumn(
      modifier = Modifier
        .widthIn(max = 720.dp)
        .fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "أهلاً بك، $displayName",
              style = MaterialTheme.typography.headlineMedium,
              color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
              text = "رحلتك إلى إتقان اليابانية — من الصفر إلى N3",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
          IconButton(
            onClick = onOpenProfile,
            modifier = Modifier
              .size(48.dp)
              .testTag("profile_button"),
          ) {
            Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.primaryContainer,
              modifier = Modifier.size(42.dp),
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(
                  text = displayName.firstOrNull()?.toString() ?: "ك",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary,
                )
              }
            }
          }
        }
      }

      // Hero Banner Card with Mount Fuji illustration
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(24.dp),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(210.dp),
          ) {
            Image(
              painter = painterResource(id = R.drawable.img_hero_fuji),
              contentDescription = "جبل فوجي",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop,
            )
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(
                  Brush.verticalGradient(
                    colors = listOf(
                      Color.Black.copy(alpha = 0.15f),
                      MaterialTheme.colorScheme.primary.copy(alpha = 0.90f),
                    )
                  )
                )
            )
            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
              verticalArrangement = Arrangement.SpaceBetween,
            ) {
              Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                StatPill(
                  label = "يوم متتالي",
                  value = "🔥 $streak",
                  containerColor = MaterialTheme.colorScheme.secondary,
                  contentColor = Color.White,
                )
                StatPill(
                  label = "نقطة XP",
                  value = "★ $xp",
                  containerColor = KintsugiContainer,
                  contentColor = Color(0xFF5D3E00),
                )
              }

              Column {
                Text(
                  text = "الدرس المقترح لك الآن",
                  style = MaterialTheme.typography.labelMedium,
                  color = Color.White.copy(alpha = 0.85f),
                )
                Text(
                  text = nextLesson.titleArabic,
                  style = MaterialTheme.typography.titleLarge,
                  color = Color.White,
                )
                Spacer(Modifier.height(10.dp))
                Button(
                  onClick = { onOpenLesson(nextLesson) },
                  modifier = Modifier.testTag("continue_lesson_button"),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = Color.White,
                  ),
                  shape = RoundedCornerShape(14.dp),
                ) {
                  Icon(Icons.Rounded.PlayArrow, contentDescription = null)
                  Spacer(Modifier.width(6.dp))
                  Text("ابدأ الدرس (+${nextLesson.xpReward} XP)")
                }
              }
            }
          }
        }
      }

      // Daily Stats & Due Flashcards Row
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          Card(
            modifier = Modifier
              .weight(1f)
              .clickable(onClick = onOpenFlashcards)
              .testTag("due_cards_summary_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                Icon(
                  imageVector = Icons.Rounded.Style,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.secondary,
                )
                Text(
                  text = "المراجعة الذكية",
                  style = MaterialTheme.typography.titleMedium,
                )
              }
              Spacer(Modifier.height(8.dp))
              Text(
                text = if (dueCardsCount > 0) "$dueCardsCount بطاقة مستحقة" else "${flashcards.size} بطاقة محفوظة",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
              )
              Spacer(Modifier.height(8.dp))
              LinearProgressIndicator(
                progress = { dailyProgress },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(6.dp)
                  .clip(CircleShape),
                color = MaterialTheme.colorScheme.tertiary,
              )
              Spacer(Modifier.height(4.dp))
              Text(
                text = "الهدف اليومي: $dailyGoal بطاقات",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }

          Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                Icon(
                  imageVector = Icons.Rounded.School,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.tertiary,
                )
                Text(
                  text = "الإنجاز الكلي",
                  style = MaterialTheme.typography.titleMedium,
                )
              }
              Spacer(Modifier.height(8.dp))
              Text(
                text = "${completedIds.size} / ${allLessons.size} دروس",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.tertiary,
              )
              Spacer(Modifier.height(8.dp))
              Text(
                text = "الاختبارات المنجزة: ${quizResults.size}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
              Text(
                text = "المستوى المستهدف: ${JlptLevel.fromRaw(profile?.targetLevel).code}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
              )
            }
          }
        }
      }

      // Kana Dojo Quick Card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenKanaDojo)
            .testTag("home_kana_dojo_card"),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
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
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(14.dp),
              modifier = Modifier.weight(1f),
            ) {
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(52.dp),
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
                Text(
                  text = "مختبر الحروف: هيراغانا وكاتاكانا",
                  style = MaterialTheme.typography.titleMedium,
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                  text = "تعلّم الـ ٩٢ حرفاً بالنطق الصوتي والتدرّب على الرسم باليد",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
            }
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
              contentDescription = "فتح مختبر الحروف",
              tint = MaterialTheme.colorScheme.primary,
            )
          }
        }
      }

      item {
        Text(
          text = "خريطة المستويات (من الصفر إلى N3)",
          style = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.onBackground,
        )
      }

      items(JlptLevel.entries) { level ->
        val levelLessons = allLessons.filter { it.level == level }
        val levelDone = levelLessons.count { completedIds.contains(it.id) }
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              if (level == JlptLevel.KANA) onOpenKanaDojo() else onOpenLevelInPath(level)
            }
            .testTag("roadmap_level_${level.code}"),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                HankoLevelBadge(level = level)
                Text(
                  text = level.titleArabic,
                  style = MaterialTheme.typography.titleMedium,
                )
              }
              Spacer(Modifier.height(6.dp))
              Text(
                text = level.descriptionArabic,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
              if (level != JlptLevel.KANA) {
                Spacer(Modifier.height(6.dp))
                Text(
                  text = "مكتمل: $levelDone من ${levelLessons.size} دروس",
                  style = MaterialTheme.typography.labelMedium,
                  color = MaterialTheme.colorScheme.tertiary,
                )
              } else {
                Spacer(Modifier.height(6.dp))
                Text(
                  text = "٤٦ هيراغانا + ٤٦ كاتاكانا مع لوحة خطّ",
                  style = MaterialTheme.typography.labelMedium,
                  color = MaterialTheme.colorScheme.secondary,
                )
              }
            }
            if (level != JlptLevel.KANA && levelLessons.isNotEmpty() && levelDone == levelLessons.size) {
              Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = "مكتمل",
                tint = MaterialTheme.colorScheme.tertiary,
              )
            } else {
              Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = KintsugiGold,
              )
            }
          }
        }
      }
    }
  }
}
