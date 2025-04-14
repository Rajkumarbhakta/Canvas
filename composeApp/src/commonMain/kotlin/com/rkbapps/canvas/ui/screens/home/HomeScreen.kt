package com.rkbapps.canvas.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
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
fun HomeScreen(navController: NavHostController, viewModel: HomeViewModel = koinViewModel()) {

    val allDesign = viewModel.allDesign.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("My Designs")
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
                navController.navigate(route = Draw())
            }) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Brush, contentDescription = "new drawing")
                    Text(text = "Draw")
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(it)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (allDesign.value.designs.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(200.dp),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = allDesign.value.designs) {
                        DesignListItem(it, onDelete = {
                            viewModel.deleteDesign(it.id)
                        }) {
                            navController.navigate(route = Draw(it.id))
                        }
                    }
                }
            } else {
                Text("No Designs")
            }
        }
    }
}


@Composable
fun DesignListItem(design: SavedDesign, onDelete: () -> Unit = {}, onClick: () -> Unit = {}) {
    Card(modifier = Modifier.padding(8.dp), onClick = onClick) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .height(200.dp).fillMaxWidth()
                    .clip(shape = RoundedCornerShape(16.dp))
                    .align(Alignment.CenterHorizontally)
            ) {
                DrawingShow(
                    state = design.state,
                    modifier = Modifier.height(200.dp).fillMaxWidth().clipToBounds()
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    design.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "delete",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun DrawingShow(
    state: DrawingState,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.background(state.backgroundColor)
    ) {
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