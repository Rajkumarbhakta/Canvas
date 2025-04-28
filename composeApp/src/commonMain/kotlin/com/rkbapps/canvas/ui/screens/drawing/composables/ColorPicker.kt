package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker

@Composable
fun ColorPicker(
    controller: ColorPickerController,
    onColorChanged: (ColorEnvelope) -> Unit
) {
    HsvColorPicker(
        initialColor = controller.selectedColor.value,
        modifier = Modifier.fillMaxWidth().height(300.dp).padding(10.dp),
        controller = controller,
        onColorChanged = onColorChanged
    )
    Spacer(modifier = Modifier.height(10.dp))
    AlphaSlider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(25.dp),
        controller = controller,
    )
    BrightnessSlider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(25.dp),
        controller = controller,
    )
}