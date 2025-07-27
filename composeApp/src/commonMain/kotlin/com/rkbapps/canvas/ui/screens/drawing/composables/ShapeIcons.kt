package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Hexagon
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.material.icons.filled.Pentagon
import androidx.compose.material.icons.filled.Rectangle
import androidx.compose.material.icons.filled.Square
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ChangeHistory

val shapeOptions = listOf(
    ShapeOption(
        type = ShapeType.NONE,
        icon = Icons.Filled.Close,
        title = "None"
    ),
    ShapeOption(
        type = ShapeType.LINE,
        icon = Icons.Filled.Minimize,
        title = "Line"
    ),
    ShapeOption(
        type = ShapeType.RECTANGLE,
        icon = Icons.Filled.Rectangle,
        title = "Rectangle"
    ),
    ShapeOption(
        type = ShapeType.SQUARE,
        icon = Icons.Filled.Square,
        title = "Square"
    ),
    ShapeOption(
        type = ShapeType.CIRCLE,
        icon = Icons.Filled.Circle,
        title = "Circle"
    ),
    ShapeOption(
        type = ShapeType.TRIANGLE,
        icon = Icons.Filled.ChangeHistory,
        title = "Triangle"
    ),
    ShapeOption(
        type = ShapeType.ARROW_LEFT,
        icon = Icons.AutoMirrored.Filled.ArrowBack,
        title = "Arrow Left"
    ),
    ShapeOption(
        type = ShapeType.ARROW_RIGHT,
        icon = Icons.AutoMirrored.Filled.ArrowForward,
        title = "Arrow Right"
    ),
    ShapeOption(
        type = ShapeType.ARROW_UP,
        icon = Icons.Filled.ArrowUpward,
        title = "Arrow Up"
    ),
    ShapeOption(
        type = ShapeType.ARROW_DOWN,
        icon = Icons.Filled.ArrowDownward,
        title = "Arrow Down"
    ),
    ShapeOption(
        type = ShapeType.STAR,
        icon = Icons.Filled.Star,
        title = "Star"
    ),
    ShapeOption(
        type = ShapeType.PENTAGON,
        icon = Icons.Filled.Pentagon,
        title = "Pentagon"
    ),
    ShapeOption(
        type = ShapeType.HEXAGON,
        icon = Icons.Filled.Hexagon,
        title = "Hexagon"
    )
)