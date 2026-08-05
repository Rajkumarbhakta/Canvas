package com.rkbapps.canvas.ui.screens.drawing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.ui.screens.drawing.utils.PaintingStyleType
import com.rkbapps.canvas.ui.screens.drawing.utils.ShapeType

sealed interface DrawingAction {
    // ── Drawing ───────────────────────────────────────────────────────────────
    data object OnNewPathStart : DrawingAction
    data class OnDraw(val offset: Offset) : DrawingAction
    data object OnPathEnd : DrawingAction

    // ── Tool selection ────────────────────────────────────────────────────────
    data class OnSelectColor(val color: Color) : DrawingAction
    data object OnClearCanvasList : DrawingAction
    data class OnThicknessChange(val thickness: Float) : DrawingAction
    data class OnPathEffectChange(val pathEffect: PaintingStyleType) : DrawingAction
    data class OnShapeTypeChange(val shapeType: ShapeType) : DrawingAction
    data class OnToggleEraser(val isEraser: Boolean) : DrawingAction
    data class OnBackgroundColorChange(val color: Color) : DrawingAction

    // ── History ───────────────────────────────────────────────────────────────
    data object OnUndo : DrawingAction
    data object OnRedo : DrawingAction

    // ── Persistence ───────────────────────────────────────────────────────────
    data class SaveDesign(val drawingState: DrawingState, val name: String) : DrawingAction

    // ── UI dialogs ────────────────────────────────────────────────────────────
    data object OnOpenNameEditDialog : DrawingAction
    data object OnCloseNameEditDialog : DrawingAction
    data object OnEnterFullScreen : DrawingAction
    data object OnExitFullScreen : DrawingAction
    data object OnEraserSelected : DrawingAction
    data object OnEraserUnselected : DrawingAction

    // ── Share / export ────────────────────────────────────────────────────────
    data object OnShareDrawing : DrawingAction
    data object OnSaveAsImage : DrawingAction

    // ── Selection mode ────────────────────────────────────────────────────────
    data class OnToggleSelectionMode(val isSelection: Boolean) : DrawingAction
    data class OnSelectPath(val pathId: String?) : DrawingAction
    data class OnDragSelectedPath(val dragAmount: Offset) : DrawingAction
    data object OnDeleteSelectedPath : DrawingAction

    // ── Milestone 1: Zoom / Pan / Page size ───────────────────────────────────
    /** Called by 2-finger gesture; [zoomDelta] is multiplicative, [panDelta] additive. */
    data class OnZoomPan(val zoomDelta: Float, val panDelta: Offset) : DrawingAction
    /** Reset zoom to 1× and pan to zero. */
    data object OnResetView : DrawingAction
    /** Change the page dimensions (A4, A3, Letter, etc.). */
    data class OnSetPageSize(
        val width: Float,
        val height: Float,
        val label: String,
    ) : DrawingAction
    data object OnOpenPageSizePicker : DrawingAction
    data object OnClosePageSizePicker : DrawingAction

    // ── Milestone 2: Multi-page ───────────────────────────────────────────────
    data object OnAddPage : DrawingAction
    data class OnSelectPage(val index: Int) : DrawingAction
    data class OnDeletePage(val index: Int) : DrawingAction
}