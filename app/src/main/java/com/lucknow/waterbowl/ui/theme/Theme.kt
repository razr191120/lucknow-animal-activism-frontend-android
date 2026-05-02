package com.lucknow.waterbowl.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = WaterBluePrimary,
    onPrimary = Color.White,
    primaryContainer = WaterBlueLight,
    onPrimaryContainer = WaterBlueDark,
    secondary = NatureGreenPrimary,
    onSecondary = Color.White,
    secondaryContainer = NatureGreenLight,
    onSecondaryContainer = NatureGreenDark,
    tertiary = NatureGreenLight,
    onTertiary = Color.White,
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    surface = Color.White,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    error = ErrorColor,
    onError = OnErrorColor,
    errorContainer = ErrorContainer,
)

private val DarkColorScheme = darkColorScheme(
    primary = WaterBlueLight,
    onPrimary = WaterBlueDark,
    primaryContainer = WaterBluePrimary,
    onPrimaryContainer = WaterBlueSurface,
    secondary = NatureGreenLight,
    onSecondary = NatureGreenDark,
    secondaryContainer = NatureGreenPrimary,
    onSecondaryContainer = NatureGreenSurface,
    background = Color(0xFF1A1C1E),
    onBackground = Color(0xFFE2E2E6),
    surface = Color(0xFF1A1C1E),
    onSurface = Color(0xFFE2E2E6),
)

@Composable
fun WaterBowlTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
