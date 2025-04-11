package com.rkbapps.canvas.ui.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.util.fastForEach
import com.rkbapps.canvas.model.PathData
import com.rkbapps.canvas.ui.screens.drawing.DrawingAction
import kotlin.math.abs

@Composable
fun DrawingCanvas(
    paths: List<PathData>,
    currentPath: PathData?,
    onAction: (DrawingAction) -> Unit,
    modifier: Modifier = Modifier
){
    Canvas(
        modifier = modifier
            .clipToBounds()
            .background(Color.White)
            .pointerInput(true) {
                detectDragGestures (
                    onDragStart = {
                        onAction(DrawingAction.OnNewPathStart)
                    },
                    onDragEnd = {
                        onAction(DrawingAction.OnPathEnd)
                    },
                    onDrag = { change, dragAmount ->
                        onAction(DrawingAction.OnDraw(change.position))
                    },
                    onDragCancel = {
                        onAction(DrawingAction.OnPathEnd)
                    }
                )
            }
    ){
        paths.fastForEach {
            drawPath(
                it.path,
                it.color,
                it.thickness
            )
        }
        currentPath?.let {
            drawPath(
                it.path,
                it.color,
                it.thickness
            )
        }
    }

}

private fun DrawScope.drawPath(
    path: List<Offset>,
    color:Color,
    thickness: Float = 10f
){
    val smoothPath = Path().apply {
        if (path.isNotEmpty()){
            moveTo(path.first().x,path.first().y)

            val smoothness = 5
            for(i in 1..path.lastIndex){
                val from = path[i-1]
                val to = path[i]
                val dx = abs(from.x-to.y)
                val dy = abs(from.y-to.y)
                if (dy>=smoothness || dx>=smoothness){
                    quadraticTo(
                        x1 = (from.x+to.x)/2f,
                        y1 = (from.y+to.y)/2f,
                        x2= to.x ,
                        y2 = to.y

                    )
                }
            }

        }
    }

    drawPath(
        path = smoothPath,
        color = color,
        style = Stroke(
            width = thickness,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f, 10f), 0f)
        )
    )
}

