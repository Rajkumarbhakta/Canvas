package com.rkbapps.canvas.ui.screens.drawing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.navigation.Draw
import com.rkbapps.canvas.ui.screens.drawing.composables.PaintingStyleType
import com.rkbapps.canvas.ui.screens.drawing.composables.ShapeType

sealed interface DrawingAction {
    data object OnNewPathStart : DrawingAction
    data class OnDraw(val offset: Offset) : DrawingAction
    data object OnPathEnd : DrawingAction
    data class OnSelectColor(val color: Color) : DrawingAction
    data object OnClearCanvasList : DrawingAction
    data class OnThicknessChange(val thickness: Float) : DrawingAction
    data class OnPathEffectChange(val pathEffect: PaintingStyleType) : DrawingAction
    data class OnShapeTypeChange(val shapeType: ShapeType) : DrawingAction
    data class OnToggleEraser(val isEraser: Boolean) : DrawingAction
    data class OnBackgroundColorChange(val color: Color) : DrawingAction
    data object OnUndo : DrawingAction
    data object OnRedo : DrawingAction
    data class SaveDesign(val drawingState: DrawingState, val name: String) : DrawingAction

    data object OnOpenNameEditDialog : DrawingAction
    data object OnCloseNameEditDialog: DrawingAction

    data object OnEnterFullScreen: DrawingAction
    data object OnExitFullScreen: DrawingAction

    data object OnEraserSelected: DrawingAction
    data object OnEraserUnselected: DrawingAction

}