package com.rkbapps.canvas.ui.screens.drawing

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import canvas.composeapp.generated.resources.Res
import canvas.composeapp.generated.resources.drawing_saved
import canvas.composeapp.generated.resources.file_export_failed
import canvas.composeapp.generated.resources.file_exported
import com.rkbapps.canvas.model.canvasFileName
import com.rkbapps.canvas.ui.screens.drawing.composables.EditDrawingNameDialog
import com.rkbapps.canvas.ui.screens.drawing.composables.MobileDrawingLayout
import com.rkbapps.canvas.ui.screens.drawing.composables.WideDrawingLayout
import com.rkbapps.canvas.ui.screens.drawing.composables.ZOOM_STEP
import com.rkbapps.canvas.ui.screens.drawing.utils.DrawingTool
import com.rkbapps.canvas.ui.screens.drawing.utils.ShapeType
import com.rkbapps.canvas.ui.screens.drawing.utils.activeTool
import com.rkbapps.canvas.ui.screens.drawing.utils.toolAction
import com.rkbapps.canvas.util.getWindowSize
import com.rkbapps.canvas.util.isDrawingFileSupported
import com.rkbapps.canvas.util.rememberDrawingFileExporter
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DrawingScreen(
    navController: NavHostController,
    viewModel: DrawingViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentDesign by viewModel.currentDesign.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val widthSizeClass = getWindowSize().widthSizeClass

    // ── Tools ─────────────────────────────────────────────────────────────────
    val tool = activeTool(state, uiState)
    // Leaving the shape tool resets the shape to NONE; remember the last one so
    // coming back to the tool brings back the same shape.
    var lastShape by rememberSaveable { mutableStateOf(ShapeType.RECTANGLE) }
    if (state.selectedShapeType != ShapeType.NONE && state.selectedShapeType != lastShape) {
        SideEffect { lastShape = state.selectedShapeType }
    }
    val onSelectTool: (DrawingTool) -> Unit = { viewModel.onAction(toolAction(it, lastShape)) }

    // ── Save, with confirmation ───────────────────────────────────────────────
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val savedMessage = stringResource(Res.string.drawing_saved)
    val showMessage: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message)
        }
    }
    val onSave: () -> Unit = {
        viewModel.onAction(DrawingAction.SaveDesign(state, currentDesign.name))
        showMessage(savedMessage)
    }

    // ── Export as a Canvas file (paged drawings, where files are supported) ───
    val exportedMessage = stringResource(Res.string.file_exported)
    val exportFailedMessage = stringResource(Res.string.file_export_failed)
    val exportFile = rememberDrawingFileExporter { saved ->
        showMessage(if (saved) exportedMessage else exportFailedMessage)
    }
    val onExportFile: (() -> Unit)? = if (isDrawingFileSupported && !uiState.isLegacy) {
        {
            scope.launch {
                // What is on screen is exported, saved or not — the same as "Save as image".
                exportFile(canvasFileName(currentDesign.name), viewModel.buildCanvasFile())
            }
        }
    } else null

    // ── Keyboard shortcuts (desktop, tablets with a keyboard) ─────────────────
    val requester = remember { FocusRequester() }
    LaunchedEffect(Unit) { requester.requestFocus() }

    val keyModifier = Modifier
        .onKeyEvent { event ->
            // Every press also sends a KeyUp; acting on both would run each shortcut twice.
            if (event.type != KeyEventType.KeyDown) return@onKeyEvent false
            val action: DrawingAction? = if (event.isCtrlPressed || event.isMetaPressed) {
                when (event.key) {
                    Key.S -> {
                        onSave()
                        return@onKeyEvent true
                    }
                    Key.Z -> if (event.isShiftPressed) DrawingAction.OnRedo else DrawingAction.OnUndo
                    Key.Y -> DrawingAction.OnRedo
                    Key.F -> if (uiState.isFullScreen) DrawingAction.OnExitFullScreen
                    else DrawingAction.OnEnterFullScreen
                    Key.Zero -> DrawingAction.OnResetView.takeIf { !uiState.isLegacy }
                    Key.Equals, Key.Plus ->
                        DrawingAction.OnZoomPan(ZOOM_STEP, Offset.Zero).takeIf { !uiState.isLegacy }
                    Key.Minus ->
                        DrawingAction.OnZoomPan(1f / ZOOM_STEP, Offset.Zero).takeIf { !uiState.isLegacy }
                    else -> null
                }
            } else {
                when (event.key) {
                    Key.V -> toolAction(DrawingTool.SELECT, lastShape)
                    Key.B -> toolAction(DrawingTool.BRUSH, lastShape)
                    Key.U -> toolAction(DrawingTool.SHAPE, lastShape)
                    Key.E -> toolAction(DrawingTool.ERASER, lastShape)
                    Key.Escape -> DrawingAction.OnExitFullScreen.takeIf { uiState.isFullScreen }
                    Key.Delete, Key.Backspace ->
                        DrawingAction.OnDeleteSelectedPath.takeIf { uiState.selectedPathId != null }
                    else -> null
                }
            }
            action?.let(viewModel::onAction) != null
        }
        .focusRequester(requester)
        .focusable()

    // Name edit dialog (shown on top of any layout)
    if (uiState.isEditDrawingNameDialogVisible) {
        EditDrawingNameDialog(
            initialName = currentDesign.name,
            onCanceled = { viewModel.onAction(DrawingAction.OnCloseNameEditDialog) }
        ) { name ->
            viewModel.updateDrawingName(name)
            viewModel.onAction(DrawingAction.OnCloseNameEditDialog)
        }
    }

    Box(modifier = keyModifier) {
        if (widthSizeClass == WindowWidthSizeClass.Compact) {
            MobileDrawingLayout(
                state = state,
                uiState = uiState,
                drawingName = currentDesign.name,
                tool = tool,
                snackbarHostState = snackbarHostState,
                onSelectTool = onSelectTool,
                onSave = onSave,
                onExportFile = onExportFile,
                onAction = viewModel::onAction,
                navigateBack = { navController.navigateUp() }
            )
        } else {
            WideDrawingLayout(
                state = state,
                uiState = uiState,
                drawingName = currentDesign.name,
                tool = tool,
                snackbarHostState = snackbarHostState,
                panelInitiallyOpen = widthSizeClass == WindowWidthSizeClass.Expanded,
                onSelectTool = onSelectTool,
                onSave = onSave,
                onExportFile = onExportFile,
                onAction = viewModel::onAction,
                navigateBack = { navController.navigateUp() }
            )
        }
    }
}
