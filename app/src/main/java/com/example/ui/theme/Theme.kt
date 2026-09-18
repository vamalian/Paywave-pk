package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = PayWaveGreenBright,
    onPrimary = Color(0xFF003822),
    primaryContainer = PayWaveGreenDark,
    onPrimaryContainer = Color(0xFF7CFAB8),
    secondary = PayWaveCyan,
    onSecondary = Color(0xFF00363F),
    secondaryContainer = Color(0xFF004E5B),
    onSecondaryContainer = Color(0xFFA5EEFF),
    tertiary = PayWaveGoldLight,
    onTertiary = Color(0xFF452B00),
    background = NeutralDarkBg,
    onBackground = TextPrimaryLight,
    surface = NeutralDarkSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = PayWaveNavyCard,
    onSurfaceVariant = TextSecondaryLight,
    outline = Color(0xFF334155),
    error = StatusFailed
  )

private val LightColorScheme =
  lightColorScheme(
    primary = PayWaveGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCEF5E0),
    onPrimaryContainer = PayWaveGreenDark,
    secondary = PayWaveSapphire,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE2E8F0),
    onSecondaryContainer = PayWaveSapphire,
    tertiary = PayWaveGold,
    onTertiary = Color.White,
    background = NeutralLightBg,
    onBackground = TextPrimaryDark,
    surface = NeutralLightSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondaryDark,
    outline = NeutralLightCardBorder,
    error = StatusFailed
  )

@Composable
fun PayWaveTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun PayWavePKTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  PayWaveTheme(darkTheme = darkTheme, content = content)
}

// Retain alias for test compatibility
@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  PayWaveTheme(darkTheme = darkTheme, content = content)
}

