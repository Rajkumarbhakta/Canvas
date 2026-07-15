package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import canvas.composeapp.generated.resources.Res
import canvas.composeapp.generated.resources.redo
import canvas.composeapp.generated.resources.undo
import org.jetbrains.compose.resources.stringResource

@Composable
fun UndoRedoItem(
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    showLabels: Boolean = false,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ToolButton(
            isActive = false,
            label = stringResource(Res.string.undo),
            showLabel = showLabels,
            onClick = onUndo,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Undo,
                contentDescription = stringResource(Res.string.undo),
                modifier = Modifier.size(20.dp)
            )
        }
        ToolButton(
            isActive = false,
            label = stringResource(Res.string.redo),
            showLabel = showLabels,
            onClick = onRedo,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Redo,
                contentDescription = stringResource(Res.string.redo),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}