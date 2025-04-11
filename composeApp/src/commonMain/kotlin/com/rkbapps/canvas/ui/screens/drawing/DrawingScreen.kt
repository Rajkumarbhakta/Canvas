package com.rkbapps.canvas.ui.screens.drawing

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.rkbapps.canvas.ui.composables.DrawingCanvas
import com.rkbapps.canvas.util.Constants
import com.rkbapps.canvas.util.Platforms
import com.rkbapps.canvas.util.getPlatform
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingScreen(navController: NavHostController, viewModel: DrawingViewModel = koinViewModel()){
    val state = viewModel.state.collectAsStateWithLifecycle()
    val colorPickerController = rememberColorPickerController()
    val isColorPickerDialogVisible = remember { mutableStateOf(false) }

    val drawingName = remember {mutableStateOf("Untitled Drawing")}
    val isEditDrawingNameDialogVisible = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(drawingName.value)
                        IconButton(onClick = {
                            isEditDrawingNameDialogVisible.value = true
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "edit name")
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.onAction(DrawingAction.OnClearCanvasList)
                            navController.navigateUp()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = "save drawing")
                    }
                }
            )
        }
    ) {
        Column(Modifier.fillMaxSize().padding(it).padding(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            if(isEditDrawingNameDialogVisible.value){
                EditDrawingNameDialog(
                    initialName = drawingName.value,
                    onCanceled = {
                        isEditDrawingNameDialogVisible.value = false
                    }
                ) {name->
                    drawingName.value = name
                    isEditDrawingNameDialogVisible.value = false
                }
            }

            if (isColorPickerDialogVisible.value){
                ColorPickerDialog(
                    isColorPickerVisible = isColorPickerDialogVisible,
                    controller = colorPickerController,
                ){
                    viewModel.onAction(DrawingAction.OnSelectColor(colorPickerController.selectedColor.value))
                }
            }

            DrawingCanvas(
                paths = state.value.paths,
                currentPath = state.value.currentPath,
                onAction = viewModel::onAction,
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
            Row(modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 8.dp)
                .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Constants.colors.forEach {
                    ColorItems(
                        color = it,
                        isSelected = it == state.value.selectedColor
                    ){
                        viewModel.onAction(DrawingAction.OnSelectColor(it))
                    }
                }
                ColorItems(
                    color = colorPickerController.selectedColor.value,
                    isSelected = colorPickerController.selectedColor.value == state.value.selectedColor
                ){
                    isColorPickerDialogVisible.value = true
                }
            }

            Slider(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                value = state.value.selectedThickness,
                onValueChange = {
                    viewModel.onAction(DrawingAction.OnThicknessChange(it))
                },
                valueRange = 1f..50f,
            )

            Button(
                modifier = Modifier.fillMaxWidth(0.6f).align(Alignment.CenterHorizontally),
                onClick = {
                    println("Clear Canvas")
                    viewModel.onAction(DrawingAction.OnClearCanvasList)
                }
            ) {
                Text("Clear Canvas")
            }

        }
    }
}

@Composable
private fun ColorItems(
    color:Color,
    isSelected: Boolean,
    onClick: (color:Color) -> Unit = {}
){
    Box(modifier = Modifier
        .clip(CircleShape)
        .background(color = color)
        .size(50.dp)
        .clickable{
            onClick(color)
        }
        .border(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) {
                if(isSystemInDarkTheme() || color == Color.Black){
                    Color.White
                }else{
                    Color.Black
                }
            }else Color.Transparent,
            shape = CircleShape
        )
    )
}

@Composable
private fun ColorPickerDialog(
    isColorPickerVisible:MutableState<Boolean>,
    controller: ColorPickerController,
    onColorChanged: (ColorEnvelope) -> Unit = {},
    onDone: (color: Color) -> Unit = {}
){
    Dialog(
        onDismissRequest = {
            isColorPickerVisible.value = false
        }
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth().background(Color.White, shape = RoundedCornerShape(8.dp))
                .padding(10.dp)
        ){
            HsvColorPicker(
                initialColor = controller.selectedColor.value,
                modifier = Modifier.fillMaxWidth().height(300.dp).padding(10.dp),
                controller = controller,
                onColorChanged = onColorChanged
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row (modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
                ){
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onDone(controller.selectedColor.value)
                        isColorPickerVisible.value = false
                    }
                ) {
                    Text("Done")
                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        isColorPickerVisible.value = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}


@Composable
private fun EditDrawingNameDialog(
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
