package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Flip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.FlashcardProgress
import com.example.data.model.JlptLevel
import com.example.data.model.SrsQuality
import com.example.data.model.UiState
import com.example.ui.components.HankoLevelBadge
import com.example.ui.components.JapaneseText
import com.example.ui.components.TtsSpeakButton

@Composable
fun FlashcardsScreen(
  flashcardsState: UiState<List<FlashcardProgress>>,
  isBusy: Boolean,
  onSeedDeck: (JlptLevel) -> Unit,
  onReviewCard: (FlashcardProgress, SrsQuality) -> Unit,
  onDeleteCard: (String) -> Unit,
  onSpeak: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  var isFlipped by remember { mutableStateOf(false) }

  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.TopCenter,
  ) {
    when (flashcardsState) {
      is UiState.Loading -> {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          CircularProgressIndicator()
        }
      }
      is UiState.Error -> {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            text = flashcardsState.message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center,
          )
        }
      }
      is UiState.Success -> {
        val cards = flashcardsState.data
        val nowMs = System.currentTimeMillis()
        val dueCards = remember(cards, nowMs) { cards.filter { it.nextReviewAtMs <= nowMs } }
        val activeCard = dueCards.firstOrNull()

        LazyColumn(
          modifier = Modifier
            .widthIn(max = 720.dp)
            .fillMaxSize(),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          item {
            Text(
              text = "البطاقات الذكية (SRS)",
              style = MaterialTheme.typography.headlineMedium,
              color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
              text = "راجع المفردات والكانجي حسب خوارزمية التكرار المتباعد",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }

          if (cards.isEmpty()) {
            item {
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              ) {
                Column(
                  modifier = Modifier.padding(18.dp),
                  horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                  Image(
                    painter = painterResource(id = R.drawable.img_study_dojo),
                    contentDescription = "مكتب الدراسة الياباني",
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(170.dp)
                      .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop,
                  )
                  Spacer(Modifier.height(16.dp))
                  Text(
                    text = "مجموعتك فارغة حالياً",
                    style = MaterialTheme.typography.titleLarge,
                  )
                  Spacer(Modifier.height(6.dp))
                  Text(
                    text = "أضف كلمات من الدروس أو حمّل حزمة بداية جاهزة الآن لتبدأ المراجعة اليومية:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                  )
                  Spacer(Modifier.height(14.dp))
                  SeedDeckRow(isBusy = isBusy, onSeedDeck = onSeedDeck)
                }
              }
            }
          } else {
            // Active review card or "All caught up!" banner
            item {
              if (activeCard != null) {
                ReviewCardBox(
                  card = activeCard,
                  dueRemaining = dueCards.size,
                  isFlipped = isFlipped,
                  onToggleFlip = { isFlipped = !isFlipped },
                  onSpeak = { onSpeak(activeCard.japanese) },
                  onRate = { quality ->
                    isFlipped = false
                    onReviewCard(activeCard, quality)
                  },
                )
              } else {
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(22.dp),
                  colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f),
                  ),
                ) {
                  Column(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                  ) {
                    Icon(
                      imageVector = Icons.Rounded.CheckCircle,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.tertiary,
                      modifier = Modifier.size(44.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                      text = "أتممتَ مراجعات اليوم بالكامل!",
                      style = MaterialTheme.typography.titleLarge,
                      color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                      text = "يمكنك تحميل بطاقات مستوى جديد أو إضافة مفردات من المسار التعليمي.",
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                      textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(14.dp))
                    SeedDeckRow(isBusy = isBusy, onSeedDeck = onSeedDeck)
                  }
                }
              }
            }

            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Text(
                  text = "البطاقات المحفوظة (${cards.size})",
                  style = MaterialTheme.typography.titleLarge,
                )
                if (activeCard != null) {
                  Text(
                    text = "مستحق الآن: ${dueCards.size}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                  )
                }
              }
            }

            items(cards, key = { it.itemId.ifBlank { it.id } }) { card ->
              val isDue = card.nextReviewAtMs <= nowMs
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
                  Column(modifier = Modifier.weight(1f)) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                      JapaneseText(
                        text = card.japanese,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                      )
                      HankoLevelBadge(level = JlptLevel.fromRaw(card.level))
                    }
                    Text(
                      text = card.arabicMeaning,
                      style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                      text = if (isDue) "مستحقة للمراجعة الآن" else "المراجعة القادمة بعد ${card.intervalDays} يوم • صندوق ${card.box}",
                      style = MaterialTheme.typography.labelMedium,
                      color = if (isDue) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary,
                    )
                  }
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    TtsSpeakButton(onClick = { onSpeak(card.japanese) })
                    IconButton(
                      onClick = { onDeleteCard(card.itemId.ifBlank { card.id }) },
                      modifier = Modifier
                        .size(48.dp)
                        .testTag("delete_card_${card.itemId}"),
                    ) {
                      Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = "حذف البطاقة",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
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
  }
}

