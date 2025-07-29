package com.rkbapps.canvas.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rkbapps.canvas.ui.screens.drawing.DrawingScreen
import com.rkbapps.canvas.ui.screens.home.HomeScreen

@Composable
fun MainNavGraph(
    navController: NavHostController,
    themeMode: String,
    onThemeChange: (String) -> Unit
) {
    NavHost(navController = navController, startDestination = Home) {
        composable<Home> {
            HomeScreen(
                navController = navController,
                themeMode = themeMode,
                onThemeChange = onThemeChange
            )
        }
        composable<Draw> {
            DrawingScreen(navController = navController)
        }
    }
}

