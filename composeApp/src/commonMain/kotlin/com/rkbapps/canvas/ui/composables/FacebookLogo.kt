package com.rkbapps.canvas.ui.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FacebookLogo() {
    val textMeasurer = rememberTextMeasurer()
    val textStyle = TextStyle(
        color = Color.White,
        fontSize = 100.sp,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold
    )

    Canvas(modifier = Modifier.size(100.dp)) {

        drawRoundRect(
            color = Color(0xFF1776d1),
            cornerRadius = CornerRadius(20f,20f)
        )
        drawText(
            textMeasurer = textMeasurer,
            text = "f",
            style = textStyle,
            topLeft = Offset(x = 40f, y = -5f)

        )

    }

}