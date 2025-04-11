package com.rkbapps.canvas.util

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable

actual fun getPlatform(): Platforms = Platforms.DESKTOP

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
actual fun getWindowSize(): WindowSizeClass = calculateWindowSizeClass()