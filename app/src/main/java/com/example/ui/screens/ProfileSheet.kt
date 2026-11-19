package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.JlptLevel
import com.example.data.model.UserProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSheet(
  profile: UserProfile?,
  onUpdatePreferences: (JlptLevel, Long) -> Unit,
  onSignOut: () -> Unit,
  onDismiss: () -> Unit,
) {
  var selectedTarget by remember(profile?.targetLevel) {
    mutableStateOf(JlptLevel.fromRaw(profile?.targetLevel))
  }
  var selectedGoal by remember(profile?.dailyGoal) {
    mutableLongStateOf(profile?.dailyGoal ?: 10L)
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor = MaterialTheme.colorScheme.surface,
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Text(
        text = "حساب المتعلّم: ${profile?.displayName ?: "متعلّم كوتوبا"}",
        style = MaterialTheme.typography.headlineMedium,
      )
      Text(
        text = "مجموع النقاط: ${profile?.xp ?: 0L} XP  •  السلسلة: ${profile?.streakDays ?: 1L} يوم",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary,
      )

      HorizontalDivider()

      Text(
        text = "المستوى المستهدف (JLPT)",
        style = MaterialTheme.typography.titleMedium,
      )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        JlptLevel.entries.forEach { level ->
          FilterChip(
            selected = selectedTarget == level,
            onClick = { selectedTarget = level },
            label = { Text(level.code) },
            modifier = Modifier.testTag("pref_level_${level.code}"),
          )
        }
      }

      Text(
        text = "الهدف اليومي للبطاقات",
        style = MaterialTheme.typography.titleMedium,
      )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        listOf(5L, 10L, 20L).forEach { goal ->
          FilterChip(
            selected = selectedGoal == goal,
            onClick = { selectedGoal = goal },
            label = { Text("$goal بطاقات") },
            modifier = Modifier.testTag("pref_goal_$goal"),
          )
        }
      }

      Button(
        onClick = {
          onUpdatePreferences(selectedTarget, selectedGoal)
          onDismiss()
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("save_preferences_button"),
        shape = RoundedCornerShape(14.dp),
      ) {
        Text("حفظ التفضيلات")
      }

      OutlinedButton(
        onClick = onSignOut,
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("sign_out_button"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(
          contentColor = MaterialTheme.colorScheme.secondary,
        ),
      ) {
        Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text("تسجيل الخروج")
      }

      Spacer(Modifier.height(16.dp))
    }
  }
}
