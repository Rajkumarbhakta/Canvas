package com.rkbapps.canvas.ui.screens.drawing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
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
    val currentDesign = viewModel.currentDesign.collectAsStateWithLifecycle()

    val isEditDrawingNameDialogVisible = remember { mutableStateOf(false) }

    val isEraserSelected = remember { mutableStateOf(false) }

    val isFullScreen = remember { mutableStateOf(false) }

    val requester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        requester.requestFocus()
    }

    Scaffold(
        modifier = Modifier.onKeyEvent {
                if ((it.isCtrlPressed || it.isMetaPressed) && it.key == Key.S) {
                    viewModel.onAction(DrawingAction.SaveDesign(state.value, currentDesign.value.name))
                    true
                }
                false
            }.focusRequester(requester)
            .focusable(),

        topBar = {
            AnimatedVisibility(
                visible = !isFullScreen.value
            ) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(currentDesign.value.name)
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
                                viewModel.onAction(DrawingAction.SaveDesign(state.value,
                                    currentDesign.value.name
                                ))
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Save, contentDescription = "save drawing")
                        }
                    }
                )
            }
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(it)
                .padding(vertical = 10.dp)

            ,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (isEditDrawingNameDialogVisible.value) {
                EditDrawingNameDialog(
                    initialName = currentDesign.value.name,
                    onCanceled = {
                        isEditDrawingNameDialogVisible.value = false
                    }
                ) { name ->
                    viewModel.updateDrawingName(name)
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

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                val rotation = animateFloatAsState(
                    targetValue = if (isFullScreen.value) 0f else 180f,
                    label = "ArrowRotation"
                )
                Icon(imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "",
                    modifier = Modifier.rotate(rotation.value).clickable {
                        isFullScreen.value = !isFullScreen.value
                    }
                )
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    Spacer(Modifier.width(16.dp))
                }
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
                item {
                    Spacer(Modifier.width(16.dp))
                }
            }
            AnimatedVisibility(visible = !isFullScreen.value) {
                ThicknessManagement(
                    value = state.value.selectedThickness
                ) {
                    viewModel.onAction(DrawingAction.OnThicknessChange(it))
                }

            }
            AnimatedVisibility(visible = !isFullScreen.value,
                modifier = Modifier.fillMaxWidth(0.6f).align(Alignment.CenterHorizontally),
            ) {
                Button(
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
}




