package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.BookmarkAdded
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.curriculum.KanaChar
import com.example.data.curriculum.KanaData
import com.example.data.model.JlptLevel
import com.example.ui.components.JapaneseText
import com.example.ui.components.KanaDrawingCanvas
import com.example.ui.components.TtsSpeakButton

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun KanaDojoScreen(
  savedFlashcardIds: Set<String>,
  onSpeak: (String) -> Unit,
  onBookmarkKana: (KanaChar) -> Unit,
  onBack: () -> Unit,
) {
  BackHandler(onBack = onBack)

  var selectedTab by remember { mutableIntStateOf(0) } // 0 = Hiragana, 1 = Katakana
  val activeList = remember(selectedTab) {
    if (selectedTab == 0) KanaData.hiraganaList else KanaData.katakanaList
  }
  var selectedChar by remember(selectedTab) { mutableStateOf(activeList.first()) }
  val groupedRows = remember(activeList) { activeList.groupBy { it.rowTitle } }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("مختبر الحروف (Kana Dojo)") },
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("kana_back_button"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
              contentDescription = "رجوع",
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.background,
        ),
      )
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
          Tab(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            text = { Text("هيراغانا (ひらがな)") },
            modifier = Modifier.testTag("tab_hiragana"),
          )
          Tab(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            text = { Text("كاتاكانا (カタカナ)") },
            modifier = Modifier.testTag("tab_katakana"),
          )
        }

        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          // Selected character interactive studio + drawing pad
          item {
            val isSaved = savedFlashcardIds.contains(selectedChar.id)
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(22.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
              Column(modifier = Modifier.padding(18.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                  ) {
                    Surface(
                      shape = RoundedCornerShape(16.dp),
                      color = MaterialTheme.colorScheme.primaryContainer,
                      modifier = Modifier.size(72.dp),
                    ) {
                      Box(contentAlignment = Alignment.Center) {
                        JapaneseText(
                          text = selectedChar.symbol,
                          fontSize = 38.sp,
                          fontWeight = FontWeight.Bold,
                          color = MaterialTheme.colorScheme.primary,
                        )
                      }
                    }
                    Column {
                      Text(
                        text = "${selectedChar.romaji}  •  يُنطق: ${selectedChar.arabicPhonetic}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                      )
                      Text(
                        text = selectedChar.rowTitle,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary,
                      )
                    }
                  }

                  Row {
                    TtsSpeakButton(onClick = { onSpeak(selectedChar.symbol) })
                    IconButton(
                      onClick = { onBookmarkKana(selectedChar) },
                      modifier = Modifier
                        .size(48.dp)
                        .testTag("bookmark_kana_button"),
                    ) {
                      Icon(
                        imageVector = if (isSaved) Icons.Rounded.BookmarkAdded else Icons.Rounded.BookmarkAdd,
                        contentDescription = "إضافة للمراجعة",
                        tint = if (isSaved) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary,
                      )
                    }
                  }
                }

                Spacer(Modifier.height(10.dp))
                Surface(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(12.dp),
                  color = MaterialTheme.colorScheme.surfaceVariant,
                ) {
                  Text(
                    text = "حيلة للتذكّر: ${selectedChar.mnemonicArabic}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(12.dp),
                  )
                }

                Spacer(Modifier.height(14.dp))
                KanaDrawingCanvas(guideChar = selectedChar.symbol)
              }
            }
          }

          // Interactive Kana Grid by rows
          groupedRows.forEach { (rowTitle, chars) ->
            item(key = rowTitle) {
              Column {
                Text(
                  text = rowTitle,
                  style = MaterialTheme.typography.titleMedium,
                  color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(8.dp))
                FlowRow(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                  chars.forEach { ch ->
                    val isSelected = ch.id == selectedChar.id
                    Surface(
                      modifier = Modifier
                        .size(width = 64.dp, height = 70.dp)
                        .clickable {
                          selectedChar = ch
                          onSpeak(ch.symbol)
                        }
                        .testTag("kana_cell_${ch.id}"),
                      shape = RoundedCornerShape(14.dp),
                      color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                      } else {
                        MaterialTheme.colorScheme.surface
                      },
                      border = BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) {
                          MaterialTheme.colorScheme.primary
                        } else {
                          MaterialTheme.colorScheme.outlineVariant
                        },
                      ),
                    ) {
                      Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                      ) {
                        JapaneseText(
                          text = ch.symbol,
                          fontSize = 24.sp,
                          fontWeight = FontWeight.Bold,
                          color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                          } else {
                            MaterialTheme.colorScheme.onSurface
                          },
                        )
                        Text(
                          text = ch.romaji,
                          style = MaterialTheme.typography.labelSmall,
                          color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                          } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                          },
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
}
