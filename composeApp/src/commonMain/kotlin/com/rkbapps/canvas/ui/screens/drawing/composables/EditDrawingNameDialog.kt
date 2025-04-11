package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
            Text("Edit Drawing Name")
        },
        text = {
            OutlinedTextField(
                value = name.value,
                onValueChange = {
                    name.value = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Enter Drawing Name")
                },
                label = {
                    Text("Name")
                }

            )
        },
        confirmButton = {
            Button(onClick = {
                onDone(name.value)
            }) {
                Text("Done")
            }
        },
        dismissButton = {
            Button(onClick = {
                onCanceled()
            }) {
                Text("Cancel")
            }
        }
    )
}