package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FormatColorFill
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import canvas.composeapp.generated.resources.Res
import canvas.composeapp.generated.resources.back
import canvas.composeapp.generated.resources.clear_page
import canvas.composeapp.generated.resources.delete_page
import canvas.composeapp.generated.resources.focus_mode
import canvas.composeapp.generated.resources.more_options
import canvas.composeapp.generated.resources.page_size
import canvas.composeapp.generated.resources.paper_color
import canvas.composeapp.generated.resources.rename
import canvas.composeapp.generated.resources.save
import canvas.composeapp.generated.resources.save_as_image
import canvas.composeapp.generated.resources.share
import canvas.composeapp.generated.resources.tool_options
import com.rkbapps.canvas.model.CanvasPage
import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.ui.screens.drawing.DrawingAction
import com.rkbapps.canvas.ui.screens.drawing.DrawingScreenState
import com.rkbapps.canvas.ui.screens.drawing.utils.DrawingTool
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs

// ─────────────────────────────────────────────────────────────────────────────
// PHONE LAYOUT  (Compact)
//
//   top bar        name, save, everything else in the menu
//   canvas         edge to edge behind the floating controls
//   bottom         undo/redo and page stepper, then the tool dock; the dock
//                  pulls up to show the active tool's options
// ─────────────────────────────────────────────────────────────────────────────

/** Height of the floating controls at the bottom; the fitted page stays above them. */
private val BottomControlsHeight = 132.dp
private val FocusDockHeight = 76.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileDrawingLayout(
    state: DrawingState,
    uiState: DrawingScreenState,
    drawingName: String,
    tool: DrawingTool,
    snackbarHostState: SnackbarHostState,
    onSelectTool: (DrawingTool) -> Unit,
    onSave: () -> Unit,
    onAction: (DrawingAction) -> Unit,
    navigateBack: () -> Unit,
) {
    val currentPage = state.pages.getOrElse(uiState.currentPageIndex) { CanvasPage() }
    val paperColor = if (uiState.isLegacy) state.backgroundColor else currentPage.backgroundColor
    val canUndo = if (uiState.isLegacy) state.undoStack.isNotEmpty() else currentPage.undoStack.isNotEmpty()
    val canRedo = if (uiState.isLegacy) state.redoStack.isNotEmpty() else currentPage.redoStack.isNotEmpty()
    val focus = uiState.isFullScreen

    var optionsOpen by rememberSaveable { mutableStateOf(false) }
    var dialog by rememberSaveable { mutableStateOf<DrawingDialog?>(null) }

    DrawingDialogs(dialog, state, uiState, paperColor, onAction, onDismiss = { dialog = null })

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        snackbarHost = {
            SnackbarHost(
                snackbarHostState,
                Modifier.padding(bottom = if (focus) FocusDockHeight else BottomControlsHeight)
            )
        },
        topBar = {
            AnimatedVisibility(visible = !focus) {
                Column {
                    TopAppBar(
                        title = {
                            DrawingTitle(drawingName, onRename = { onAction(DrawingAction.OnOpenNameEditDialog) })
                        },
                        navigationIcon = {
                            ChromeButton(
                                label = stringResource(Res.string.back),
                                onClick = navigateBack,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            ) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, null) }
                        },
                        actions = {
                            ChromeButton(label = stringResource(Res.string.save), onClick = onSave) {
                                Icon(Icons.Outlined.Save, null, Modifier.size(22.dp))
                            }
                            MenuButton(stringResource(Res.string.more_options), Icons.Rounded.MoreVert) { close ->
                                MenuItem(stringResource(Res.string.rename), Icons.Outlined.Edit, onClick = {
                                    close(); onAction(DrawingAction.OnOpenNameEditDialog)
                                })
                                MenuItem(stringResource(Res.string.save_as_image), Icons.Outlined.Image, onClick = {
                                    close(); onAction(DrawingAction.OnSaveAsImage)
                                })
                                MenuItem(stringResource(Res.string.share), Icons.Outlined.Share, onClick = {
                                    close(); onAction(DrawingAction.OnShareDrawing)
                                })
                                HorizontalDivider()
                                if (!uiState.isLegacy) {
                                    MenuItem(stringResource(Res.string.page_size), Icons.Outlined.Description, onClick = {
                                        close(); onAction(DrawingAction.OnOpenPageSizePicker)
                                    })
                                }
                                MenuItem(stringResource(Res.string.paper_color), Icons.Outlined.FormatColorFill, onClick = {
                                    close(); dialog = DrawingDialog.PAPER_COLOR
                                })
                                MenuItem(stringResource(Res.string.focus_mode), Icons.Rounded.Fullscreen, onClick = {
                                    close(); onAction(DrawingAction.OnEnterFullScreen)
                                })
                                HorizontalDivider()
                                if (!uiState.isLegacy) {
                                    MenuItem(
                                        stringResource(Res.string.delete_page), Icons.Outlined.Delete,
                                        destructive = true,
                                        enabled = state.pages.size > 1,
                                        onClick = { close(); dialog = DrawingDialog.DELETE_PAGE }
                                    )
                                }
                                MenuItem(
                                    stringResource(Res.string.clear_page), Icons.Outlined.DeleteSweep,
                                    destructive = true,
                                    onClick = { close(); dialog = DrawingDialog.CLEAR_PAGE }
                                )
                            }
                            Spacer(Modifier.size(4.dp))
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        )
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            DrawingSurface(
                state = state,
                uiState = uiState,
                currentPage = currentPage,
                // Starting a stroke puts the options away so they never cover the drawing.
                onAction = { action ->
                    if (action is DrawingAction.OnNewPathStart) optionsOpen = false
                    onAction(action)
                },
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = if (focus) FocusDockHeight else BottomControlsHeight)
            )

            // Zoom only takes up space once the page is zoomed; pressing it fits the page again.
            AnimatedVisibility(
                visible = !uiState.isLegacy && abs(uiState.zoom - 1f) > 0.01f,
                modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FloatingBar { ZoomLabel(uiState.zoom, onAction) }
            }

            if (focus) {
                FocusDock(
                    activeTool = tool,
                    canUndo = canUndo,
                    canRedo = canRedo,
                    onSelectTool = onSelectTool,
                    onAction = onAction,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
                )
            } else {
                Column(
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        FloatingBar {
                            Row(Modifier.padding(2.dp)) {
                                UndoRedoButtons(canUndo, canRedo, onAction, buttonSize = 36.dp)
                            }
                        }
                        if (!uiState.isLegacy) {
                            PageStepper(uiState.currentPageIndex, state.pages.size, onAction)
                        }
                    }
                    ToolDock(
                        state = state,
                        tool = tool,
                        paperColor = paperColor,
                        hasSelection = uiState.selectedPathId != null,
                        optionsOpen = optionsOpen,
                        onOptionsOpenChange = { optionsOpen = it },
                        onSelectTool = onSelectTool,
                        onAction = onAction
                    )
                }
            }
        }
    }
}

