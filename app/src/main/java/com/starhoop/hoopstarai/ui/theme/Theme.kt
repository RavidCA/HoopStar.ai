package com.starhoop.hoopstar.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val HoopDarkColors = darkColorScheme(
    primary = HoopOrange,
    onPrimary = Color.Black,
    primaryContainer = HoopOrangeDark,
    onPrimaryContainer = Color.White,
    secondary = HoopOrangeLight,
    onSecondary = Color.Black,
    background = Charcoal,
    onBackground = TextPrimary,
    surface = CharcoalElevated,
    onSurface = TextPrimary,
    surfaceVariant = CharcoalCard,
    onSurfaceVariant = TextSecondary,
    outline = OutlineGray,
    error = ErrorRed,
    onError = Color.White
)

private val HoopLightColors = lightColorScheme(
    primary = HoopOrangeDark,
    onPrimary = Color.White,
    background = Color(0xFFF7F7FA),
    surface = Color.White,
    error = ErrorRed
)

@Composable
fun HoopStarTheme(
    darkTheme: Boolean = true, // dark-first
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) HoopDarkColors else HoopLightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}