package com.rkbapps.canvas

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rkbapps.canvas.navigation.MainNavGraph
import com.rkbapps.canvas.theme.AppTheme
import com.rkbapps.canvas.ui.screens.drawing.DrawingScreen
import org.koin.compose.KoinContext
import org.koin.core.context.startKoin


@Composable
internal fun App(navController: NavHostController = rememberNavController()) = AppTheme(
    darkTheme = false
) {
    KoinContext{
        MainNavGraph(navController = navController)
    }
}




