package com.rkbapps.canvas

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rkbapps.canvas.navigation.MainNavGraph
import com.rkbapps.canvas.theme.AppTheme
import org.koin.compose.KoinContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf



@Composable
internal fun App(navController: NavHostController = rememberNavController()) {
    AppTheme(darkTheme = false) {
        MainNavGraph(navController = navController,)
    }
}