@Composable
private fun ReviewCardBox(
  card: FlashcardProgress,
  dueRemaining: Int,
  isFlipped: Boolean,
  onToggleFlip: () -> Unit,
  onSpeak: () -> Unit,
  onRate: (SrsQuality) -> Unit,
) {
  val rotation by animateFloatAsState(
    targetValue = if (isFlipped) 180f else 0f,
    animationSpec = spring(dampingRatio = 0.8f, stiffness = 360f),
    label = "cardFlip",
  )

  Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .height(250.dp)
        .graphicsLayer {
          rotationY = rotation
          cameraDistance = 12f * density
        }
        .clickable(onClick = onToggleFlip)
        .testTag("flashcard_flip_surface"),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
      if (rotation <= 90f) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            HankoLevelBadge(level = JlptLevel.fromRaw(card.level))
            Text(
              text = "متبقي للمراجعة: $dueRemaining",
              style = MaterialTheme.typography.labelLarge,
              color = MaterialTheme.colorScheme.secondary,
            )
          }
          Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            JapaneseText(
              text = card.japanese,
              fontSize = 44.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(10.dp))
            TtsSpeakButton(onClick = onSpeak)
          }
          Row(
            modifier = Modifier.align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            Icon(
              imageVector = Icons.Rounded.Flip,
              contentDescription = null,
              modifier = Modifier.size(16.dp),
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
              text = "اضغط لقلب البطاقة ورؤية المعنى",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }
      } else {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { rotationY = 180f }
            .padding(20.dp),
        ) {
          Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            JapaneseText(
              text = card.reading,
              fontSize = 20.sp,
              color = MaterialTheme.colorScheme.primary,
            )
            if (card.romaji.isNotBlank()) {
              Text(
                text = card.romaji,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
            Spacer(Modifier.height(14.dp))
            Text(
              text = card.arabicMeaning,
              style = MaterialTheme.typography.headlineMedium,
              color = MaterialTheme.colorScheme.onSurface,
              textAlign = TextAlign.Center,
            )
          }
        }
      }
    }

    if (isFlipped) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        SrsQuality.entries.forEach { q ->
          val btnColor = when (q) {
            SrsQuality.AGAIN -> MaterialTheme.colorScheme.secondary
            SrsQuality.HARD -> Color(0xFFD99B26)
            SrsQuality.GOOD -> MaterialTheme.colorScheme.primary
            SrsQuality.EASY -> MaterialTheme.colorScheme.tertiary
          }
          Button(
            onClick = { onRate(q) },
            modifier = Modifier
              .weight(1f)
              .height(54.dp)
              .testTag("srs_rate_${q.name}"),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = btnColor, contentColor = Color.White),
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = q.labelArabic,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
              )
              Text(
                text = q.hintArabic,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.85f),
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SeedDeckRow(
  isBusy: Boolean,
  onSeedDeck: (JlptLevel) -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .horizontalScroll(rememberScrollState()),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    JlptLevel.entries.forEach { level ->
      OutlinedButton(
        onClick = { onSeedDeck(level) },
        enabled = !isBusy,
        modifier = Modifier.testTag("seed_deck_${level.code}"),
        shape = RoundedCornerShape(12.dp),
      ) {
        Icon(
          imageVector = Icons.Rounded.AddCircleOutline,
          contentDescription = null,
          modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.width(6.dp))
        Text("حزمة ${level.code}")
      }
    }
  }
}
