package com.rkbapps.canvas.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicColorScheme



/** The app's colour scheme for a seed [color]; also used to preview themes in settings. */
@Composable
internal fun appColorScheme(color: Color, darkTheme: Boolean): ColorScheme =
    rememberDynamicColorScheme(
        primary = color,
        isDark = darkTheme,
        specVersion = ColorSpec.SpecVersion.SPEC_2025,
    )

@Composable
internal fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    color: Color = defaultColor,
    content: @Composable () -> Unit
) {
    MaterialTheme (
        colorScheme = appColorScheme(color, darkTheme),
        content = { Surface(content = content) },
        typography = typography()
    )
}


