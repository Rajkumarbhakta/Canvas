package com.rkbapps.canvas

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rkbapps.canvas.navigation.MainNavGraph
import com.rkbapps.canvas.ui.theme.AppTheme


@Composable
internal fun App(navController: NavHostController = rememberNavController()) {
    AppTheme(darkTheme = false) {
        MainNavGraph(navController = navController,)
    }
}









