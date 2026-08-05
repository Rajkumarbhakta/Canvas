package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import canvas.composeapp.generated.resources.save_project
import canvas.composeapp.generated.resources.shapes
import canvas.composeapp.generated.resources.share
import canvas.composeapp.generated.resources.untitled_drawing
import com.rkbapps.canvas.model.CanvasPage
import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.ui.composables.DrawingCanvas
import com.rkbapps.canvas.ui.composables.MinimalDropdownMenu
import com.rkbapps.canvas.ui.screens.drawing.DrawingAction
import com.rkbapps.canvas.ui.screens.drawing.DrawingScreenState
import com.rkbapps.canvas.ui.screens.drawing.utils.ShapeType
import com.rkbapps.canvas.util.desktopScrollZoom
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


// ─────────────────────────────────────────────────────────────────────────────
// TABLET LAYOUT  (Medium — tablets, foldables open)
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabletDrawingLayout(
    state: DrawingState,
    uiState: DrawingScreenState,
    currentDesign: SavedDesign,
    onAction: (DrawingAction) -> Unit,
    navigateBack: () -> Unit
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

    if (uiState.isPageSizePickerVisible) {
        PageSizePickerDialog(
            currentLabel = state.pageSizeLabel,
            onAction = onAction
        )
    }

    Scaffold(
        topBar = {
            AnimatedVisibility(visible = !uiState.isFullScreen) {
                TopAppBar(
                    title = {
                        Text(
                            text = currentDesign.name.ifEmpty { stringResource(Res.string.untitled_drawing) },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = navigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(Res.string.back))
                        }
                    },
                    actions = {
                        IconButton(onClick = { onAction(DrawingAction.OnUndo) }) {
                            Icon(Icons.AutoMirrored.Filled.Undo, "Undo")
                        }
                        IconButton(onClick = {
                            onAction(DrawingAction.SaveDesign(state, currentDesign.name))
                        }) {
                            Icon(Icons.Default.Save, stringResource(Res.string.save_project))
                        }
                        IconButton(onClick = { onAction(DrawingAction.OnSaveAsImage) }) {
                            Icon(Icons.Default.Download, stringResource(Res.string.save_as_image))
                        }
                        IconButton(onClick = { onAction(DrawingAction.OnShareDrawing) }) {
                            Icon(Icons.Default.Share, stringResource(Res.string.share))
                        }
                        if (!uiState.isLegacy) {
                            IconButton(onClick = { onAction(DrawingAction.OnResetView) }) {
                                Text("Fit", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        MinimalDropdownMenu {
                            DropdownMenuItem(
                                leadingIcon = { Icon(Icons.Default.Edit, null) },
                                text = { Text(stringResource(Res.string.edit_name)) },
                                onClick = { onAction(DrawingAction.OnOpenNameEditDialog) }
                            )
                            if (!uiState.isLegacy) {
                                DropdownMenuItem(
                                    text = { Text("📄 Page Size") },
                                    onClick = { onAction(DrawingAction.OnOpenPageSizePicker) }
                                )
                            }
                            DropdownMenuItem(
                                leadingIcon = { Icon(Icons.Default.Cancel, null) },
                                text = {
                                    Text(
                                        stringResource(Res.string.clear),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = { showClearConfirm = true }
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    )
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Row(modifier = Modifier.weight(1f).fillMaxSize()) {

                // ── Left Tool Rail ────────────────────────────────────────────
                AnimatedVisibility(visible = !uiState.isFullScreen) {
                    Surface(
                        modifier = Modifier.fillMaxHeight().width(72.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        tonalElevation = 2.dp
                    ) {
                        VerticalDrawingActionItems(
                            uiState = uiState,
                            selectedShape = state.selectedShapeType,
                            isRedoVisible = state.redoStack.isNotEmpty(),
                            isUndoVisible = state.undoStack.isNotEmpty(),
                            onAction = onAction,
                            isLegacy = uiState.isLegacy,
                        )
                    }
                }

                // ── Canvas ────────────────────────────────────────────────────
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    val currentPage = state.pages.getOrElse(uiState.currentPageIndex) { CanvasPage() }

                    DrawingCanvas(
                        paths = if (uiState.isLegacy) state.paths else currentPage.paths,
                        currentPath = state.currentPath,
                        onAction = onAction,
                        isSelectionMode = uiState.isSelectionMode,
                        selectedPathId = uiState.selectedPathId,
                        dragOffset = state.dragOffset,
                        modifier = Modifier
                            .fillMaxSize()
                            .desktopScrollZoom(
                                isLegacy = uiState.isLegacy,
                                onAction = onAction,
                            ),
                        backgroundColor = if (uiState.isLegacy) state.backgroundColor
                                          else currentPage.backgroundColor,
                        zoom = uiState.zoom,
                        panOffset = uiState.panOffset,
                        pageWidth = state.pageWidth,
                        pageHeight = state.pageHeight,
                        isLegacy = uiState.isLegacy,
                    )

                    Column(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp)
                    ) {
                        AnimatedVisibility(visible = uiState.isFullScreen) {
                            HorizontalDrawingActionItem(
                                uiState = uiState,
                                isRedoVisible = state.redoStack.isNotEmpty(),
                                isUndoVisible = state.undoStack.isNotEmpty(),
                                onAction = onAction
                            )
                        }
                    }
                }

                // ── Right panel: Properties + Page Strip ──────────────────────
                AnimatedVisibility(visible = !uiState.isFullScreen) {
                    Row {
                        RightPanelUiForLargeScreen(
                            selectedPaintingStyle = state.selectedPathEffect,
                            selectedShape = state.selectedShapeType,
                            selectedColor = state.selectedColor,
                            selectedThickness = state.selectedThickness,
                            onAction = onAction
                        )
                        if (!uiState.isLegacy) {
                            VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                // Page navigation header
                                TabletPageNavigationHeader(
                                    currentPageIndex = uiState.currentPageIndex,
                                    totalPages = state.pages.size,
                                    onAction = onAction
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                                // Vertical page strip with thumbnails
                                VerticalPageStrip(
                                    pages = state.pages,
                                    currentPageIndex = uiState.currentPageIndex,
                                    pageWidth = state.pageWidth,
                                    pageHeight = state.pageHeight,
                                    onAction = onAction
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}