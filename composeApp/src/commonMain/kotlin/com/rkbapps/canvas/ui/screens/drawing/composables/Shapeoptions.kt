package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Square
import androidx.compose.material.icons.filled.ChangeHistory
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Hexagon
import androidx.compose.material.icons.filled.Pentagon
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Rectangle
import androidx.compose.material.icons.filled.Remove
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * List of available shapes with icons and titles
 */
val shapeOptions: List<ShapeOption> = listOf(
    ShapeOption(
        type = ShapeType.LINE,
        icon = Icons.Default.Remove,
        title = "Line"
    ),
    ShapeOption(
        type = ShapeType.RECTANGLE,
        icon = Icons.Default.Rectangle,
        title = "Rectangle"
    ),
    ShapeOption(
        type = ShapeType.SQUARE,
        icon = Icons.Default.Square,
        title = "Square"
    ),
    ShapeOption(
        type = ShapeType.CIRCLE,
        icon = Icons.Default.Circle,
        title = "Circle"
    ),
    ShapeOption(
        type = ShapeType.OVAL,
        icon = Icons.Default.Circle, // fallback
        title = "Oval"
    ),
    ShapeOption(
        type = ShapeType.TRIANGLE,
        icon = Icons.Default.ChangeHistory,
        title = "Triangle"
    ),
    ShapeOption(
        type = ShapeType.STAR,
        icon = Icons.Default.Star,
        title = "Star"
    ),
    ShapeOption(
        type = ShapeType.PENTAGON,
        icon = Icons.Default.Pentagon,
        title = "Pentagon"
    ),
    ShapeOption(
        type = ShapeType.HEXAGON,
        icon = Icons.Default.Hexagon,
        title = "Hexagon"
    ),
    ShapeOption(
        type = ShapeType.ARROW_LEFT,
        icon = Icons.Default.ArrowBack,
        title = "Arrow Left"
    ),
    ShapeOption(
        type = ShapeType.ARROW_RIGHT,
        icon = Icons.Default.ArrowForward,
        title = "Arrow Right"
    ),
    ShapeOption(
        type = ShapeType.ARROW_UP,
        icon = Icons.Default.ArrowUpward,
        title = "Arrow Up"
    ),
    ShapeOption(
        type = ShapeType.ARROW_DOWN,
        icon = Icons.Default.ArrowDownward,
        title = "Arrow Down"
    )
)
