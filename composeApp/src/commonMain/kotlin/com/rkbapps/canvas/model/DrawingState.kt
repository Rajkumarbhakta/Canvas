package com.rkbapps.canvas.ui.screens.drawing

import com.rkbapps.canvas.ui.screens.drawing.composables.ShapeType

data class DrawingState(
    val paths: List<PathData> = emptyList(),
    val currentPath: PathData? = null,
    val selectedColor: Long = 0xFF000000,
    val selectedThickness: Float = 5f,
    val selectedPathEffect: Any? = null,
    val backgroundColor: Long = 0xFFFFFFFF,

    // 👇 NEW FIELD
    val selectedShapeType: ShapeType = ShapeType.NONE
)
