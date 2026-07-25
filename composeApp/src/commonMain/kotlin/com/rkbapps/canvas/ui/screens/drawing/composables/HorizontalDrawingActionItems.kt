package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import canvas.composeapp.generated.resources.Res
import canvas.composeapp.generated.resources.outline_stylus_brush
import com.rkbapps.canvas.ui.screens.drawing.DrawingAction
import com.rkbapps.canvas.ui.screens.drawing.DrawingScreenState
import com.rkbapps.canvas.ui.screens.drawing.utils.ShapeType
import org.jetbrains.compose.resources.painterResource

@Composable
fun HorizontalDrawingActionItem(
    uiState: DrawingScreenState,
    isUndoVisible: Boolean = true,
    isRedoVisible: Boolean = true,
    onAction:(action: DrawingAction) -> Unit
){

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        // fullscreen enter/exit
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
        //undo button
        AnimatedVisibility(visible = isUndoVisible){
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
        }
        //redo button
        AnimatedVisibility(visible = isRedoVisible){
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
        }
        // Selection tool
        ToolButton(
            isActive = uiState.isSelectionMode,
            label = "Select",
            showLabel = true,
            onClick = {
                onAction(DrawingAction.OnToggleSelectionMode(!uiState.isSelectionMode))
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
            isActive = !uiState.isSelectionMode && !uiState.isEraserSelected,
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