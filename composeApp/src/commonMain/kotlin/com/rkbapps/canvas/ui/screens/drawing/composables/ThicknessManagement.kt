package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ThicknessManagement(
    value:Float,
    onValueChange: (Float) -> Unit
){
    val circleRadius = (value*35f)/200f
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        val circleColor = MaterialTheme.colorScheme.primary
        Slider(
            modifier = Modifier.weight(1f),
            value = value,
            onValueChange = onValueChange,
            valueRange = 1f..200f,
        )
        Canvas(modifier = Modifier.size(70.dp)) {
            drawCircle(
                radius = circleRadius,
                color = circleColor,
                center = center
            )
        }

    }

}
