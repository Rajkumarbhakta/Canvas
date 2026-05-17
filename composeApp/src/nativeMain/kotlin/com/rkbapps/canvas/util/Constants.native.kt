package com.rkbapps.canvas.util

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import platform.Foundation.NSBundle

actual fun getPlatform(): Platforms = Platforms.IOS

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
actual fun getWindowSize(): WindowSizeClass = calculateWindowSizeClass()

actual fun getAppVersion(): String {
    val version = NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String
    return version ?: "Unknown"
}