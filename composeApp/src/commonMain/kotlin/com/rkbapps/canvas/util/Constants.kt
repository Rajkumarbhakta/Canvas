package com.rkbapps.canvas.util

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object Constants {
    val colors = listOf(
        Color.Black,
        Color.White,
        Color.Blue,
        Color.Cyan,
        Color.Red,
        Color.Green,
        Color.Magenta,
        Color.Yellow,
        Color.Gray,
    )
}

enum class Platforms{
    ANDROID,
    IOS,
    WEB,
    DESKTOP
}

expect fun getPlatform(): Platforms

@Composable
expect fun getWindowSize(): WindowSizeClass
