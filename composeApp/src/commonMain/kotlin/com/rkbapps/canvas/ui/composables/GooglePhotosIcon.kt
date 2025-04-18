package com.rkbapps.canvas.ui.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp


@Composable
fun GooglePhotosIcon() {

    Canvas(modifier = Modifier.size(100.dp)){
        drawArc(
            color = Color(0xFF00ab58),
            startAngle = 180f,
            sweepAngle = 180f,
            style = Fill,
            useCenter = false,
            size = Size(size.width/2,size.height/2),
            topLeft = Offset(0f,size.height/4)
        )
        drawArc(
            color = Color(0xFFf04231),
            startAngle = -90f,
            sweepAngle = 180f,
            style = Fill,
            useCenter = false,
            size = Size(size.width/2,size.height/2),
            topLeft = Offset(size.height/4,0f)
        )
        drawArc(
            color = Color(0xFF0586f0),
            startAngle = 0f,
            sweepAngle = 180f,
            style = Fill,
            useCenter = false,
            size = Size(size.width/2,size.height/2),
            topLeft = Offset(size.width/2,size.height/4)
        )
        drawArc(
            color = Color(0xFFffbb33),
            startAngle = 90f,
            sweepAngle = 180f,
            style = Fill,
            useCenter = false,
            size = Size(size.width/2,size.height/2),
            topLeft = Offset(size.width/4,size.height/2)
        )
    }
}