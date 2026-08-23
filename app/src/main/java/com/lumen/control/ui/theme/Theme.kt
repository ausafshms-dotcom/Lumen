package com.lumen.control.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val LumenColorScheme = darkColorScheme(
    primary = AccentStart,
    secondary = AccentEnd,
    background = Background,
    surface = Surface,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = Danger
)

@Composable
fun LumenTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LumenColorScheme,
        typography = MaterialTheme.typography.copy(
            headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 26.sp, color = TextPrimary),
            titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = TextPrimary),
            bodyMedium = TextStyle(fontSize = 14.sp, color = TextSecondary)
        ),
        content = content
    )
}
