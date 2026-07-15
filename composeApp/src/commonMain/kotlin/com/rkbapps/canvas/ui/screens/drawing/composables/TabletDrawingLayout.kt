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
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import canvas.composeapp.generated.resources.edit_name
import canvas.composeapp.generated.resources.outline_stylus_brush
import canvas.composeapp.generated.resources.save_as_image
import canvas.composeapp.generated.resources.save_project
import canvas.composeapp.generated.resources.shapes
import canvas.composeapp.generated.resources.share
import canvas.composeapp.generated.resources.untitled_drawing
import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.ui.composables.DrawingCanvas
import com.rkbapps.canvas.ui.composables.MinimalDropdownMenu
import com.rkbapps.canvas.ui.screens.drawing.DrawingAction
import com.rkbapps.canvas.ui.screens.drawing.DrawingScreenState
import com.rkbapps.canvas.ui.screens.drawing.utils.ShapeType
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

    Column(modifier = Modifier.fillMaxSize()) {
        // ── Top App Bar ──────────────────────────────────────────────────────
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
                    // Inline action buttons for tablet
                    IconButton(onClick = { onAction(DrawingAction.OnUndo) }) {
                        Icon(Icons.AutoMirrored.Filled.Undo, "Undo")
                    }
                    IconButton(onClick = { onAction(DrawingAction.SaveDesign(state, currentDesign.name)) }) {
                        Icon(Icons.Default.Save, stringResource(Res.string.save_project))
                    }
                    IconButton(onClick = { onAction(DrawingAction.OnSaveAsImage) }) {
                        Icon(Icons.Default.Download, stringResource(Res.string.save_as_image))
                    }
                    IconButton(onClick = { onAction(DrawingAction.OnShareDrawing) }) {
                        Icon(Icons.Default.Share, stringResource(Res.string.share))
                    }
                    MinimalDropdownMenu {
                        DropdownMenuItem(
                            leadingIcon = { Icon(Icons.Default.Edit, null) },
                            text = { Text(stringResource(Res.string.edit_name)) },
                            onClick = { onAction(DrawingAction.OnOpenNameEditDialog) }
                        )
                        DropdownMenuItem(
                            leadingIcon = { Icon(Icons.Default.Cancel, null) },
                            text = { Text(stringResource(Res.string.clear), color = MaterialTheme.colorScheme.error) },
                            onClick = { showClearConfirm = true }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                )
            )
        }

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
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(vertical = 16.dp, horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        UndoRedoItem(
                            onUndo = { onAction(DrawingAction.OnUndo) },
                            onRedo = { onAction(DrawingAction.OnRedo) }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        // Brush tool (pencil mode is "drawing")
                        ToolButton(
                            isActive = !uiState.isEraserSelected && state.selectedShapeType == ShapeType.NONE,
                            label = "Brush",
                            showLabel = false,
                            onClick = {
                                onAction(DrawingAction.OnEraserUnselected)
                                onAction(DrawingAction.OnToggleEraser(false))
                                onAction(DrawingAction.OnShapeTypeChange(ShapeType.NONE))
                            }
                        ) {
                            Icon(
                                painter = painterResource(
                                    Res.drawable.outline_stylus_brush
                                ),
                                contentDescription = "Brush",
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        EraserItem(
                            isEraserSelected = uiState.isEraserSelected,
                            showLabel = false,
                            onClick = {
                                if (uiState.isEraserSelected) {
                                    onAction(DrawingAction.OnEraserUnselected)
                                    onAction(DrawingAction.OnToggleEraser(false))
                                } else {
                                    onAction(DrawingAction.OnEraserSelected)
                                    onAction(DrawingAction.OnToggleEraser(true))
                                }
                            }
                        )

                        ToolButton(
                            isActive = state.selectedShapeType != ShapeType.NONE,
                            label = "Shapes",
                            showLabel = false,
                            onClick = { /* Toggle shape panel — handled via right panel */ }
                        ) {
                            Icon(
                                painter = painterResource(
                                    Res.drawable.shapes
                                ),
                                contentDescription = "Shapes",
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(Modifier.weight(1f))

                        // Full screen toggle
                        ToolButton(
                            isActive = uiState.isFullScreen,
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

            // ── Canvas ───────────────────────────────────────────────────────
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                DrawingCanvas(
                    paths = state.paths,
                    currentPath = state.currentPath,
                    onAction = onAction,
                    modifier = Modifier.fillMaxSize(),
                    backgroundColor = state.backgroundColor
                )
            }

            // ── Right Properties Panel ───────────────────────────────────────
            AnimatedVisibility(visible = !uiState.isFullScreen) {
                Surface(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(240.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    tonalElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Section: Brush Style
                        PropertySection(title = "Brush Style") {
                            BrushStyleInlineSelector(
                                selected = state.selectedPathEffect,
                                onSelected = { onAction(DrawingAction.OnPathEffectChange(it)) }
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        // Section: Shapes
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

                        // Section: Stroke Color
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

                        // Section: Background Color
                        PropertySection(title = "Canvas Background") {
                            BackgroundColorChangeItem { color ->
                                onAction(DrawingAction.OnBackgroundColorChange(color))
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        // Section: Thickness
                        PropertySection(title = "Stroke Width") {
                            ThicknessManagement(value = state.selectedThickness) {
                                onAction(DrawingAction.OnThicknessChange(it))
                            }
                        }
                    }
                }
            }
        }
    }
}