package com.rkbapps.canvas.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.rkbapps.canvas.ui.screens.drawing.utils.PaintingStyleType
import com.rkbapps.canvas.ui.screens.drawing.utils.ShapeType
import com.rkbapps.canvas.util.serializers.SafeOffsetListSerializer
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
    @Serializable(with = SafeOffsetListSerializer::class)
    val path: List<Offset> = emptyList(),
    val isEraser: Boolean = false,
    val shapeType: ShapeType = ShapeType.NONE,
    @Serializable(with = SafeOffsetListSerializer::class)
    val shapePoints: List<Offset> = emptyList() // Start and end points for shapes
)
