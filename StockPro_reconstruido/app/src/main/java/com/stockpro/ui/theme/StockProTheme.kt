package com.stockpro.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val StockProColors = darkColorScheme(
    primary = Color(0xFFB6F36A), onPrimary = Color(0xFF17210F),
    secondary = Color(0xFF86D94A), onSecondary = Color(0xFF10200B),
    background = Color(0xFF101713), onBackground = Color(0xFFF0F4EF),
    surface = Color(0xFF18211B), onSurface = Color(0xFFF0F4EF),
    surfaceVariant = Color(0xFF243129), onSurfaceVariant = Color(0xFFB8C5B9),
    error = Color(0xFFFF8A80)
)

@Composable fun StockProTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = StockProColors, content = content)
}
