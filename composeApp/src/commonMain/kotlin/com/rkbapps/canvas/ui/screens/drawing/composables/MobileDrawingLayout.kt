package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import canvas.composeapp.generated.resources.clear_canvas
import canvas.composeapp.generated.resources.edit_name
import canvas.composeapp.generated.resources.save_as_image
import canvas.composeapp.generated.resources.save_project
import canvas.composeapp.generated.resources.share
import canvas.composeapp.generated.resources.untitled_drawing
import com.rkbapps.canvas.model.CanvasPage
import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.ui.composables.DrawingCanvas
import com.rkbapps.canvas.ui.composables.MinimalDropdownMenu
import com.rkbapps.canvas.ui.screens.drawing.DrawingAction
import com.rkbapps.canvas.ui.screens.drawing.DrawingScreenState
import com.rkbapps.canvas.util.desktopScrollZoom
import org.jetbrains.compose.resources.stringResource


// ─────────────────────────────────────────────────────────────────────────────
// MOBILE LAYOUT  (Compact — phones)
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileDrawingLayout(
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
            AnimatedVisibility(
                visible = !uiState.isFullScreen,
                enter = fadeIn() + slideInVertically { -it },
                exit = fadeOut() + slideOutVertically { -it },
            ) {
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
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(Res.string.back)
                            )
                        }
                    },
                    actions = {
                        MinimalDropdownMenu {
                            DropdownMenuItem(
                                leadingIcon = { Icon(Icons.Default.Edit, null) },
                                text = { Text(stringResource(Res.string.edit_name)) },
                                onClick = { onAction(DrawingAction.OnOpenNameEditDialog) }
                            )
                            DropdownMenuItem(
                                leadingIcon = { Icon(Icons.Default.Save, null) },
                                text = { Text(stringResource(Res.string.save_project)) },
                                onClick = { onAction(DrawingAction.SaveDesign(state, currentDesign.name)) }
                            )
                            DropdownMenuItem(
                                leadingIcon = { Icon(Icons.Default.Download, null) },
                                text = { Text(stringResource(Res.string.save_as_image)) },
                                onClick = { onAction(DrawingAction.OnSaveAsImage) }
                            )
                            DropdownMenuItem(
                                leadingIcon = { Icon(Icons.Default.Share, null) },
                                text = { Text(stringResource(Res.string.share)) },
                                onClick = { onAction(DrawingAction.OnShareDrawing) }
                            )
                            if (!uiState.isLegacy) {
                                DropdownMenuItem(
                                    text = { Text("📄 Page Size") },
                                    onClick = { onAction(DrawingAction.OnOpenPageSizePicker) }
                                )
                                DropdownMenuItem(
                                    text = { Text("🔍 Reset Zoom") },
                                    onClick = { onAction(DrawingAction.OnResetView) }
                                )
                            }
                            DropdownMenuItem(
                                leadingIcon = { Icon(Icons.Default.Cancel, null) },
                                text = { Text(stringResource(Res.string.clear)) },
                                onClick = { showClearConfirm = true }
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            val currentPage = state.pages.getOrElse(uiState.currentPageIndex) { CanvasPage() }

            // Canvas — full screen
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

            // Floating undo/redo buttons in full-screen mode
            AnimatedVisibility(
                visible = uiState.isFullScreen,
                modifier = Modifier.align(Alignment.TopStart).padding(top = 16.dp, start = 12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.9f),
                    tonalElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        UndoRedoItem(
                            onUndo = { onAction(DrawingAction.OnUndo) },
                            onRedo = { onAction(DrawingAction.OnRedo) }
                        )
                    }
                }
            }

            // Bottom Toolbar pill (hidden in full screen)
            AnimatedVisibility(
                visible = !uiState.isFullScreen,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it },
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 16.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp,
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Row 1: Tool row
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            item { Spacer(Modifier.width(12.dp)) }
                            item {
                                UndoRedoItem(
                                    onUndo = { onAction(DrawingAction.OnUndo) },
                                    onRedo = { onAction(DrawingAction.OnRedo) }
                                )
                            }
                            item {
                                ToolButton(
                                    isActive = uiState.isSelectionMode,
                                    label = "Select",
                                    showLabel = false,
                                    onClick = {
                                        onAction(DrawingAction.OnToggleSelectionMode(!uiState.isSelectionMode))
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.TouchApp,
                                        contentDescription = "Select",
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            item {
                                PaintingStyle(selected = state.selectedPathEffect) { effect ->
                                    onAction(DrawingAction.OnToggleSelectionMode(false))
                                    onAction(DrawingAction.OnPathEffectChange(effect))
                                }
                            }
                            item {
                                EraserItem(isEraserSelected = uiState.isEraserSelected) {
                                    onAction(DrawingAction.OnToggleSelectionMode(false))
                                    if (uiState.isEraserSelected) {
                                        onAction(DrawingAction.OnEraserUnselected)
                                        onAction(DrawingAction.OnToggleEraser(false))
                                    } else {
                                        onAction(DrawingAction.OnEraserSelected)
                                        onAction(DrawingAction.OnToggleEraser(true))
                                    }
                                }
                            }
                            item {
                                ShapeSelector(selectedShape = state.selectedShapeType) { shapeType ->
                                    onAction(DrawingAction.OnToggleSelectionMode(false))
                                    onAction(DrawingAction.OnEraserUnselected)
                                    onAction(DrawingAction.OnToggleEraser(false))
                                    onAction(DrawingAction.OnShapeTypeChange(shapeType))
                                }
                            }
                            item {
                                ColorItemList(selectedColor = state.selectedColor) { color ->
                                    onAction(DrawingAction.OnEraserUnselected)
                                    onAction(DrawingAction.OnToggleEraser(false))
                                    onAction(DrawingAction.OnSelectColor(color))
                                }
                            }
                            item {
                                BackgroundColorChangeItem { color ->
                                    onAction(DrawingAction.OnBackgroundColorChange(color))
                                }
                            }
                            item { Spacer(Modifier.width(12.dp)) }
                        }

                        // Row 2: Thickness slider
                        ThicknessManagement(value = state.selectedThickness) { thickness ->
                            onAction(DrawingAction.OnThicknessChange(thickness))
                        }

                        // Row 3: Clear + Full screen toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { showClearConfirm = true }) {
                                Text(
                                    text = stringResource(Res.string.clear_canvas),
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                            TextButton(onClick = { onAction(DrawingAction.OnEnterFullScreen) }) {
                                Text(
                                    text = "Full Screen",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }

            // Page navigation bar (mobile — compact floating bar above the bottom toolbar)
            AnimatedVisibility(
                visible = !uiState.isFullScreen && !uiState.isLegacy,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 200.dp)   // clear the ~180dp toolbar pill
            ) {
                MobilePageNavigationBar(
                    pages = state.pages,
                    currentPageIndex = uiState.currentPageIndex,
                    pageWidth = state.pageWidth,
                    pageHeight = state.pageHeight,
                    onAction = onAction,
                )
            }

            // Full-screen exit button
            AnimatedVisibility(
                visible = uiState.isFullScreen,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 10.dp)
            ) {
                Surface(
                    onClick = { onAction(DrawingAction.OnExitFullScreen) },
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.8f),
                    tonalElevation = 4.dp
                ) {
                    Text(
                        text = "Exit Full Screen",
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
