package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val KotobaLightColorScheme = lightColorScheme(
  primary = AizomeIndigo,
  onPrimary = Color.White,
  primaryContainer = AizomeIndigoContainer,
  onPrimaryContainer = AizomeOnContainer,
  secondary = ShuiroVermilion,
  onSecondary = Color.White,
  secondaryContainer = ShuiroContainer,
  onSecondaryContainer = ShuiroOnContainer,
  tertiary = MatchaJade,
  onTertiary = Color.White,
  tertiaryContainer = MatchaContainer,
  onTertiaryContainer = MatchaOnContainer,
  background = WashiCream,
  onBackground = SumiInkText,
  surface = Color.White,
  onSurface = SumiInkText,
  surfaceVariant = TatamiBeige,
  onSurfaceVariant = SumiMutedText,
  surfaceContainerLow = WashiCream,
  surfaceContainer = TatamiBeige,
  surfaceContainerHigh = TatamiSurfaceVariant,
  outline = Color(0xFFD6CCBE),
  outlineVariant = Color(0xFFE7DFD3),
)

private val KotobaDarkColorScheme = darkColorScheme(
  primary = IndigoMoon,
  onPrimary = Color(0xFF0A1B33),
  primaryContainer = IndigoMoonContainer,
  onPrimaryContainer = Color(0xFFD9E4F5),
  secondary = LanternCoral,
  onSecondary = Color(0xFF3E0F0D),
  secondaryContainer = LanternCoralContainer,
  onSecondaryContainer = Color(0xFFFADBD9),
  tertiary = JadeNight,
  onTertiary = Color(0xFF092A1F),
  tertiaryContainer = JadeNightContainer,
  onTertiaryContainer = Color(0xFFD4EFE4),
  background = KyotoNightBg,
  onBackground = KyotoTextPrimary,
  surface = KyotoNightSurface,
  onSurface = KyotoTextPrimary,
  surfaceVariant = KyotoNightSurfaceHigh,
  onSurfaceVariant = KyotoTextMuted,
  surfaceContainerLow = KyotoNightBg,
  surfaceContainer = KyotoNightSurface,
  surfaceContainerHigh = KyotoNightSurfaceHigh,
  outline = Color(0xFF3A4254),
  outlineVariant = Color(0xFF2B3240),
)

val KotobaShapes = Shapes(
  small = RoundedCornerShape(10.dp),
  medium = RoundedCornerShape(16.dp),
  large = RoundedCornerShape(22.dp),
  extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun KotobaTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) KotobaDarkColorScheme else KotobaLightColorScheme
  MaterialTheme(
    colorScheme = colorScheme,
    typography = KotobaTypography,
    shapes = KotobaShapes,
    content = content,
  )
}
