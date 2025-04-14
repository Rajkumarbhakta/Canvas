package com.rkbapps.canvas.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.rkbapps.canvas.ui.screens.drawing.composables.PaintingStyleType
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable


@Immutable
@Stable
@Serializable
data class DrawingState(
    @Contextual
    val selectedColor: Color = Color.Blue,
    val selectedThickness: Float = 10f,
    val selectedPathEffect: PaintingStyleType = PaintingStyleType.STROKE,
    val currentPath: PathData? = null,
    val paths : List<PathData> = emptyList(),
    val isEraserMode: Boolean = false,
    @Contextual
    val backgroundColor:Color = Color.White,
    val undoStack: List<List<PathData>> = emptyList(),
    val redoStack: List<List<PathData>> = emptyList()
)
