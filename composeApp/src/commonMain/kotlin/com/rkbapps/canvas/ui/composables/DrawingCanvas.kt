package com.rkbapps.canvas.ui.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.util.fastForEach
import com.rkbapps.canvas.model.PathData
import com.rkbapps.canvas.ui.screens.drawing.DrawingAction
import com.rkbapps.canvas.ui.screens.drawing.composables.PaintingStyleType
import com.rkbapps.canvas.ui.screens.drawing.composables.ShapeType
import kotlin.math.abs

@Composable
fun DrawingCanvas(
    paths: List<PathData>,
    currentPath: PathData?,
    backgroundColor:Color,
    onAction: (DrawingAction) -> Unit,
    modifier: Modifier = Modifier
){
    Canvas(
        modifier = modifier
            .clipToBounds()
            .background(backgroundColor)
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
                it.thickness,
                it.pathEffect,
                isEraser = it.isEraser,
                backgroundColor,
                it.shapeType,
                it.shapePoints
            )
        }
        currentPath?.let {
            drawPath(
                it.path,
                it.color,
                it.thickness,
                it.pathEffect,
                isEraser = it.isEraser,
                backgroundColor,
                it.shapeType,
                it.shapePoints
            )
        }
    }

}

internal fun DrawScope.drawPath(
    path: List<Offset>,
    color:Color,
    thickness: Float = 10f,
    pathEffect: PaintingStyleType,
    isEraser: Boolean = false,
    backgroundColor:Color = Color.White,
    shapeType: ShapeType = ShapeType.NONE,
    shapePoints: List<Offset> = emptyList()
){
    // If it's a shape and we have shape points, draw the shape
    if (shapeType != ShapeType.NONE && shapePoints.size >= 2) {
        drawShape(shapePoints, color, thickness, pathEffect, shapeType)
        return
    }
    
    // Otherwise draw a regular path
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

    if (isEraser){
        drawPath(
            path = smoothPath,
            color = backgroundColor,
            style = Stroke(
                width = thickness,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            ),
            blendMode = BlendMode.Src
        )
    }else{
        drawPath(
            path = smoothPath,
            color = color,
            style = when(pathEffect){
                PaintingStyleType.DOT -> {
                    Stroke(
                        width = thickness,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f, 15f), 0f)
                    )
                }
                PaintingStyleType.STROKE -> {
                    Stroke(
                        width = thickness,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                }
                PaintingStyleType.FILL -> {
                    Fill
                }
            }
        )
    }
}

