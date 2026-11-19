package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JlptLevel
import com.example.ui.theme.NotoSansJpFontFamily

@Composable
fun JapaneseText(
  text: String,
  modifier: Modifier = Modifier,
  fontSize: TextUnit = 18.sp,
  fontWeight: FontWeight = FontWeight.Medium,
  color: Color = MaterialTheme.colorScheme.onSurface,
  textAlign: TextAlign? = null,
) {
  CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
    Text(
      text = text,
      modifier = modifier,
      style = TextStyle(
        fontFamily = NotoSansJpFontFamily,
        fontWeight = fontWeight,
        fontSize = fontSize,
        color = color,
      ),
      textAlign = textAlign,
    )
  }
}

@Composable
fun TtsSpeakButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  contentDescription: String = "استماع للنطق الياباني",
) {
  IconButton(
    onClick = onClick,
    modifier = modifier
      .size(48.dp)
      .testTag("tts_speak_button"),
    colors = IconButtonDefaults.iconButtonColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
      contentColor = MaterialTheme.colorScheme.primary,
    ),
  ) {
    Icon(
      imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
      contentDescription = contentDescription,
    )
  }
}

@Composable
fun HankoLevelBadge(
  level: JlptLevel,
  modifier: Modifier = Modifier,
) {
  val (bg, fg) = when (level) {
    JlptLevel.KANA -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.secondary
    JlptLevel.N5 -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.tertiary
    JlptLevel.N4 -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.primary
    JlptLevel.N3 -> MaterialTheme.colorScheme.secondary to Color.White
  }
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(10.dp),
    color = bg,
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      JapaneseText(
        text = level.badgeJapanese,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = fg,
      )
      Text(
        text = level.code,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = fg,
      )
    }
  }
}

@Composable
fun KanaDrawingCanvas(
  guideChar: String,
  modifier: Modifier = Modifier,
) {
  val completedStrokes = remember(guideChar) { mutableStateListOf<List<Offset>>() }
  var activeStroke by remember(guideChar) { mutableStateOf<List<Offset>>(emptyList()) }

  val guideColor = MaterialTheme.colorScheme.outline
  val inkColor = MaterialTheme.colorScheme.primary
  val watermarkColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f)

  Column(
    modifier = modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = "تدرّب على كتابة الحرف بالإصبع",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      TextButton(
        onClick = {
          completedStrokes.clear()
          activeStroke = emptyList()
        },
        modifier = Modifier.testTag("clear_drawing_button"),
      ) {
        Icon(
          imageVector = Icons.Rounded.Refresh,
          contentDescription = "مسح اللوحة",
          modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(4.dp))
        Text("مسح اللوحة")
      }
    }

    Spacer(Modifier.height(6.dp))

    Box(
      modifier = Modifier
        .size(210.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(MaterialTheme.colorScheme.surface)
        .border(
          width = 1.5.dp,
          color = MaterialTheme.colorScheme.outline,
          shape = RoundedCornerShape(20.dp),
        ),
      contentAlignment = Alignment.Center,
    ) {
      JapaneseText(
        text = guideChar,
        fontSize = 132.sp,
        fontWeight = FontWeight.Normal,
        color = watermarkColor,
      )

      Canvas(
        modifier = Modifier
          .fillMaxSize()
          .pointerInput(guideChar) {
            detectDragGestures(
              onDragStart = { offset ->
                activeStroke = listOf(offset)
              },
              onDrag = { change, _ ->
                change.consume()
                activeStroke = activeStroke + change.position
              },
              onDragEnd = {
                if (activeStroke.size > 1) {
                  completedStrokes.add(activeStroke)
                }
                activeStroke = emptyList()
              },
              onDragCancel = {
                activeStroke = emptyList()
              },
            )
          }
      ) {
        val dash = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
        drawLine(
          color = guideColor.copy(alpha = 0.65f),
          start = Offset(size.width / 2f, 0f),
          end = Offset(size.width / 2f, size.height),
          strokeWidth = 2f,
          pathEffect = dash,
        )
        drawLine(
          color = guideColor.copy(alpha = 0.65f),
          start = Offset(0f, size.height / 2f),
          end = Offset(size.width, size.height / 2f),
          strokeWidth = 2f,
          pathEffect = dash,
        )

        val strokeStyle = Stroke(
          width = 14f,
          cap = StrokeCap.Round,
          join = StrokeJoin.Round,
        )

        (completedStrokes + listOf(activeStroke)).forEach { points ->
          if (points.size >= 2) {
            val path = Path().apply {
              moveTo(points.first().x, points.first().y)
              for (i in 1 until points.size) {
                lineTo(points[i].x, points[i].y)
              }
            }
            drawPath(path = path, color = inkColor, style = strokeStyle)
          }
        }
      }
    }
  }
}

@Composable
fun StatPill(
  label: String,
  value: String,
  containerColor: Color,
  contentColor: Color,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier = modifier,
    shape = CircleShape,
    color = containerColor,
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      Text(
        text = value,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = contentColor,
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = contentColor.copy(alpha = 0.85f),
      )
    }
  }
}
