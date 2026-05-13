package com.rkbapps.canvas.navigation
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
) {
    NavHost(navController = navController, startDestination = Home) {
        composable<Home> {
            HomeScreen(
                navController = navController,
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

