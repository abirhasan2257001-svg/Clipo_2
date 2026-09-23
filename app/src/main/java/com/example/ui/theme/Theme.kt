package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CapCutColorScheme = darkColorScheme(
  primary = CapCutCyan,
  onPrimary = CapCutDarkBg,
  primaryContainer = CapCutSurfaceVariant,
  onPrimaryContainer = CapCutCyan,
  secondary = CapCutYellow,
  onSecondary = CapCutDarkBg,
  secondaryContainer = CapCutSurface,
  onSecondaryContainer = CapCutYellow,
  background = CapCutDarkBg,
  onBackground = CapCutTextPrimary,
  surface = CapCutPanelBg,
  onSurface = CapCutTextPrimary,
  surfaceVariant = CapCutSurfaceVariant,
  onSurfaceVariant = CapCutTextSecondary,
  outline = CapCutBorder
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = CapCutColorScheme,
    typography = Typography,
    content = content
  )
}
