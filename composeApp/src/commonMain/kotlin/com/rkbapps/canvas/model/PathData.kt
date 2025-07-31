package com.rkbapps.canvas.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.rkbapps.canvas.ui.screens.drawing.composables.PaintingStyleType
import com.rkbapps.canvas.ui.screens.drawing.composables.ShapeType

/**
 * Represents a single stroke/shape drawn on the canvas
 */
data class PathData(
    val path: List<Offset> = emptyList(),      // For freehand path
    val color: Color = Color.Black,            // Stroke/Fill color
    val thickness: Float = 5f,                 // Stroke thickness
    val pathEffect: PaintingStyleType = PaintingStyleType.STROKE, // Stroke style
    val isEraser: Boolean = false,             // Eraser flag
    val shapeType: ShapeType = ShapeType.NONE, // Shape type (if shape drawing)
    val shapePoints: List<Offset> = emptyList() // Points for shapes (start & end)
)
