package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun BackgroundColorChangeItem(
    onBackgroundColorPick:(Color)->Unit
){

    val colorPickerController = rememberColorPickerController()
    val isColorPickerDialogVisible = remember { mutableStateOf(false) }

    if (isColorPickerDialogVisible.value){
        ColorPickerDialog(
            isColorPickerVisible = isColorPickerDialogVisible,
            controller = colorPickerController,
            onDone = onBackgroundColorPick
        )
    }

    Row(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ColorItems(
            color = if(colorPickerController.selectedColor.value == Color.Transparent) Color.White else colorPickerController.selectedColor.value,
            isSelected = false,
            imageVector = Icons.Default.FormatColorFill
        ){
            isColorPickerDialogVisible.value = true
        }
    }
}