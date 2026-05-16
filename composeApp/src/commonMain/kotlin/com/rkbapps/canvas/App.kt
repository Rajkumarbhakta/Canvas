package com.rkbapps.canvas

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rkbapps.canvas.navigation.MainNavGraph
import com.rkbapps.canvas.ui.screens.settings.SettingsViewModel
import com.rkbapps.canvas.ui.theme.AppTheme
import com.rkbapps.canvas.util.Platforms
import com.rkbapps.canvas.util.getPlatform
import io.ktor.util.Platform
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun App(navController: NavHostController = rememberNavController()) {
    val darkTheme = if (getPlatform() == Platforms.WEB){
        isSystemInDarkTheme()
    }else{
        val viewModel: SettingsViewModel = koinViewModel()
        val isSystemTheme by viewModel.isSystemTheme.collectAsStateWithLifecycle()
        val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
        if (isSystemTheme) isSystemInDarkTheme() else isDarkTheme
    }

    AppTheme(darkTheme = darkTheme) {
        MainNavGraph(navController = navController,)
    }
}
