package com.rkbapps.canvas.ui.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CircularIndicator(
    canvasSize: Dp = 300.dp,
    backgroundIndicatorColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
    foreGroundColor: Color = MaterialTheme.colorScheme.primary,
    backgroundIndicatorStrokeWidth: Dp = 50.dp,
    foregroundIndicatorStrokeWidth: Dp = 50.dp,
    maxIndicatorValue:Float = 100f,
    progress:()-> Float
) {

    val allowedIndicatorValue = if (progress() <= maxIndicatorValue) progress() else maxIndicatorValue

    val animatedIndicatorValue = remember {
        mutableStateOf(0f)
    }

    LaunchedEffect(allowedIndicatorValue) {
        animatedIndicatorValue.value = allowedIndicatorValue
    }

    val percentage = (animatedIndicatorValue.value / maxIndicatorValue) * 100

    val swipeAngle = animateFloatAsState(
        targetValue = (2.4 * percentage).toFloat(),
        label = "",
        animationSpec = tween(1000)
    )


    Canvas(modifier = Modifier.size(canvasSize)) {
        val componentSize = size/1.25f

        backgroundIndicator(
            componentSize = componentSize,
            indicatorColor = backgroundIndicatorColor,
            indicatorThickness = backgroundIndicatorStrokeWidth.toPx()
        )

        foregroundIndicator(
            swipeAngle = swipeAngle.value,
            componentSize = componentSize,
            indicatorColor = foreGroundColor,
            indicatorThickness = foregroundIndicatorStrokeWidth.toPx()
        )
    }
}

fun DrawScope.backgroundIndicator(
    componentSize: Size,
    indicatorColor:Color ,
    indicatorThickness:Float
){
    drawArc(
        color = indicatorColor,
        startAngle = 150f,
        sweepAngle = 240f,
        useCenter = false,
        style = Stroke(
            width = indicatorThickness,
            cap = StrokeCap.Round
        ),
        topLeft = Offset(
            x = (size.width - componentSize.width) / 2f,
            y = (size.height - componentSize.height) / 2f
        )
    )
}

fun DrawScope.foregroundIndicator(
    swipeAngle:Float,
    componentSize: Size,
    indicatorColor:Color ,
    indicatorThickness:Float
){
    drawArc(
//        color = indicatorColor,
        brush = Brush.linearGradient(
            colors = listOf(
                Color.Red,
                Color.Magenta
            )
        ),
        startAngle = 150f,
        sweepAngle = swipeAngle,
        useCenter = false,
        style = Stroke(
            width = indicatorThickness,
            cap = StrokeCap.Round
        ),
        topLeft = Offset(
            x = (size.width - componentSize.width) / 2f,
            y = (size.height - componentSize.height) / 2f
        )
    )
}
