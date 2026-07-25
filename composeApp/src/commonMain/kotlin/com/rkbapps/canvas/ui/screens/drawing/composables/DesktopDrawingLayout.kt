package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import canvas.composeapp.generated.resources.Res
import canvas.composeapp.generated.resources.back
import canvas.composeapp.generated.resources.clear
import canvas.composeapp.generated.resources.edit_name
import canvas.composeapp.generated.resources.outline_stylus_brush
import canvas.composeapp.generated.resources.save_as_image
import canvas.composeapp.generated.resources.share
import canvas.composeapp.generated.resources.untitled_drawing
import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.ui.composables.DrawingCanvas
import com.rkbapps.canvas.ui.screens.drawing.DrawingAction
import com.rkbapps.canvas.ui.screens.drawing.DrawingScreenState
import com.rkbapps.canvas.ui.screens.drawing.utils.ShapeType
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.text.ifEmpty

// ─────────────────────────────────────────────────────────────────────────────
// DESKTOP LAYOUT  (Expanded — desktop, large tablets landscape)
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesktopDrawingLayout(
    state: DrawingState,
    uiState: DrawingScreenState,
    currentDesign: SavedDesign,
    onAction: (DrawingAction) -> Unit,
    navigateBack:()-> Unit
) {

    var showClearConfirm by remember { mutableStateOf(false) }

    if (showClearConfirm) {
        ClearCanvasConfirmDialog(
            onConfirm = {
                onAction(DrawingAction.OnClearCanvasList)
                showClearConfirm = false
            },
            onDismiss = { showClearConfirm = false }
        )
    }

    Scaffold(
        topBar = {
            // ── Top MenuBar ──────────────────────────────────────────────────────
            AnimatedVisibility(visible = !uiState.isFullScreen) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = currentDesign.name.ifEmpty { stringResource(Res.string.untitled_drawing) },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Ctrl+S to save  •  Ctrl+Z undo  •  Ctrl+Y redo",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = navigateBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                stringResource(Res.string.back)
                            )
                        }
                    },
                    actions = {
                        TextButton(onClick = { onAction(DrawingAction.OnOpenNameEditDialog) }) {
                            Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(stringResource(Res.string.edit_name))
                        }
                        TextButton(onClick = {
                            onAction(
                                DrawingAction.SaveDesign(
                                    state,
                                    currentDesign.name
                                )
                            )
                        }) {
                            Icon(Icons.Default.Save, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Save  ⌘S")
                        }
                        TextButton(onClick = { onAction(DrawingAction.OnSaveAsImage) }) {
                            Icon(Icons.Default.Download, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(stringResource(Res.string.save_as_image))
                        }
                        TextButton(onClick = { onAction(DrawingAction.OnShareDrawing) }) {
                            Icon(Icons.Default.Share, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(stringResource(Res.string.share))
                        }
                        TextButton(onClick = { showClearConfirm = true }) {
                            Text(
                                text = stringResource(Res.string.clear),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    )
                )
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(it)) {
            Row(modifier = Modifier.fillMaxSize().weight(1f)) {
                // ── Left Tool Rail ───────────────────────────────────────────────
                AnimatedVisibility(visible = !uiState.isFullScreen) {
                    Surface(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(72.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        tonalElevation = 2.dp
                    ) {
                        VerticalDrawingActionItems(
                            uiState = uiState,
                            selectedShape = state.selectedShapeType,
                            isRedoVisible = state.redoStack.isNotEmpty(),
                            isUndoVisible = state.undoStack.isNotEmpty(),
                            onAction = onAction
                        )
                    }
                }

                // ── Canvas ───────────────────────────────────────────────────────
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    DrawingCanvas(
                        paths = state.paths,
                        currentPath = state.currentPath,
                        onAction = onAction,
                        isSelectionMode = uiState.isSelectionMode,
                        selectedPathId = uiState.selectedPathId,
                        dragOffset = state.dragOffset,
                        modifier = Modifier.fillMaxSize(),
                        backgroundColor = state.backgroundColor
                    )

                    Column(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp)
                    ) {
                        AnimatedVisibility(visible = uiState.isFullScreen) {
                            HorizontalDrawingActionItem(
                                uiState = uiState,
                                isRedoVisible = state.redoStack.isNotEmpty(),
                                isUndoVisible =  state.undoStack.isNotEmpty(),
                                onAction = onAction
                            )
                        }
                    }
                }

                // ── Right Properties Panel ───────────────────────────────────────
                AnimatedVisibility(visible = !uiState.isFullScreen) {
                    RightPanelUiForLargeScreen(
                        selectedPaintingStyle = state.selectedPathEffect,
                        selectedShape = state.selectedShapeType,
                        selectedColor = state.selectedColor,
                        selectedThickness = state.selectedThickness,
                        onAction = onAction
                    )
                }
            }
        }
    }
}