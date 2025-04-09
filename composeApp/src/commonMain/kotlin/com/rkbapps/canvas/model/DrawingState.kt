package com.rkbapps.canvas.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Immutable
@Stable
data class DrawingState(
    val selectedColor: Color = Color.Blue,
    val selectedThickness: Float = 10f,
    val currentPath: PathData? = null,
    val paths : List<PathData> = emptyList()
)
