package com.rkbapps.canvas

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rkbapps.canvas.navigation.MainNavGraph
import com.rkbapps.canvas.theme.AppTheme
import org.koin.compose.KoinContext


@Composable
internal fun App(navController: NavHostController = rememberNavController()) = AppTheme(
    darkTheme = isSystemInDarkTheme()
) {
    KoinContext{
        MainNavGraph(navController = navController)
    }
}




