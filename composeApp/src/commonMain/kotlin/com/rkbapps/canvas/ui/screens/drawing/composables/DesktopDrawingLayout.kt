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

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {

                // ── Left Tool Sidebar ────────────────────────────────────────────
                AnimatedVisibility(visible = !uiState.isFullScreen) {
                    Surface(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(80.dp),
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        tonalElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState())
                                .padding(vertical = 16.dp, horizontal = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Undo / Redo
                            ToolButton(
                                isActive = false,
                                label = "Undo",
                                showLabel = true,
                                onClick = { onAction(DrawingAction.OnUndo) }
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Undo,
                                    "Undo",
                                    Modifier.size(20.dp)
                                )
                            }
                            ToolButton(
                                isActive = false,
                                label = "Redo",
                                showLabel = true,
                                onClick = { onAction(DrawingAction.OnRedo) }
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Redo,
                                    "Redo",
                                    Modifier.size(20.dp)
                                )
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 6.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )

                            // Selection tool
                            ToolButton(
                                isActive = state.isSelectionMode,
                                label = "Select",
                                showLabel = true,
                                onClick = {
                                    onAction(DrawingAction.OnToggleSelectionMode(!state.isSelectionMode))
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.TouchApp,
                                    contentDescription = "Select",
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            // Brush tool
                            ToolButton(
                                isActive = !state.isSelectionMode && !uiState.isEraserSelected && state.selectedShapeType == ShapeType.NONE,
                                label = "Brush",
                                showLabel = true,
                                onClick = {
                                    onAction(DrawingAction.OnToggleSelectionMode(false))
                                    onAction(DrawingAction.OnEraserUnselected)
                                    onAction(DrawingAction.OnToggleEraser(false))
                                    onAction(DrawingAction.OnShapeTypeChange(ShapeType.NONE))
                                }
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.outline_stylus_brush),
                                    contentDescription = "Brush",
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            // Eraser
                            EraserItem(
                                isEraserSelected = uiState.isEraserSelected,
                                showLabel = true,
                                onClick = {
                                    onAction(DrawingAction.OnToggleSelectionMode(false))
                                    if (uiState.isEraserSelected) {
                                        onAction(DrawingAction.OnEraserUnselected)
                                        onAction(DrawingAction.OnToggleEraser(false))
                                    } else {
                                        onAction(DrawingAction.OnEraserSelected)
                                        onAction(DrawingAction.OnToggleEraser(true))
                                    }
                                }
                            )

                            Spacer(Modifier.weight(1f))

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )

                            // Full screen toggle
                            ToolButton(
                                isActive = uiState.isFullScreen,
                                label = if (uiState.isFullScreen) "Exit" else "Focus",
                                showLabel = true,
                                onClick = {
                                    if (uiState.isFullScreen) onAction(DrawingAction.OnExitFullScreen)
                                    else onAction(DrawingAction.OnEnterFullScreen)
                                }
                            ) {
                                Text(
                                    text = if (uiState.isFullScreen) "◻" else "⛶",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (uiState.isFullScreen) MaterialTheme.colorScheme.onPrimaryContainer
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                // ── Canvas ───────────────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surfaceDim)
                ) {
                    DrawingCanvas(
                        paths = state.paths,
                        currentPath = state.currentPath,
                        onAction = onAction,
                        isSelectionMode = state.isSelectionMode,
                        selectedPathId = state.selectedPathId,
                        dragOffset = state.dragOffset,
                        modifier = Modifier.fillMaxSize(),
                        backgroundColor = state.backgroundColor
                    )

                    Column(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp)
                    ) {
                        AnimatedVisibility(visible = uiState.isFullScreen) {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                ToolButton(
                                    isActive = uiState.isFullScreen,
                                    label = if (uiState.isFullScreen) "Exit" else "Focus",
                                    showLabel = true,
                                    onClick = {
                                        if (uiState.isFullScreen) onAction(DrawingAction.OnExitFullScreen)
                                        else onAction(DrawingAction.OnEnterFullScreen)
                                    }
                                ) {
                                    Text(
                                        text = if (uiState.isFullScreen) "◻" else "⛶",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (uiState.isFullScreen) MaterialTheme.colorScheme.onPrimaryContainer
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                ToolButton(
                                    isActive = false,
                                    label = "Undo",
                                    showLabel = true,
                                    onClick = { onAction(DrawingAction.OnUndo) }
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Undo,
                                        "Undo",
                                        Modifier.size(20.dp)
                                    )
                                }
                                ToolButton(
                                    isActive = false,
                                    label = "Redo",
                                    showLabel = true,
                                    onClick = { onAction(DrawingAction.OnRedo) }
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Redo,
                                        "Redo",
                                        Modifier.size(20.dp)
                                    )
                                }

                                // Selection tool
                                ToolButton(
                                    isActive = state.isSelectionMode,
                                    label = "Select",
                                    showLabel = true,
                                    onClick = {
                                        onAction(DrawingAction.OnToggleSelectionMode(!state.isSelectionMode))
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.TouchApp,
                                        contentDescription = "Select",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Brush tool
                                ToolButton(
                                    isActive = !state.isSelectionMode && !uiState.isEraserSelected && state.selectedShapeType == ShapeType.NONE,
                                    label = "Brush",
                                    showLabel = true,
                                    onClick = {
                                        onAction(DrawingAction.OnToggleSelectionMode(false))
                                        onAction(DrawingAction.OnEraserUnselected)
                                        onAction(DrawingAction.OnToggleEraser(false))
                                        onAction(DrawingAction.OnShapeTypeChange(ShapeType.NONE))
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.outline_stylus_brush),
                                        contentDescription = "Brush",
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                // Eraser
                                EraserItem(
                                    isEraserSelected = uiState.isEraserSelected,
                                    showLabel = true,
                                    onClick = {
                                        onAction(DrawingAction.OnToggleSelectionMode(false))
                                        if (uiState.isEraserSelected) {
                                            onAction(DrawingAction.OnEraserUnselected)
                                            onAction(DrawingAction.OnToggleEraser(false))
                                        } else {
                                            onAction(DrawingAction.OnEraserSelected)
                                            onAction(DrawingAction.OnToggleEraser(true))
                                        }
                                    }
                                )
                            }
                        }
                    }

                }

                // ── Right Properties Panel ───────────────────────────────────────
                AnimatedVisibility(visible = !uiState.isFullScreen) {
                    Surface(
                        modifier = Modifier
                            .fillMaxHeight()
                            .widthIn(min = 280.dp, max = 320.dp),
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        tonalElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState())
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Text(
                                text = "Properties",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Brush Style
                            PropertySection(title = "Brush Style") {
                                BrushStyleInlineSelector(
                                    selected = state.selectedPathEffect,
                                    onSelected = { onAction(DrawingAction.OnPathEffectChange(it)) }
                                )
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                            // Shapes
                            PropertySection(title = "Shapes") {
                                ShapeInlineGrid(
                                    selectedShape = state.selectedShapeType,
                                    onShapeSelected = {
                                        onAction(DrawingAction.OnEraserUnselected)
                                        onAction(DrawingAction.OnToggleEraser(false))
                                        onAction(DrawingAction.OnShapeTypeChange(it))
                                    }
                                )
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                            // Stroke Color
                            PropertySection(title = "Stroke Color") {
                                ColorSwatchPanel(
                                    selectedColor = state.selectedColor,
                                    onColorSelected = {
                                        onAction(DrawingAction.OnEraserUnselected)
                                        onAction(DrawingAction.OnToggleEraser(false))
                                        onAction(DrawingAction.OnSelectColor(it))
                                    }
                                )
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                            // Background Color
                            PropertySection(title = "Canvas Background") {
                                BackgroundColorChangeItem { color ->
                                    onAction(DrawingAction.OnBackgroundColorChange(color))
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                            // Stroke Width
                            PropertySection(title = "Stroke Width") {
                                ThicknessManagement(value = state.selectedThickness) {
                                    onAction(DrawingAction.OnThicknessChange(it))
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                            // Keyboard shortcuts hint
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                shape = MaterialTheme.shapes.medium,
                                tonalElevation = 1.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Keyboard Shortcuts",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    ShortcutHint("Ctrl+Z / ⌘Z", "Undo")
                                    ShortcutHint("Ctrl+Y / ⌘Y", "Redo")
                                    ShortcutHint("Ctrl+S / ⌘S", "Save")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}