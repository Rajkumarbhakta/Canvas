package com.rkbapps.canvas.ui.screens.drawing.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Waves
import androidx.compose.ui.graphics.vector.ImageVector
import canvas.composeapp.generated.resources.Res
import canvas.composeapp.generated.resources.brush_style_dot
import canvas.composeapp.generated.resources.brush_style_fill
import canvas.composeapp.generated.resources.brush_style_pencil
import canvas.composeapp.generated.resources.brush_style_scallop
import canvas.composeapp.generated.resources.brush_style_stroke
import org.jetbrains.compose.resources.StringResource


enum class PaintingStyleType {
    STROKE,
    DOT,
    FILL,
    SCALLOP,
    PENCIL,
}

data class PaintingStyle(
    val title: StringResource,
    val icon: ImageVector,
    val type: PaintingStyleType
)

val paintingStyles = listOf(
    PaintingStyle(
        title = Res.string.brush_style_stroke,
        icon = Icons.Default.HorizontalRule,
        type = PaintingStyleType.STROKE
    ),
    PaintingStyle(
        title = Res.string.brush_style_dot,
        icon = Icons.Default.MoreHoriz,
        type = PaintingStyleType.DOT
    ),
    PaintingStyle(
        title = Res.string.brush_style_fill,
        icon = Icons.Default.RadioButtonChecked,
        type = PaintingStyleType.FILL
    ),
    PaintingStyle(
        title = Res.string.brush_style_scallop,
        icon = Icons.Default.Waves,
        type = PaintingStyleType.SCALLOP
    ),
    PaintingStyle(
        title = Res.string.brush_style_pencil,
        icon = Icons.Default.Edit,
        type = PaintingStyleType.PENCIL
    ),
)
