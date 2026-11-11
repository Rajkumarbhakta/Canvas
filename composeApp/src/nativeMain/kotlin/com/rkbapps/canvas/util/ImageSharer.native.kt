package com.rkbapps.canvas.util

import com.rkbapps.canvas.model.DrawingState

class ImageSharerNative : ImageSharer {
    override fun shareDrawing(
        drawingState: DrawingState,
        fileName: String,
        isLegacy: Boolean,
        currentPageIndex: Int,
    ) {
        // Full implementation would use iOS platform APIs (UIActivityViewController).
    }

    override fun saveDrawing(
        drawingState: DrawingState,
        fileName: String,
        isLegacy: Boolean,
        currentPageIndex: Int,
    ) {
        // Full implementation would use iOS platform APIs (PHPhotoLibrary / Files).
    }
}
