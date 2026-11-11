package com.rkbapps.canvas.ui.screens.drawing.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.graphics.vector.ImageVector
import canvas.composeapp.generated.resources.Res
import canvas.composeapp.generated.resources.shape_arrow_down
import canvas.composeapp.generated.resources.shape_arrow_left
import canvas.composeapp.generated.resources.shape_arrow_right
import canvas.composeapp.generated.resources.shape_arrow_up
import canvas.composeapp.generated.resources.shape_circle
import canvas.composeapp.generated.resources.shape_hexagon
import canvas.composeapp.generated.resources.shape_line
import canvas.composeapp.generated.resources.shape_none
import canvas.composeapp.generated.resources.shape_pentagon
import canvas.composeapp.generated.resources.shape_rectangle
import canvas.composeapp.generated.resources.shape_square
import canvas.composeapp.generated.resources.shape_star
import canvas.composeapp.generated.resources.shape_triangle
import org.jetbrains.compose.resources.StringResource

data class ShapeOption(
    val type: ShapeType,
    val icon: ImageVector,
    val title: StringResource
)

val shapeOptions = listOf(
    ShapeOption(type = ShapeType.NONE, icon = Icons.Filled.Close, title = Res.string.shape_none),
    ShapeOption(type = ShapeType.LINE, icon = Icons.Filled.HorizontalRule, title = Res.string.shape_line),
    ShapeOption(type = ShapeType.RECTANGLE, icon = Icons.Outlined.Rectangle, title = Res.string.shape_rectangle),
    ShapeOption(type = ShapeType.SQUARE, icon = Icons.Outlined.Square, title = Res.string.shape_square),
    ShapeOption(type = ShapeType.CIRCLE, icon = Icons.Outlined.Circle, title = Res.string.shape_circle),
    ShapeOption(type = ShapeType.TRIANGLE, icon = Icons.Outlined.ChangeHistory, title = Res.string.shape_triangle),
    ShapeOption(type = ShapeType.ARROW_LEFT, icon = Icons.AutoMirrored.Outlined.ArrowBack, title = Res.string.shape_arrow_left),
    ShapeOption(type = ShapeType.ARROW_RIGHT, icon = Icons.AutoMirrored.Outlined.ArrowForward, title = Res.string.shape_arrow_right),
    ShapeOption(type = ShapeType.ARROW_UP, icon = Icons.Outlined.ArrowUpward, title = Res.string.shape_arrow_up),
    ShapeOption(type = ShapeType.ARROW_DOWN, icon = Icons.Outlined.ArrowDownward, title = Res.string.shape_arrow_down),
    ShapeOption(type = ShapeType.STAR, icon = Icons.Outlined.Star, title = Res.string.shape_star),
    ShapeOption(type = ShapeType.PENTAGON, icon = Icons.Outlined.Pentagon, title = Res.string.shape_pentagon),
    ShapeOption(type = ShapeType.HEXAGON, icon = Icons.Outlined.Hexagon, title = Res.string.shape_hexagon),
)