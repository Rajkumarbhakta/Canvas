package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import canvas.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun EditDrawingNameDialog(
    initialName: String,
    onCanceled:()-> Unit,
    onDone: (name:String) -> Unit
){
    val name: MutableState<String> = remember {mutableStateOf(initialName)}
    AlertDialog(
        onDismissRequest = {
            onCanceled()
        },
        title = {
            Text(stringResource(Res.string.edit_drawing_name))
        },
        text = {
            OutlinedTextField(
                value = name.value,
                onValueChange = {
                    name.value = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(stringResource(Res.string.enter_drawing_name))
                },
                label = {
                    Text(stringResource(Res.string.name))
                }

            )
        },
        confirmButton = {
            Button(onClick = {
                onDone(name.value)
            }) {
                Text(stringResource(Res.string.done))
            }
        },
        dismissButton = {
            Button(onClick = {
                onCanceled()
            }) {
                Text(stringResource(Res.string.cancel))
            }
        }
    )
}