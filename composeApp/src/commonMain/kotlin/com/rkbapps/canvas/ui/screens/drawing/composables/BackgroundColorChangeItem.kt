package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun BackgroundColorChangeItem(
    onBackgroundColorPick: (Color) -> Unit
) {
    val colorPickerController = rememberColorPickerController()
    val isColorPickerDialogVisible = remember { mutableStateOf(false) }

    if (isColorPickerDialogVisible.value) {
        ColorPickerDialog(
            isColorPickerVisible = isColorPickerDialogVisible,
            controller = colorPickerController,
            onDone = onBackgroundColorPick
        )
    }

    CompactToolButton(
        isActive = false,
        onClick = { isColorPickerDialogVisible.value = true }
    ) {
        Icon(
            imageVector = Icons.Default.FormatColorFill,
            contentDescription = "Change background color",
            modifier = Modifier.size(22.dp)
        )
    }
}

/**
 * Desktop/Tablet: Background color inline picker with label.
 */
@Composable
fun BackgroundColorInlineItem(
    currentBgColor: Color,
    onBackgroundColorPick: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorPickerController = rememberColorPickerController()
    val isColorPickerDialogVisible = remember { mutableStateOf(false) }

    if (isColorPickerDialogVisible.value) {
        ColorPickerDialog(
            isColorPickerVisible = isColorPickerDialogVisible,
            controller = colorPickerController,
            onDone = onBackgroundColorPick
        )
    }

    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(currentBgColor)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.FormatColorFill,
            contentDescription = "Canvas background color",
            modifier = Modifier.size(16.dp),
            tint = if (currentBgColor == Color.White || currentBgColor == Color.Transparent)
                Color.DarkGray else Color.White
        )
    }
}