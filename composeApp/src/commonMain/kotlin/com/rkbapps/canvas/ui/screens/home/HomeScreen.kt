package com.rkbapps.canvas.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.navigation.Draw
import com.rkbapps.canvas.ui.composables.DrawingCanvas
import com.rkbapps.canvas.ui.composables.drawPath
import com.rkbapps.canvas.ui.screens.drawing.DrawingAction
import com.rkbapps.canvas.util.json
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController,viewModel: HomeViewModel = koinViewModel()){

    val allDesign = viewModel.allDesign.collectAsStateWithLifecycle()



    Scaffold(
        topBar = {
            TopAppBar(
                title = {

                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.getAllDesign()
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(route = Draw)
            }) {
                Text(text = "Draw")
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(it).padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(allDesign.value.designs.isNotEmpty()){
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(200.dp),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = allDesign.value.designs) {
                        DesignListItem(it){
                            val json = json.encodeToString(SavedDesign.serializer(),it)
                            navController.navigate(route = Draw(json))
                        }
                    }
                }
            }else{
                Text("No Designs")
            }
        }
    }
}


@Composable
fun DesignListItem(design: SavedDesign,onClick:()->Unit = {}){
    Card(modifier = Modifier.padding(8.dp), onClick = onClick) {
        Box(modifier = Modifier.height(200.dp).width(150.dp)){
            DrawingShow(state = design.state)
        }
        Text(design.name)
    }
}


@Composable
fun DrawingShow(
    state: DrawingState
){
    Canvas(
        modifier = Modifier
            .clipToBounds()
            .background(state.backgroundColor)
    ){
        state.paths.fastForEach {
            drawPath(
                it.path,
                it.color,
                it.thickness,
                it.pathEffect,
                isEraser = it.isEraser,
                state.backgroundColor
            )
        }
        state.currentPath?.let {
            drawPath(
                it.path,
                it.color,
                it.thickness,
                it.pathEffect,
                isEraser = it.isEraser,
                state.backgroundColor
            )
        }
    }
}