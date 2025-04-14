package com.rkbapps.canvas.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.rkbapps.canvas.ui.screens.drawing.composables.PaintingStyleType
import com.rkbapps.canvas.util.ColorSerializer
import com.rkbapps.canvas.util.ListOffsetSerializer
import com.rkbapps.canvas.util.OffsetSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Stable
@Immutable
@Serializable
data class PathData(
    val id: String,
    @Contextual
    val color: Color,
    val thickness: Float = 10f,
    val pathEffect: PaintingStyleType = PaintingStyleType.STROKE,
    @Serializable(with = ListOffsetSerializer::class)
    val path: List<Offset> = emptyList(),
    val isEraser: Boolean = false
)
