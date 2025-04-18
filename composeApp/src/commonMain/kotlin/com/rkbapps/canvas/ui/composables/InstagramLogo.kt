package com.rkbapps.canvas.ui.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun InstagramLogo() {

    val gradient = Brush.linearGradient(
        listOf(
            Color.Yellow,
            Color.Red,
            Color.Magenta
        )
    )

    Canvas(modifier = Modifier.size(100.dp)) {
        drawRoundRect(
            brush = gradient,
            style = Stroke(
                width = 4.dp.toPx(),
                cap = StrokeCap.Round
            ),
            cornerRadius = CornerRadius(25f,25f)
        )

        drawCircle(
            brush = gradient,
            radius = 20.dp.toPx(),
            style = Stroke(
                width = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        )

        drawCircle(
            brush = gradient,
            radius = 10.dp.toPx(),
            center = Offset(80f,20f)
        )



    }

}