package com.rkbapps.canvas.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.rkbapps.canvas.navigation.Draw

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController){
    Scaffold(
        topBar = {
            TopAppBar(
                title = {

                }
            )
        }
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(it).padding(horizontal = 16.dp, vertical = 8.dp)) {
            Button(
                onClick = {
                    navController.navigate(route = Draw)
                }
            ) {
                Text("Start Drawing")
            }
        }
    }
}