package com.rkbapps.canvas.ui.screens.drawing

import com.rkbapps.canvas.ui.screens.drawing.composables.ShapeType

sealed class DrawingAction {
    data class SaveDesign(val state: DrawingState, val name: String) : DrawingAction()
    object OnUndo : DrawingAction()
    object OnRedo : DrawingAction()
    object OnClearCanvasList : DrawingAction()
    object OnEraserSelected : DrawingAction()
    object OnEraserUnselected : DrawingAction()
    data class OnToggleEraser(val enabled: Boolean) : DrawingAction()
    data class OnSelectColor(val color: Long) : DrawingAction()
    data class OnBackgroundColorChange(val color: Long) : DrawingAction()
    data class OnThicknessChange(val thickness: Float) : DrawingAction()
    data class OnPathEffectChange(val effect: Any) : DrawingAction()

    // 👇 NEW ACTION
    data class OnShapeTypeChange(val shapeType: ShapeType) : DrawingAction()

    object OnOpenNameEditDialog : DrawingAction()
    object OnCloseNameEditDialog : DrawingAction()
    object OnEnterFullScreen : DrawingAction()
    object OnExitFullScreen : DrawingAction()
}
