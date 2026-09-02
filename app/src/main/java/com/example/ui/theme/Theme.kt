package com.example.ui.theme

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

private val PrimeSecondaryDark = Color(0xFF9EBBD8)

private val DarkColorScheme = darkColorScheme(
    primary = PrimePrimary,
    onPrimary = PrimeOnPrimary,
    primaryContainer = PrimePrimaryContainer,
    onPrimaryContainer = PrimeOnPrimaryContainer,
    secondary = PrimeSecondaryDark,
    onSecondary = PrimeTextPrimary,
    secondaryContainer = PrimeSurfaceContainerHigh,
    onSecondaryContainer = PrimePrimary,
    tertiary = PrimeGreen,
    onTertiary = PrimeOnPrimaryDark,
    background = PrimeBackground,
    onBackground = PrimeTextPrimary,
    surface = PrimeSurface,
    onSurface = PrimeTextPrimary,
    surfaceVariant = PrimeSurfaceVariant,
    onSurfaceVariant = PrimeTextSecondary,
    surfaceContainerHigh = PrimeSurfaceContainerHigh,
    outline = PrimeBorder,
    outlineVariant = PrimeBorder.copy(alpha = 0.3f),
    error = PrimeRed,
    onError = PrimeOnPrimaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = PrimeGoldDark,
    onPrimary = PrimeWhite,
    primaryContainer = PrimeLightSurfaceVariant,
    onPrimaryContainer = PrimeGoldDark,
    secondary = PrimeBlue,
    onSecondary = PrimeWhite,
    secondaryContainer = PrimeLightSurfaceVariant,
    onSecondaryContainer = PrimeBlue,
    tertiary = PrimeGreen,
    onTertiary = PrimeWhite,
    background = PrimeWhite,
    onBackground = PrimeTextPrimaryLight,
    surface = PrimeLightSurface,
    onSurface = PrimeTextPrimaryLight,
    surfaceVariant = PrimeLightSurfaceVariant,
    onSurfaceVariant = PrimeTextSecondaryLight,
    outline = PrimeBorderLight,
    error = PrimeRed,
    onError = PrimeWhite
)

@Composable
fun PrimeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
