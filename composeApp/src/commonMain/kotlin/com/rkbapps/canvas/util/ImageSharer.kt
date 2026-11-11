package com.rkbapps.canvas.util

import com.rkbapps.canvas.model.DrawingState

/**
 * Platform-agnostic contract for exporting a drawing to an image file or share sheet.
 *
 * [isLegacy] and [currentPageIndex] are passed explicitly so implementations can correctly
 * locate the active page's paths (paged mode) or fall back to the flat [DrawingState.paths]
 * list (legacy mode) without needing access to UI state.
 */
interface ImageSharer {
    /**
     * Opens a system share-sheet / "open with" dialog for the rendered drawing.
     *
     * @param drawingState    Full drawing state (page geometry, paths, etc.)
     * @param fileName        Base name for the exported file (no extension).
     * @param isLegacy        True for drawings created before paged mode was introduced.
     * @param currentPageIndex The zero-based index of the currently active page (paged mode only).
     */
    fun shareDrawing(
        drawingState: DrawingState,
        fileName: String,
        isLegacy: Boolean = true,
        currentPageIndex: Int = 0,
    )

    /**
     * Saves the rendered drawing to the device's pictures folder.
     *
     * @param drawingState    Full drawing state (page geometry, paths, etc.)
     * @param fileName        Base name for the exported file (no extension).
     * @param isLegacy        True for drawings created before paged mode was introduced.
     * @param currentPageIndex The zero-based index of the currently active page (paged mode only).
     */
    fun saveDrawing(
        drawingState: DrawingState,
        fileName: String,
        isLegacy: Boolean = true,
        currentPageIndex: Int = 0,
    )
}
