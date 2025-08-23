package com.rkbapps.canvas.ui.screens.drawing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.rkbapps.canvas.ui.composables.DrawingCanvas
import com.rkbapps.canvas.ui.screens.drawing.composables.BackgroundColorChangeItem
import com.rkbapps.canvas.ui.screens.drawing.composables.ColorItemList
import com.rkbapps.canvas.ui.screens.drawing.composables.EditDrawingNameDialog
import com.rkbapps.canvas.ui.screens.drawing.composables.EraserItem
import com.rkbapps.canvas.ui.screens.drawing.composables.PaintingStyle
import com.rkbapps.canvas.ui.screens.drawing.composables.ShapeSelector
import com.rkbapps.canvas.ui.screens.drawing.composables.ThicknessManagement
import com.rkbapps.canvas.ui.screens.drawing.composables.UndoRedoItem
import com.rkbapps.canvas.ui.screens.drawing.composables.shapeOptions
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingScreen(navController: NavHostController, viewModel: DrawingViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentDesign by viewModel.currentDesign.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val requester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        requester.requestFocus()
    }

    Scaffold(
        modifier = Modifier.onKeyEvent {
            if ((it.isCtrlPressed || it.isMetaPressed) && it.key == Key.S) {
                viewModel.onAction(DrawingAction.SaveDesign(state, currentDesign.name))
                true
            }
            if ((it.isCtrlPressed || it.isMetaPressed) && it.key == Key.Z) {
                viewModel.onAction(DrawingAction.OnUndo)
                true
            }
            if ((it.isCtrlPressed || it.isMetaPressed) && it.key == Key.Y) {
                viewModel.onAction(DrawingAction.OnRedo)
                true
            }

            false
        }.focusRequester(requester)
            .focusable(),

        topBar = {
            AnimatedVisibility(
                visible = !uiState.isFullScreen
            ) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(currentDesign.name)
                            IconButton(onClick = {
                                viewModel.onAction(DrawingAction.OnOpenNameEditDialog)
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
                                viewModel.onAction(
                                    DrawingAction.SaveDesign(
                                        state,
                                        currentDesign.name
                                    )
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "save drawing"
                            )
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
                .padding(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.isEditDrawingNameDialogVisible) {
                EditDrawingNameDialog(
                    initialName = currentDesign.name,
                    onCanceled = {
                        viewModel.onAction(DrawingAction.OnCloseNameEditDialog)
                    }
                ) { name ->
                    viewModel.updateDrawingName(name)
                    viewModel.onAction(DrawingAction.OnCloseNameEditDialog)
                }
            }

            DrawingCanvas(
                paths = state.paths,
                currentPath = state.currentPath,
                onAction = viewModel::onAction,
                modifier = Modifier.fillMaxWidth().weight(1f),
                backgroundColor = state.backgroundColor
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                val rotation = animateFloatAsState(
                    targetValue = if (uiState.isFullScreen) 0f else 180f,
                    label = "ArrowRotation"
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "",
                    modifier = Modifier.rotate(rotation.value).clickable {
                        if (uiState.isFullScreen) viewModel.onAction(DrawingAction.OnExitFullScreen)
                        else viewModel.onAction(DrawingAction.OnEnterFullScreen)
                    }
                )
            }

            LazyRow(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        coroutineScope.launch {
                            scrollState.scrollBy(-delta)
                        }
                    },
                ),
                state = scrollState,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)

            ) {
                item {
                    Spacer(Modifier.width(16.dp))
                }
                item {
                    BackgroundColorChangeItem { color ->
                        viewModel.onAction(DrawingAction.OnBackgroundColorChange(color))
                    }
                }

                item {
                    ColorItemList(
                        selectedColor = state.selectedColor,
                    ) { color ->
                        viewModel.onAction(DrawingAction.OnEraserUnselected)
                        viewModel.onAction(DrawingAction.OnToggleEraser(false))
                        viewModel.onAction(DrawingAction.OnSelectColor(color))
                    }
                }

                item {
                    EraserItem(
                        isEraserSelected = uiState.isEraserSelected
                    ) {
                        viewModel.onAction(DrawingAction.OnEraserSelected)
                        viewModel.onAction(DrawingAction.OnToggleEraser(true))
                    }
                }

                item {
                    PaintingStyle(
                        selected = state.selectedPathEffect,
                    ) { pathEffect ->
                        viewModel.onAction(DrawingAction.OnPathEffectChange(pathEffect))
                    }
                }

                item {
                    ShapeSelector(
                        selectedShape = state.selectedShapeType,
                        shapes = shapeOptions,
                        onShapeSelected = { shapeType ->
                            viewModel.onAction(DrawingAction.OnEraserUnselected)
                            viewModel.onAction(DrawingAction.OnToggleEraser(false))
                            viewModel.onAction(DrawingAction.OnShapeTypeChange(shapeType))
                        }
                    )
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
            AnimatedVisibility(visible = !uiState.isFullScreen) {
                ThicknessManagement(
                    value = state.selectedThickness
                ) { thickness ->
                    viewModel.onAction(DrawingAction.OnThicknessChange(thickness))
                }

            }
            AnimatedVisibility(
                visible = !uiState.isFullScreen,
                modifier = Modifier.fillMaxWidth(0.6f).align(Alignment.CenterHorizontally),
            ) {
                Button(
                    onClick = {
                        viewModel.onAction(DrawingAction.OnClearCanvasList)
                    }
                ) {
                    Text("Clear Canvas")
                }
            }

        }
    }
}