// Function to draw different shapes
internal fun DrawScope.drawShape(
    points: List<Offset>,
    color: Color,
    thickness: Float,
    pathEffect: PaintingStyleType,
    shapeType: ShapeType
) {
    if (points.size < 2) return
    
    val start = points[0]
    val end = points[1]
    
    val style = when(pathEffect) {
        PaintingStyleType.DOT -> {
            Stroke(
                width = thickness,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f, 15f), 0f)
            )
        }
        PaintingStyleType.STROKE -> {
            Stroke(
                width = thickness,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        }
        PaintingStyleType.FILL -> {
            Fill
        }
    }
    
    val shapePath = Path()
    
    when (shapeType) {
        ShapeType.LINE -> {
            // Draw a line
            drawLine(
                color = color,
                start = start,
                end = end,
                strokeWidth = thickness,
                cap = StrokeCap.Round
            )
        }
        ShapeType.RECTANGLE -> {
            // Draw a rectangle
            shapePath.apply {
                moveTo(start.x, start.y)
                lineTo(end.x, start.y)
                lineTo(end.x, end.y)
                lineTo(start.x, end.y)
                close()
            }
            drawPath(shapePath, color, style = style)
        }
        ShapeType.SQUARE -> {
            // Draw a square
            val size = maxOf(kotlin.math.abs(end.x - start.x), kotlin.math.abs(end.y - start.y))
            val endX = if (end.x >= start.x) start.x + size else start.x - size
            val endY = if (end.y >= start.y) start.y + size else start.y - size
            
            shapePath.apply {
                moveTo(start.x, start.y)
                lineTo(endX, start.y)
                lineTo(endX, endY)
                lineTo(start.x, endY)
                close()
            }
            drawPath(shapePath, color, style = style)
        }
        ShapeType.CIRCLE -> {
            // Draw a circle
            val radius = kotlin.math.sqrt(
                (end.x - start.x) * (end.x - start.x) + 
                (end.y - start.y) * (end.y - start.y)
            )
            drawCircle(color, radius, start, style = style)
        }
        ShapeType.OVAL -> {
            // Draw an oval
            val width = kotlin.math.abs(end.x - start.x) * 2
            val height = kotlin.math.abs(end.y - start.y) * 2
            drawOval(
                color = color,
                topLeft = Offset(start.x - width/2, start.y - height/2),
                size = androidx.compose.ui.geometry.Size(width, height),
                style = style
            )
        }
        ShapeType.TRIANGLE -> {
            // Draw a triangle
            shapePath.apply {
                moveTo(start.x, end.y)
                lineTo((start.x + end.x) / 2, start.y)
                lineTo(end.x, end.y)
                close()
            }
            drawPath(shapePath, color, style = style)
        }
        ShapeType.ARROW_RIGHT -> {
            // Draw right arrow
            val arrowLength = end.x - start.x
            val arrowHeight = kotlin.math.abs(end.y - start.y) / 3
            
            // Draw the line
            drawLine(
                color = color,
                start = start,
                end = Offset(end.x - arrowHeight, start.y),
                strokeWidth = thickness,
                cap = StrokeCap.Round
            )
            
            // Draw the arrowhead
            shapePath.apply {
                moveTo(end.x, start.y)
                lineTo(end.x - arrowHeight, start.y - arrowHeight)
                lineTo(end.x - arrowHeight, start.y + arrowHeight)
                close()
            }
            drawPath(shapePath, color, style = style)
        }
        ShapeType.ARROW_LEFT -> {
            // Draw left arrow
            val arrowLength = start.x - end.x
            val arrowHeight = kotlin.math.abs(end.y - start.y) / 3
            
            // Draw the line
            drawLine(
                color = color,
                start = start,
                end = Offset(end.x + arrowHeight, start.y),
                strokeWidth = thickness,
                cap = StrokeCap.Round
            )
            
            // Draw the arrowhead
            shapePath.apply {
                moveTo(end.x, start.y)
                lineTo(end.x + arrowHeight, start.y - arrowHeight)
                lineTo(end.x + arrowHeight, start.y + arrowHeight)
                close()
            }
            drawPath(shapePath, color, style = style)
        }
        ShapeType.ARROW_UP -> {
            // Draw up arrow
            val arrowLength = start.y - end.y
            val arrowWidth = kotlin.math.abs(end.x - start.x) / 3
            
            // Draw the line
            drawLine(
                color = color,
                start = start,
                end = Offset(start.x, end.y + arrowWidth),
                strokeWidth = thickness,
                cap = StrokeCap.Round
            )
            
            // Draw the arrowhead
            shapePath.apply {
                moveTo(start.x, end.y)
                lineTo(start.x - arrowWidth, end.y + arrowWidth)
                lineTo(start.x + arrowWidth, end.y + arrowWidth)
                close()
            }
            drawPath(shapePath, color, style = style)
        }
        ShapeType.ARROW_DOWN -> {
            // Draw down arrow
            val arrowLength = end.y - start.y
            val arrowWidth = kotlin.math.abs(end.x - start.x) / 3
            
            // Draw the line
            drawLine(
                color = color,
                start = start,
                end = Offset(start.x, end.y - arrowWidth),
                strokeWidth = thickness,
                cap = StrokeCap.Round
            )
            
            // Draw the arrowhead
            shapePath.apply {
                moveTo(start.x, end.y)
                lineTo(start.x - arrowWidth, end.y - arrowWidth)
                lineTo(start.x + arrowWidth, end.y - arrowWidth)
                close()
            }
            drawPath(shapePath, color, style = style)
        }
        ShapeType.STAR -> {
            // Draw a star (5-pointed)
            val radius = kotlin.math.sqrt(
                (end.x - start.x) * (end.x - start.x) + 
                (end.y - start.y) * (end.y - start.y)
            )
            val innerRadius = radius * 0.4f
            
            shapePath.apply {
                val centerX = start.x
                val centerY = start.y
                
                for (i in 0 until 10) {
                    val angle = kotlin.math.PI / 2 + i * kotlin.math.PI / 5
                    val r = if (i % 2 == 0) radius else innerRadius
                    val x = centerX + (r * kotlin.math.cos(angle)).toFloat()
                    val y = centerY - (r * kotlin.math.sin(angle)).toFloat()
                    
                    if (i == 0) {
                        moveTo(x, y)
                    } else {
                        lineTo(x, y)
                    }
                }
                close()
            }
            drawPath(shapePath, color, style = style)
        }
        ShapeType.PENTAGON -> {
            // Draw a pentagon
            val radius = kotlin.math.sqrt(
                (end.x - start.x) * (end.x - start.x) + 
                (end.y - start.y) * (end.y - start.y)
            )
            
            shapePath.apply {
                val centerX = start.x
                val centerY = start.y
                
                for (i in 0 until 5) {
                    val angle = kotlin.math.PI / 2 + i * 2 * kotlin.math.PI / 5
                    val x = centerX + (radius * kotlin.math.cos(angle)).toFloat()
                    val y = centerY - (radius * kotlin.math.sin(angle)).toFloat()
                    
                    if (i == 0) {
                        moveTo(x, y)
                    } else {
                        lineTo(x, y)
                    }
                }
                close()
            }
            drawPath(shapePath, color, style = style)
        }
        ShapeType.HEXAGON -> {
            // Draw a hexagon
            val radius = kotlin.math.sqrt(
                (end.x - start.x) * (end.x - start.x) + 
                (end.y - start.y) * (end.y - start.y)
            )
            
            shapePath.apply {
                val centerX = start.x
                val centerY = start.y
                
                for (i in 0 until 6) {
                    val angle = i * 2 * kotlin.math.PI / 6
                    val x = centerX + (radius * kotlin.math.cos(angle)).toFloat()
                    val y = centerY + (radius * kotlin.math.sin(angle)).toFloat()
                    
                    if (i == 0) {
                        moveTo(x, y)
                    } else {
                        lineTo(x, y)
                    }
                }
                close()
            }
            drawPath(shapePath, color, style = style)
        }
        else -> { /* Do nothing for NONE */ }
    }
}

//dashPathEffect(floatArrayOf(2f, 25f), 0f)

