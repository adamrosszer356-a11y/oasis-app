// ui/theme/OasisTheme.kt
package com.example.oasisapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightPrimary = Color(0xFF2E7D32)  // zöld
private val LightSecondary = Color(0xFF80CBC4)
private val LightBackground = Color(0xFFF5F7F4)
private val LightSurface = Color(0xFFFFFFFF)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = Color.White,
    secondary = LightSecondary,
    onSecondary = Color.Black,
    background = LightBackground,
    onBackground = Color(0xFF1B1B1B),
    surface = LightSurface,
    onSurface = Color(0xFF1B1B1B),
)

@Composable
fun OasisTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Itt egyszerűen mindenképp a világos palettát használjuk
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography(),
        content = content
    )
}
