package com.rkbapps.canvas.ui.screens.drawing

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.rkbapps.canvas.ui.screens.drawing.composables.DesktopDrawingLayout
import com.rkbapps.canvas.ui.screens.drawing.composables.EditDrawingNameDialog
import com.rkbapps.canvas.ui.screens.drawing.composables.MobileDrawingLayout
import com.rkbapps.canvas.ui.screens.drawing.composables.TabletDrawingLayout
import com.rkbapps.canvas.util.Log
import com.rkbapps.canvas.util.getWindowSize
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DrawingScreen(
    navController: NavHostController,
    viewModel: DrawingViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentDesign by viewModel.currentDesign.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val windowSizeClass = getWindowSize()
    val widthSizeClass = windowSizeClass.widthSizeClass

    val requester = remember { FocusRequester() }

    LaunchedEffect(Unit) { requester.requestFocus() }

    // Global keyboard shortcut handler (desktop/tablet)
    val keyModifier = Modifier
        .onKeyEvent {
            when {
                (it.isCtrlPressed || it.isMetaPressed) && it.key == Key.S -> {
                    viewModel.onAction(DrawingAction.SaveDesign(state, currentDesign.name))
                    true
                }
                (it.isCtrlPressed || it.isMetaPressed) && it.key == Key.Z -> {
                    viewModel.onAction(DrawingAction.OnUndo)
                    true
                }
                (it.isCtrlPressed || it.isMetaPressed) && it.key == Key.Y -> {
                    viewModel.onAction(DrawingAction.OnRedo)
                    true
                }
                (it.isCtrlPressed || it.isMetaPressed) && it.key == Key.F -> {
                    if (!uiState.isFullScreen){
                        viewModel.onAction(DrawingAction.OnEnterFullScreen)
                    }
                    true
                }
                // Ctrl+0 / Cmd+0 — reset zoom and pan to default (1× centred)
                (it.isCtrlPressed || it.isMetaPressed) && it.key == Key.Zero -> {
                    if (!uiState.isLegacy) {
                        viewModel.onAction(DrawingAction.OnResetView)
                    }
                    true
                }
                else -> false
            }
        }
        .focusRequester(requester)
        .focusable()

    // Name edit dialog (platform-agnostic, shown on top of any layout)
    if (uiState.isEditDrawingNameDialogVisible) {
        EditDrawingNameDialog(
            initialName = currentDesign.name,
            onCanceled = { viewModel.onAction(DrawingAction.OnCloseNameEditDialog) }
        ) { name ->
            viewModel.updateDrawingName(name)
            viewModel.onAction(DrawingAction.OnCloseNameEditDialog)
        }
    }

    // Route to platform-appropriate layout
    // Apply keyboard shortcut modifier at layout root
    Box(modifier = keyModifier) {
        when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> MobileDrawingLayout(
                state = state,
                uiState = uiState,
                currentDesign = currentDesign,
                onAction = viewModel::onAction,
                navigateBack = { navController.navigateUp() }
            )

            WindowWidthSizeClass.Medium -> TabletDrawingLayout(
                state = state,
                uiState = uiState,
                currentDesign = currentDesign,
                onAction = viewModel::onAction,
                navigateBack = { navController.navigateUp() })

            else -> DesktopDrawingLayout(
                state = state,
                uiState = uiState,
                currentDesign = currentDesign,
                onAction = viewModel::onAction,
                navigateBack = { navController.navigateUp() }) // Expanded
        }
    }
}
