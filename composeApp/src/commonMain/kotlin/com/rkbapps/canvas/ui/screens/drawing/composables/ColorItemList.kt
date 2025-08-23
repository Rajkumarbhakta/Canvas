package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.rkbapps.canvas.util.Constants

@Composable
fun ColorItemList(
    selectedColor:Color,
    onColorItemClick:(Color)->Unit
){
    var expanded by remember { mutableStateOf(false) }
    val colorPickerController = rememberColorPickerController()
    remember{ mutableStateOf(false) }
    val isColorPickerDialogVisible = remember { mutableStateOf(false) }
    if (isColorPickerDialogVisible.value){
        ColorPickerDialog(
            isColorPickerVisible = isColorPickerDialogVisible,
            controller = colorPickerController,
            onDone = onColorItemClick
        )
    }
    Box(
        modifier = Modifier
            .size(50.dp)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(10.dp))
    ){
        Box(
            modifier = Modifier
                .padding(10.dp)
                .size(35.dp)
                .clip(CircleShape)
                .background(
                    color =selectedColor,
                )
                .clickable {
                    expanded = true
                }
        ){}
        DropdownMenu(
            expanded= expanded,
            onDismissRequest = {expanded = false},
            offset = DpOffset(x = 0.dp, y = (0).dp),
            modifier = Modifier.padding(10.dp)
        ){
            FlowRow(modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(10.dp))
                .padding(15.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Constants.colors.forEach {
                    ColorItems(
                        color = it,
                        isSelected = it == selectedColor,
                        onClick = onColorItemClick
                    )
                }
                ColorItems(
                    color = if(colorPickerController.selectedColor.value == Color.Transparent) Color.White else colorPickerController.selectedColor.value,
                    isSelected = colorPickerController.selectedColor.value == selectedColor,
                    imageVector = Icons.Outlined.Palette
                ){
                    isColorPickerDialogVisible.value = true
                }
            }
        }
    }
}


@Composable
fun ColorItems(
    color:Color,
    isSelected: Boolean,
    imageVector: ImageVector?=null,
    onClick: (color:Color) -> Unit = {}
){
    Box(modifier = Modifier
        .clip(CircleShape)
        .background(color = color)
        .size(35.dp)
        .clickable  {
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
        ),
        contentAlignment = Alignment.Center
    ){
        imageVector?.let {
            Icon(
                imageVector,
                contentDescription = "color picker"
            )
        }
    }
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
                .fillMaxWidth().background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
                .padding(10.dp)
        ){
            ColorPicker(controller, onColorChanged)
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

