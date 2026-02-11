package com.itd.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ITDDarkColorScheme = darkColorScheme(
    primary = ITDPrimary,
    onPrimary = ITDOnSurface,
    primaryContainer = ITDPrimaryVariant,
    secondary = ITDAccent,
    onSecondary = ITDOnSurface,
    background = ITDBackground,
    onBackground = ITDOnBackground,
    surface = ITDSurface,
    onSurface = ITDOnSurface,
    surfaceVariant = ITDSurfaceVariant,
    onSurfaceVariant = ITDOnSurfaceVariant,
    outline = ITDDivider,
    error = ITDError,
    onError = ITDOnSurface
)

@Composable
fun ITDTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = ITDBackground.toArgb()
            window.navigationBarColor = ITDBottomBar.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = ITDDarkColorScheme,
        typography = ITDTypography,
        content = content
    )
}
