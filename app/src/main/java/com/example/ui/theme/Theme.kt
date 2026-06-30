package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = GoogleBlue,
    secondary = Color(0xFFC2E7FF),
    tertiary = GoogleGreen,
    background = Color(0xFF12141C),
    surface = Color(0xFF1E212B),
    onBackground = Color(0xFFE3E2E6),
    onSurface = Color(0xFFE3E2E6)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = GoogleBlue,
    secondary = Color(0xFFC2E7FF),
    tertiary = GoogleGreen,
    background = Color(0xFFF8F9FA),
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1F1F1F),
    onSurface = Color(0xFF1F1F1F),
    outline = Color(0xFFDADCE0)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Force false for dynamic color to preserve the high-fidelity branded Vibrant Palette styling
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
