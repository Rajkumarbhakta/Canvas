package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import canvas.composeapp.generated.resources.Res
import canvas.composeapp.generated.resources.brush
import canvas.composeapp.generated.resources.outline_stylus_brush
import canvas.composeapp.generated.resources.redo
import canvas.composeapp.generated.resources.shapes
import canvas.composeapp.generated.resources.undo
import com.rkbapps.canvas.ui.screens.drawing.DrawingAction
import com.rkbapps.canvas.ui.screens.drawing.DrawingScreenState
import com.rkbapps.canvas.ui.screens.drawing.utils.ShapeType
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun VerticalDrawingActionItems(
    uiState: DrawingScreenState,
    selectedShape: ShapeType,
    isUndoVisible: Boolean = true,
    isRedoVisible: Boolean = true,
    onAction:(action: DrawingAction) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp, horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(visible = isUndoVisible){}
        ToolButton(
            isActive = false,
            label = stringResource(Res.string.undo),
            showLabel = true,
            onClick = { onAction(DrawingAction.OnUndo) },
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Undo,
                contentDescription = stringResource(Res.string.undo),
                modifier = Modifier.size(20.dp)
            )
        }
        AnimatedVisibility(visible = isRedoVisible){
            ToolButton(
                isActive = false,
                label = stringResource(Res.string.redo),
                showLabel = true,
                onClick = {onAction(DrawingAction.OnRedo)},
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Redo,
                    contentDescription = stringResource(Res.string.redo),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 4.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // Selection tool
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

        // Brush tool (pencil mode is "drawing")
        ToolButton(
            isActive = !uiState.isSelectionMode && !uiState.isEraserSelected,
            label = "Brush",
            showLabel = false,
            onClick = {
                onAction(DrawingAction.OnToggleSelectionMode(false))
                onAction(DrawingAction.OnEraserUnselected)
                onAction(DrawingAction.OnToggleEraser(false))
                onAction(DrawingAction.OnShapeTypeChange(ShapeType.NONE))
            }
        ) {
            Icon(
                painter = painterResource(Res.drawable.outline_stylus_brush),
                contentDescription = stringResource(Res.string.brush),
                modifier = Modifier.size(22.dp)
            )
        }

        EraserItem(
            isEraserSelected = uiState.isEraserSelected,
            showLabel = false,
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

        ToolButton(
            isActive = selectedShape != ShapeType.NONE,
            label = stringResource(Res.string.shapes),
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