package com.rkbapps.canvas.ui.screens.drawing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.rkbapps.canvas.ui.composables.DrawingCanvas
import com.rkbapps.canvas.ui.screens.drawing.composables.BackgroundColorChangeItem
import com.rkbapps.canvas.ui.screens.drawing.composables.ColorItemList
import com.rkbapps.canvas.ui.screens.drawing.composables.EditDrawingNameDialog
import com.rkbapps.canvas.ui.screens.drawing.composables.EraserItem
import com.rkbapps.canvas.ui.screens.drawing.composables.PaintingStyle
import com.rkbapps.canvas.ui.screens.drawing.composables.ThicknessManagement
import com.rkbapps.canvas.ui.screens.drawing.composables.UndoRedoItem
import com.rkbapps.canvas.util.Log
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingScreen(navController: NavHostController, viewModel: DrawingViewModel = koinViewModel()) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    val drawingName = remember { mutableStateOf("Untitled Drawing") }
    val isEditDrawingNameDialogVisible = remember { mutableStateOf(false) }

    val isEraserSelected = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(drawingName.value)
                        IconButton(onClick = {
                            isEditDrawingNameDialogVisible.value = true
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "edit name")
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.onAction(DrawingAction.OnClearCanvasList)
                            navController.navigateUp()
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.onAction(DrawingAction.SaveDesign(state.value,drawingName.value))
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = "save drawing")
                    }
                }
            )
        }
    ) {
        Column(
            Modifier.fillMaxSize().padding(it).padding(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (isEditDrawingNameDialogVisible.value) {
                EditDrawingNameDialog(
                    initialName = drawingName.value,
                    onCanceled = {
                        isEditDrawingNameDialogVisible.value = false
                    }
                ) { name ->
                    drawingName.value = name
                    isEditDrawingNameDialogVisible.value = false
                }
            }

            DrawingCanvas(
                paths = state.value.paths,
                currentPath = state.value.currentPath,
                onAction = viewModel::onAction,
                modifier = Modifier.fillMaxWidth().weight(1f),
                backgroundColor = state.value.backgroundColor
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    BackgroundColorChangeItem {
                        viewModel.onAction(DrawingAction.OnBackgroundColorChange(it))
                    }
                }

                item {
                    ColorItemList(
                        selectedColor = state.value.selectedColor,
                    ) { color ->
                        isEraserSelected.value = false
                        viewModel.onAction(DrawingAction.OnToggleEraser(false))
                        viewModel.onAction(DrawingAction.OnSelectColor(color))
                    }
                }

                item {
                    EraserItem(
                        isEraserSelected = isEraserSelected.value
                    ) {
                        isEraserSelected.value = true
                        viewModel.onAction(DrawingAction.OnToggleEraser(true))
                    }
                }

                item {
                    PaintingStyle(
                        selected = state.value.selectedPathEffect,
                    ) {
                        Log.d("Path Effect", it)
                        viewModel.onAction(DrawingAction.OnPathEffectChange(it))
                    }
                }

                item {
                    UndoRedoItem(
                        onUndo = {
                            viewModel.onAction(DrawingAction.OnUndo)
                        },
                        onRedo = {
                            viewModel.onAction(DrawingAction.OnRedo)
                        }
                    )
                }
            }
            ThicknessManagement(
                value = state.value.selectedThickness
            ) {
                viewModel.onAction(DrawingAction.OnThicknessChange(it))
            }
            Button(
                modifier = Modifier.fillMaxWidth(0.6f).align(Alignment.CenterHorizontally),
                onClick = {
                    Log.d("Clear Canvas", "Clicked")
                    viewModel.onAction(DrawingAction.OnClearCanvasList)
                }
            ) {
                Text("Clear Canvas")
            }

        }
    }
}




