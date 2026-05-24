package com.rkbapps.canvas.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.LocalActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import org.koin.core.context.GlobalContext

actual fun getPlatform(): Platforms = Platforms.ANDROID

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
actual fun getWindowSize(): WindowSizeClass {
    val activity = LocalActivity.current
    return calculateWindowSizeClass(activity = activity!!)
}

actual fun getAppVersion(): String {
    return try {
        val context = GlobalContext.get().get<Context>()
        val packageManager = context.packageManager
        val packageName = context.packageName
        val packageInfo = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(packageName, 0)
        }
        packageInfo.versionName ?: "Unknown"
    } catch (e: Exception) {
        "Unknown"
    }
}