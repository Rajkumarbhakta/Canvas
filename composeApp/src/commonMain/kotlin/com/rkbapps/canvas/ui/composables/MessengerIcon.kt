package com.rkbapps.canvas.ui.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun MessengerIcon() {

    val colors = listOf(Color(0xFF02b8f9), Color(0xFF0277fe))

    Canvas(modifier = Modifier.size(100.dp)) {

        val trianglePath = Path().let {
            it.moveTo(this.size.width * .20f, this.size.height * .77f) //(20,77)
            it.lineTo(this.size.width * .20f, this.size.height * 0.95f) // (20,95)
            it.lineTo(this.size.width * .37f, this.size.height * 0.86f) //(37,86)
            it.close()
            it
        }

        val electricPath = Path().let {
            it.moveTo(this.size.width * .20f, this.size.height * 0.60f) //(20,60)
            it.lineTo(this.size.width * .45f, this.size.height * 0.35f) //(45,35)
            it.lineTo(this.size.width * 0.56f, this.size.height * 0.46f) // (56,46)
            it.lineTo(this.size.width * 0.78f, this.size.height * 0.35f) //(78,35)
            it.lineTo(this.size.width * 0.54f, this.size.height * 0.60f) //(54,60)
            it.lineTo(this.size.width * 0.43f, this.size.height * 0.45f) //(43,45)
            it.close()
            it
        }


        drawOval(
            brush = Brush.linearGradient(colors),
            size = Size(this.size.width, this.size.height * 0.95f) // size will be for 100 (100,95)
        )

        drawPath(
            path = trianglePath,
            Brush.verticalGradient(colors = colors),
            style = Stroke(width = 15f, cap = StrokeCap.Round)
        )

        drawPath(path = electricPath, color = Color.White)


    }
}