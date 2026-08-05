package com.rkbapps.canvas.ui.screens.drawing

import androidx.compose.ui.geometry.Offset
import com.rkbapps.canvas.ui.screens.drawing.utils.ShapeType

data class DrawingScreenState(
    val isEditDrawingNameDialogVisible: Boolean = false,
    val isEraserSelected: Boolean = false,
    val isFullScreen: Boolean = false,
    val isSelectionMode: Boolean = false,
    val selectedShapeType: ShapeType = ShapeType.NONE,
    val selectedPathId: String? = null,

    // ── Milestone 1: zoom / pan ───────────────────────────────────────────────
    /** Current zoom scale. 1f = 100 %. Clamped to [MIN_ZOOM]..[MAX_ZOOM]. */
    val zoom: Float = 1f,
    /** Pan translation applied to the page frame, in screen pixels. */
    val panOffset: Offset = Offset.Zero,
    val minZoom: Float = MIN_ZOOM,
    val maxZoom: Float = MAX_ZOOM,
    /** Whether this drawing was created before the page-coordinate system.
     *  Legacy drawings render full-screen with no page frame. */
    val isLegacy: Boolean = false,

    // ── Milestone 2: multi-page ───────────────────────────────────────────────
    /** Index of the currently active page in [DrawingState.pages]. */
    val currentPageIndex: Int = 0,

    /** True while a page-size picker dialog should be shown. */
    val isPageSizePickerVisible: Boolean = false,
) {
    companion object {
        /** Minimum allowed zoom level (25 %). */
        const val MIN_ZOOM = 0.25f
        /** Maximum allowed zoom level (500 %). */
        const val MAX_ZOOM = 5f
    }
}
