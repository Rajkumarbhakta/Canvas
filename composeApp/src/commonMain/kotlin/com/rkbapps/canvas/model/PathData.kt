package com.rkbapps.canvas.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.rkbapps.canvas.ui.screens.drawing.composables.PaintingStyleType

@Stable
@Immutable
data class PathData(
    val id: String,
    val color: Color,
    val thickness: Float = 10f,
    val pathEffect: PaintingStyleType = PaintingStyleType.STROKE,
    val path: List<Offset> = emptyList(),
    val isEraser: Boolean = false
)
