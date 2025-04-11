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
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
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
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.rkbapps.canvas.ui.composables.DrawingCanvas
import com.rkbapps.canvas.util.Constants
import com.rkbapps.canvas.util.Platforms
import com.rkbapps.canvas.util.getPlatform

@Composable
fun DrawingScreen(viewModel: DrawingViewModel = if (getPlatform() == Platforms.ANDROID) viewModel() else DrawingViewModel()){
    val state = viewModel.state.collectAsStateWithLifecycle()
    val colorPickerController = rememberColorPickerController()
    val isColorPickerDialogVisible = remember { mutableStateOf(false) }

    Scaffold {
        Column(Modifier.fillMaxSize().padding(it).padding(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

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
fun ColorItems(
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
fun ColorPickerDialog(
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