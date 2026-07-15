package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import canvas.composeapp.generated.resources.Res
import canvas.composeapp.generated.resources.cancel
import canvas.composeapp.generated.resources.done
import canvas.composeapp.generated.resources.edit_drawing_name
import canvas.composeapp.generated.resources.enter_drawing_name
import canvas.composeapp.generated.resources.name
import org.jetbrains.compose.resources.stringResource

@Composable
fun EditDrawingNameDialog(
    initialName: String,
    onCanceled: () -> Unit,
    onDone: (name: String) -> Unit
) {
    val name: MutableState<String> = remember { mutableStateOf(initialName) }

    AlertDialog(
        onDismissRequest = onCanceled,
        icon = {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = stringResource(Res.string.edit_drawing_name),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            OutlinedTextField(
                value = name.value,
                onValueChange = { name.value = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(Res.string.enter_drawing_name)) },
                label = { Text(stringResource(Res.string.name)) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
            )
        },
        confirmButton = {
            Button(
                onClick = { onDone(name.value) },
                shape = MaterialTheme.shapes.medium,
                enabled = name.value.isNotBlank()
            ) {
                Text(stringResource(Res.string.done))
            }
        },
        dismissButton = {
            TextButton(onClick = onCanceled) {
                Text(stringResource(Res.string.cancel))
            }
        },
        shape = MaterialTheme.shapes.extraLarge
    )
}