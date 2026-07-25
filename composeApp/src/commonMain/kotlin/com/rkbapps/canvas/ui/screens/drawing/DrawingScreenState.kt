package com.rkbapps.canvas.ui.screens.drawing

import com.rkbapps.canvas.ui.screens.drawing.utils.ShapeType

data class DrawingScreenState(
    val isEditDrawingNameDialogVisible: Boolean = false,
    val isEraserSelected: Boolean = false,
    val isFullScreen: Boolean = false,
    val isSelectionMode: Boolean = false,
    val selectedShapeType: ShapeType = ShapeType.NONE,
    val selectedPathId: String? = null,
)
