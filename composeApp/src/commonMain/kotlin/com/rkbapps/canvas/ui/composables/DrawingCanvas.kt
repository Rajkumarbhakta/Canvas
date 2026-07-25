package com.rkbapps.canvas.ui.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StampedPathEffectStyle
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.util.fastForEach
import com.rkbapps.canvas.model.PathData
import com.rkbapps.canvas.ui.screens.drawing.DrawingAction
import com.rkbapps.canvas.ui.screens.drawing.utils.PaintingStyleType
import com.rkbapps.canvas.ui.screens.drawing.utils.ShapeType
import com.rkbapps.canvas.util.Log
import com.rkbapps.canvas.util.detectTapAndDrag
import kotlin.math.*

@Composable
fun DrawingCanvas(
    paths: List<PathData>,
    currentPath: PathData?,
    backgroundColor:Color,
    onAction: (DrawingAction) -> Unit,
    isSelectionMode: Boolean = false,
    selectedPathId: String? = null,
    dragOffset: Offset = Offset.Zero,
    modifier: Modifier = Modifier
){
    Canvas(
        modifier = modifier
            .clipToBounds()
            .background(backgroundColor)
            .pointerInput(isSelectionMode, paths, selectedPathId) {
                detectTapAndDrag(
                    onTap = { offset->
                        if (isSelectionMode){
                            // Check if hit delete button of the currently selected path
                            val selectedPath = paths.firstOrNull { it.id == selectedPathId }
                            if (selectedPath != null) {
                                val box = selectedPath.getBoundingBox()
                                if (box != null) {
                                    val deleteButtonCenter = Offset(box.right, box.top)
                                    if ((offset - deleteButtonCenter).getDistance() <= 35f) {
                                        onAction(DrawingAction.OnDeleteSelectedPath)
                                        return@detectTapAndDrag
                                    }
                                }
                            }

                            // Otherwise check if hit any path
                            val hitPath = paths.lastOrNull { it.isHit(offset) }
                            if (hitPath != null) {
                                onAction(DrawingAction.OnSelectPath(hitPath.id))
                            } else {
                                onAction(DrawingAction.OnSelectPath(null))
                            }
                        }
                    },
                    onDragStart = { startOffset ->
                        if (!isSelectionMode)
                            onAction(DrawingAction.OnNewPathStart)
                    },
                    onDrag = { change, dragAmount  ->
                        if (isSelectionMode) {
                            if (selectedPathId != null) {
                                onAction(DrawingAction.OnDragSelectedPath(dragAmount))
                            }
                        } else {
                            onAction(DrawingAction.OnDraw(change.position))
                        }
                    },
                    onDragEnd = {
                        onAction(DrawingAction.OnPathEnd)
                    },
                    onDragCancel = {
                        onAction(DrawingAction.OnPathEnd)
                    }
                )
            }
    ){
        paths.fastForEach {
            val isSelected = isSelectionMode && it.id == selectedPathId
            if (isSelected && dragOffset != Offset.Zero) {
                drawContext.canvas.save()
                drawContext.canvas.translate(dragOffset.x, dragOffset.y)
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
                drawContext.canvas.restore()
            } else {
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

        // Draw selection decorations on top of all paths
        if (isSelectionMode && selectedPathId != null) {
            val selectedPath = paths.firstOrNull { it.id == selectedPathId }
            if (selectedPath != null) {
                var box = selectedPath.getBoundingBox()
                if (box != null) {
                    if (dragOffset != Offset.Zero) {
                        box = box.translate(dragOffset)
                    }
                    // Draw bounding box dashed border
                    drawRect(
                        color = Color(0xFF2196F3),
                        topLeft = box.topLeft,
                        size = box.size,
                        style = Stroke(
                            width = 2f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                    )
                    
                    // Draw delete button at top-right
                    val deleteButtonCenter = Offset(box.right, box.top)
                    drawCircle(
                        color = Color.Red,
                        radius = 14f,
                        center = deleteButtonCenter
                    )
                    val crossSize = 6f
                    drawLine(
                        color = Color.White,
                        start = deleteButtonCenter - Offset(crossSize, crossSize),
                        end = deleteButtonCenter + Offset(crossSize, crossSize),
                        strokeWidth = 3f
                    )
                    drawLine(
                        color = Color.White,
                        start = deleteButtonCenter - Offset(crossSize, -crossSize),
                        end = deleteButtonCenter + Offset(crossSize, -crossSize),
                        strokeWidth = 3f
                    )
                }
            }
        }
    }

}

fun PathData.getBoundingBox(): Rect? {
    if (shapeType == ShapeType.NONE) {
        if (path.isEmpty()) return null
        var minX = Float.MAX_VALUE
        var maxX = Float.MIN_VALUE
        var minY = Float.MAX_VALUE
        var maxY = Float.MIN_VALUE
        path.forEach {
            if (it.x < minX) minX = it.x
            if (it.x > maxX) maxX = it.x
            if (it.y < minY) minY = it.y
            if (it.y > maxY) maxY = it.y
        }
        val halfThickness = maxOf(thickness, 10f) / 2f
        return Rect(
            minX - halfThickness,
            minY - halfThickness,
            maxX + halfThickness,
            maxY + halfThickness
        )
    } else {
        if (shapePoints.size < 2) return null
        val start = shapePoints[0]
        val end = shapePoints[1]
        val halfThickness = maxOf(thickness, 10f) / 2f
        
        return when (shapeType) {
            ShapeType.LINE -> {
                Rect(
                    minOf(start.x, end.x) - halfThickness,
                    minOf(start.y, end.y) - halfThickness,
                    maxOf(start.x, end.x) + halfThickness,
                    maxOf(start.y, end.y) + halfThickness
                )
            }
            ShapeType.RECTANGLE, ShapeType.SQUARE -> {
                val minX = minOf(start.x, end.x)
                val maxX = maxOf(start.x, end.x)
                val minY = minOf(start.y, end.y)
                val maxY = maxOf(start.y, end.y)
                if (shapeType == ShapeType.SQUARE) {
                    val size = maxOf(abs(end.x - start.x), abs(end.y - start.y))
                    val endX = if (end.x >= start.x) start.x + size else start.x - size
                    val endY = if (end.y >= start.y) start.y + size else start.y - size
                    Rect(
                        minOf(start.x, endX) - halfThickness,
                        minOf(start.y, endY) - halfThickness,
                        maxOf(start.x, endX) + halfThickness,
                        maxOf(start.y, endY) + halfThickness
                    )
                } else {
                    Rect(
                        minX - halfThickness,
                        minY - halfThickness,
                        maxX + halfThickness,
                        maxY + halfThickness
                    )
                }
            }
            ShapeType.CIRCLE -> {
                val radius = sqrt(
                    (end.x - start.x) * (end.x - start.x) + 
                    (end.y - start.y) * (end.y - start.y)
                )
                Rect(
                    start.x - radius - halfThickness,
                    start.y - radius - halfThickness,
                    start.x + radius + halfThickness,
                    start.y + radius + halfThickness
                )
            }
            ShapeType.OVAL -> {
                val width = abs(end.x - start.x) * 2
                val height = abs(end.y - start.y) * 2
                Rect(
                    start.x - width/2 - halfThickness,
                    start.y - height/2 - halfThickness,
                    start.x + width/2 + halfThickness,
                    start.y + height/2 + halfThickness
                )
            }
            ShapeType.TRIANGLE -> {
                Rect(
                    minOf(start.x, end.x) - halfThickness,
                    minOf(start.y, end.y) - halfThickness,
                    maxOf(start.x, end.x) + halfThickness,
                    maxOf(start.y, end.y) + halfThickness
                )
            }
            ShapeType.ARROW_RIGHT, ShapeType.ARROW_LEFT, ShapeType.ARROW_UP, ShapeType.ARROW_DOWN -> {
                val minX = minOf(start.x, end.x)
                val maxX = maxOf(start.x, end.x)
                val minY = minOf(start.y, end.y)
                val maxY = maxOf(start.y, end.y)
                Rect(
                    minX - halfThickness - 20f,
                    minY - halfThickness - 20f,
                    maxX + halfThickness + 20f,
                    maxY + halfThickness + 20f
                )
            }
            ShapeType.STAR, ShapeType.PENTAGON, ShapeType.HEXAGON -> {
                val radius = sqrt(
                    (end.x - start.x) * (end.x - start.x) + 
                    (end.y - start.y) * (end.y - start.y)
                )
                Rect(
                    start.x - radius - halfThickness,
                    start.y - radius - halfThickness,
                    start.x + radius + halfThickness,
                    start.y + radius + halfThickness
                )
            }
        }
    }
}

fun distanceToSegment(p: Offset, a: Offset, b: Offset): Float {
    val ab = b - a
    val ap = p - a
    val abLenSq = ab.x * ab.x + ab.y * ab.y
    if (abLenSq == 0f) return (p - a).getDistance()
    
    var t = (ap.x * ab.x + ap.y * ab.y) / abLenSq
    t = maxOf(0f, minOf(1f, t))
    val projection = a + ab * t
    return (p - projection).getDistance()
}

fun distanceToPath(p: Offset, path: List<Offset>): Float {
    if (path.isEmpty()) return Float.MAX_VALUE
    if (path.size == 1) return (p - path[0]).getDistance()
    var minDistance = Float.MAX_VALUE
    for (i in 0 until path.size - 1) {
        val d = distanceToSegment(p, path[i], path[i+1])
        if (d < minDistance) {
            minDistance = d
        }
    }
    return minDistance
}

fun PathData.isHit(offset: Offset): Boolean {
    val threshold = maxOf(thickness, 10f) / 2f + 30f // 30f is hit padding for easy selection
    
    if (shapeType == ShapeType.NONE) {
        if (path.isEmpty()) return false
        return distanceToPath(offset, path) <= threshold
    } else {
        if (shapePoints.size < 2) return false
        val start = shapePoints[0]
        val end = shapePoints[1]
        
        return when (shapeType) {
            ShapeType.LINE -> {
                distanceToSegment(offset, start, end) <= threshold
            }
            ShapeType.RECTANGLE, ShapeType.SQUARE, ShapeType.TRIANGLE -> {
                val box = getBoundingBox() ?: return false
                box.contains(offset)
            }
            ShapeType.CIRCLE -> {
                val radius = sqrt(
                    (end.x - start.x) * (end.x - start.x) + 
                    (end.y - start.y) * (end.y - start.y)
                )
                val distToCenter = (offset - start).getDistance()
                distToCenter <= radius + threshold
            }
            ShapeType.OVAL -> {
                val box = getBoundingBox() ?: return false
                box.contains(offset)
            }
            ShapeType.STAR, ShapeType.PENTAGON, ShapeType.HEXAGON -> {
                val box = getBoundingBox() ?: return false
                box.contains(offset)
            }
            else -> false
        }
    }
}

fun DrawScope.drawPath(
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
                val dx = abs(from.x-to.x)
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
        when(pathEffect){
            PaintingStyleType.PENCIL->{
                drawPath(
                    path = smoothPath,
                    color = color.copy(alpha = 0.15f),
                    style = Stroke(width = thickness * 2f, cap = StrokeCap.Round)
                )
            }
            else->{}
        }
        drawPath(
            path = smoothPath,
            color = color,
            style = getDrawStyle(thickness,pathEffect)
        )
    }
}


fun getDrawStyle(thickness: Float,style: PaintingStyleType): DrawStyle {
    return when(style){
        PaintingStyleType.DOT -> {

            val dotLength = thickness          // size of each dot
            val gapLength = thickness * 2.5f   // space between dots

            Stroke(
                width = thickness,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(dotLength,gapLength), 0f)
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
        PaintingStyleType.SCALLOP -> {
            Stroke(
                width = thickness,
                pathEffect = scallopEffect(thickness)
            )
        }
        PaintingStyleType.PENCIL -> {
            Stroke(width = thickness * 0.6f, cap = StrokeCap.Round)
        }
    }
}


fun scallopEffect(thickness: Float): PathEffect {
    val wavePath = Path().apply {
        moveTo(0f, 0f)
        quadraticTo(
            thickness, thickness,
            thickness * 2, 0f
        )
    }

    return PathEffect.stampedPathEffect(
        shape = wavePath,
        advance = thickness * 2,
        phase = 0f,
        style = StampedPathEffectStyle.Rotate
    )
}


// Function to draw different shapes
fun DrawScope.drawShape(
    points: List<Offset>,
    color: Color,
    thickness: Float,
    pathEffect: PaintingStyleType,
    shapeType: ShapeType
) {
    if (points.size < 2) return
    
    val start = points[0]
    val end = points[1]
    
    val style = getDrawStyle(thickness,pathEffect)
    
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

