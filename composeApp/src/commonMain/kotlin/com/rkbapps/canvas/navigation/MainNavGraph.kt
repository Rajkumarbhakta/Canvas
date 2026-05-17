package com.rkbapps.canvas.navigation
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rkbapps.canvas.ui.screens.drawing.DrawingScreen
import com.rkbapps.canvas.ui.screens.home.HomeScreen
import com.rkbapps.canvas.ui.screens.settings.SettingsScreen

@Composable
fun MainNavGraph(
    navController: NavHostController,
    windowSizeClass: WindowSizeClass
) {
    NavHost(navController = navController, startDestination = Home) {
        composable<Home> {
            HomeScreen(
                navController = navController,
                windowSizeClass = windowSizeClass
            )
        }
        composable<Draw> {
            DrawingScreen(navController = navController)
        }

        composable<Settings> {
            SettingsScreen(navController = navController)
        }

    }
}

