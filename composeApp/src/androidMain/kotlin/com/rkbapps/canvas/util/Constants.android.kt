package com.rkbapps.canvas.util

import androidx.activity.compose.LocalActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

actual fun getPlatform(): Platforms = Platforms.ANDROID

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
actual fun getWindowSize(): WindowSizeClass {
    val activity = LocalActivity.current
    return calculateWindowSizeClass(activity = activity!!)
}