package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
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
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.ChangeHistory
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Hexagon
import androidx.compose.material.icons.outlined.Pentagon
import androidx.compose.material.icons.outlined.Rectangle
import androidx.compose.material.icons.outlined.Square
import androidx.compose.material.icons.outlined.Star

val shapeOptions = listOf(
    ShapeOption(
        type = ShapeType.NONE,
        icon = Icons.Filled.Close,
        title = "None"
    ),
    ShapeOption(
        type = ShapeType.LINE,
        icon = Icons.Filled.HorizontalRule,
        title = "Line"
    ),
    ShapeOption(
        type = ShapeType.RECTANGLE,
        icon = Icons.Outlined.Rectangle,
        title = "Rectangle"
    ),
    ShapeOption(
        type = ShapeType.SQUARE,
        icon = Icons.Outlined.Square,
        title = "Square"
    ),
    ShapeOption(
        type = ShapeType.CIRCLE,
        icon = Icons.Outlined.Circle,
        title = "Circle"
    ),
    ShapeOption(
        type = ShapeType.TRIANGLE,
        icon = Icons.Outlined.ChangeHistory,
        title = "Triangle"
    ),
    ShapeOption(
        type = ShapeType.ARROW_LEFT,
        icon = Icons.AutoMirrored.Outlined.ArrowBack,
        title = "Arrow Left"
    ),
    ShapeOption(
        type = ShapeType.ARROW_RIGHT,
        icon = Icons.AutoMirrored.Outlined.ArrowForward,
        title = "Arrow Right"
    ),
    ShapeOption(
        type = ShapeType.ARROW_UP,
        icon = Icons.Outlined.ArrowUpward,
        title = "Arrow Up"
    ),
    ShapeOption(
        type = ShapeType.ARROW_DOWN,
        icon = Icons.Outlined.ArrowDownward,
        title = "Arrow Down"
    ),
    ShapeOption(
        type = ShapeType.STAR,
        icon = Icons.Outlined.Star,
        title = "Star"
    ),
    ShapeOption(
        type = ShapeType.PENTAGON,
        icon = Icons.Outlined.Pentagon,
        title = "Pentagon"
    ),
    ShapeOption(
        type = ShapeType.HEXAGON,
        icon = Icons.Outlined.Hexagon,
        title = "Hexagon"
    )
)