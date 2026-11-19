package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JlptLevel
import com.example.data.model.QuizResult
import com.example.data.model.UiState
import com.example.ui.components.HankoLevelBadge
import com.example.ui.components.JapaneseText
import com.example.ui.components.TtsSpeakButton
import com.example.viewmodel.QuizSessionState

@Composable
fun QuizScreen(
  quizSession: QuizSessionState?,
  quizResultsState: UiState<List<QuizResult>>,
  onStartQuiz: (JlptLevel?) -> Unit,
  onSelectOption: (Int) -> Unit,
  onAdvanceQuiz: () -> Unit,
  onCloseQuiz: () -> Unit,
  onSpeak: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.TopCenter,
  ) {
    if (quizSession != null) {
      ActiveQuizView(
        session = quizSession,
        onSelectOption = onSelectOption,
        onAdvanceQuiz = onAdvanceQuiz,
        onCloseQuiz = onCloseQuiz,
        onSpeak = onSpeak,
      )
    } else {
      LazyColumn(
        modifier = Modifier
          .widthIn(max = 720.dp)
          .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        item {
          Text(
            text = "ساحة الاختبارات والتحدّي",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
          )
          Text(
            text = "اختبر فهمك للحروف، القواعد، والمفردات واحصل على نقاط XP",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }

        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onStartQuiz(null) }
              .testTag("start_quiz_mixed"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "الاختبار الشامل السريع",
                  style = MaterialTheme.typography.titleLarge,
                  color = Color.White,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                  text = "٦ أسئلة متنوّعة من الحروف حتى N3 لاختبار مستواك العام",
                  style = MaterialTheme.typography.bodyMedium,
                  color = Color.White.copy(alpha = 0.85f),
                )
              }
              Icon(
                imageVector = Icons.Rounded.Quiz,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(36.dp),
              )
            }
          }
        }

        items(JlptLevel.entries) { level ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onStartQuiz(level) }
              .testTag("start_quiz_${level.code}"),
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
                    text = "اختبار ${level.titleArabic}",
                    style = MaterialTheme.typography.titleMedium,
                  )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                  text = level.descriptionArabic,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
              Button(
                onClick = { onStartQuiz(level) },
                shape = RoundedCornerShape(12.dp),
              ) {
                Text("ابدأ")
              }
            }
          }
        }

        if (quizResultsState is UiState.Success && quizResultsState.data.isNotEmpty()) {
          item {
            Spacer(Modifier.height(6.dp))
            Text(
              text = "سجل اختباراتك الأخيرة",
              style = MaterialTheme.typography.titleLarge,
            )
          }
          items(quizResultsState.data, key = { it.id }) { res ->
            val pct = ((res.score * 100) / res.totalQuestions.coerceAtLeast(1L)).toInt()
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                Column {
                  Text(
                    text = "اختبار ${res.level}",
                    style = MaterialTheme.typography.titleMedium,
                  )
                  Text(
                    text = "النتيجة: ${res.score} من ${res.totalQuestions} ($pct%)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                }
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = MaterialTheme.colorScheme.tertiaryContainer,
                ) {
                  Text(
                    text = "+${res.xpEarned} XP",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ActiveQuizView(
  session: QuizSessionState,
  onSelectOption: (Int) -> Unit,
  onAdvanceQuiz: () -> Unit,
  onCloseQuiz: () -> Unit,
  onSpeak: (String) -> Unit,
) {
  Column(
    modifier = Modifier
      .widthIn(max = 640.dp)
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.SpaceBetween,
  ) {
    if (session.isFinished) {
      val total = session.questions.size.coerceAtLeast(1)
      val pct = (session.correctCount * 100) / total
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Icon(
            imageVector = Icons.Rounded.EmojiEvents,
            contentDescription = null,
            tint = Color(0xFFD99B26),
            modifier = Modifier.size(64.dp),
          )
          Spacer(Modifier.height(12.dp))
          Text(
            text = "انتهى الاختبار!",
            style = MaterialTheme.typography.headlineMedium,
          )
          Spacer(Modifier.height(6.dp))
          Text(
            text = "أجبتَ بشكل صحيح عن ${session.correctCount} من $total ($pct%)",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
          )
          if (session.lastXpEarned != null) {
            Spacer(Modifier.height(10.dp))
            Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.tertiaryContainer,
            ) {
              Text(
                text = "تم حفظ النتيجة • +${session.lastXpEarned} XP",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
              )
            }
          }
          Spacer(Modifier.height(22.dp))
          Button(
            onClick = onCloseQuiz,
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("finish_quiz_button"),
            shape = RoundedCornerShape(14.dp),
          ) {
            Text("العودة إلى الاختبارات")
          }
        }
      }
    } else {
      val q = session.questions[session.currentIndex]
      val progress = (session.currentIndex + 1).toFloat() / session.questions.size.toFloat()

      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = "السؤال ${session.currentIndex + 1} من ${session.questions.size}",
            style = MaterialTheme.typography.titleMedium,
          )
          OutlinedButton(
            onClick = onCloseQuiz,
            modifier = Modifier.testTag("quit_quiz_button"),
          ) {
            Icon(Icons.Rounded.Close, contentDescription = "إنهاء", modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text("خروج")
          }
        }

        LinearProgressIndicator(
          progress = { progress },
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(CircleShape),
        )

        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Text(
              text = q.promptArabic,
              style = MaterialTheme.typography.titleMedium,
              textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(12.dp))
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              JapaneseText(
                text = q.promptJapanese,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
              )
              TtsSpeakButton(onClick = { onSpeak(q.promptJapanese) })
            }
          }
        }

        q.options.forEachIndexed { idx, option ->
          val selected = session.selectedOptionIndex
          val isAnswered = selected != null
          val isCorrectOption = idx == q.correctIndex
          val isChosen = idx == selected

          val bg = when {
            !isAnswered -> MaterialTheme.colorScheme.surface
            isCorrectOption -> MaterialTheme.colorScheme.tertiaryContainer
            isChosen -> MaterialTheme.colorScheme.secondaryContainer
            else -> MaterialTheme.colorScheme.surface
          }
          val border = when {
            !isAnswered -> MaterialTheme.colorScheme.outlineVariant
            isCorrectOption -> MaterialTheme.colorScheme.tertiary
            isChosen -> MaterialTheme.colorScheme.secondary
            else -> MaterialTheme.colorScheme.outlineVariant
          }

          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .clickable(enabled = !isAnswered) { onSelectOption(idx) }
              .testTag("quiz_option_$idx"),
            shape = RoundedCornerShape(14.dp),
            color = bg,
            border = BorderStroke(1.5.dp, border),
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Text(
                text = option,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
              )
              if (isAnswered && isCorrectOption) {
                Icon(
                  imageVector = Icons.Rounded.CheckCircle,
                  contentDescription = "إجابة صحيحة",
                  tint = MaterialTheme.colorScheme.tertiary,
                )
              }
            }
          }
        }

        if (session.selectedOptionIndex != null) {
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
          ) {
            Text(
              text = q.explanationArabic,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              modifier = Modifier.padding(14.dp),
            )
          }
        }
      }

      if (session.selectedOptionIndex != null) {
        Button(
          onClick = onAdvanceQuiz,
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("next_quiz_question_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
          ),
        ) {
          Text(
            text = if (session.currentIndex + 1 < session.questions.size) "السؤال التالي" else "عرض النتيجة",
            style = MaterialTheme.typography.titleMedium,
          )
        }
      }
    }
  }
}
