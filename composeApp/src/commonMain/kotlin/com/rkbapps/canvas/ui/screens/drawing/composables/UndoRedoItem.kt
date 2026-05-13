package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import canvas.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UndoRedoItem(
    onUndo:()->Unit,
    onRedo:()->Unit
){
    Row(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(10.dp))
            .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
            PaintingStyleItem(
                isSelected =  false,
                icon = {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Undo, contentDescription = stringResource(Res.string.undo), modifier = Modifier.size(20.dp))
                },
                title = {
                    Text(stringResource(Res.string.undo), style = MaterialTheme.typography.labelSmall)
                }
            ) {
                onUndo()
            }
        PaintingStyleItem(
            isSelected =  false,
            icon = {
                Icon(imageVector = Icons.AutoMirrored.Filled.Redo, contentDescription = stringResource(Res.string.redo), modifier = Modifier.size(20.dp))
            },
            title = {
                Text(stringResource(Res.string.redo), style = MaterialTheme.typography.labelSmall)
            }
        ) {
            onRedo()
        }
    }
}