/**
 * The tool dock. Pressing a tool selects it; pressing the active tool again (or
 * the nib on the right) pulls up that tool's options.
 */
@Composable
private fun ToolDock(
    state: DrawingState,
    tool: DrawingTool,
    paperColor: Color,
    hasSelection: Boolean,
    optionsOpen: Boolean,
    onOptionsOpenChange: (Boolean) -> Unit,
    onSelectTool: (DrawingTool) -> Unit,
    onAction: (DrawingAction) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = 6.dp
    ) {
        Column {
            AnimatedVisibility(visible = optionsOpen) {
                Column {
                    ToolInspector(
                        tool = tool,
                        color = state.selectedColor,
                        thickness = state.selectedThickness,
                        style = state.selectedPathEffect,
                        shape = state.selectedShapeType,
                        paperColor = paperColor,
                        hasSelection = hasSelection,
                        onAction = onAction,
                        modifier = Modifier
                            .heightIn(max = 420.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ToolButtons(
                    activeTool = tool,
                    buttonSize = 48.dp,
                    onSelectTool = { picked ->
                        if (picked == tool) {
                            onOptionsOpenChange(!optionsOpen)
                        } else {
                            onSelectTool(picked)
                            // A shape has to be chosen before it can be drawn.
                            onOptionsOpenChange(picked == DrawingTool.SHAPE)
                        }
                    }
                )
                Spacer(Modifier.weight(1f))
                val label = stringResource(Res.string.tool_options)
                Row(
                    modifier = Modifier
                        .clip(ChromeShape)
                        .clickable(role = Role.Button, onClickLabel = label) { onOptionsOpenChange(!optionsOpen) }
                        .height(48.dp)
                        .padding(start = 8.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NibBadge(state.selectedColor, state.selectedThickness, tool == DrawingTool.ERASER)
                    Icon(
                        if (optionsOpen) Icons.Rounded.KeyboardArrowDown else Icons.Rounded.KeyboardArrowUp,
                        contentDescription = label,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
