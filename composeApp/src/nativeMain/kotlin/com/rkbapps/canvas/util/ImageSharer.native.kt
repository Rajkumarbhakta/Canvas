package com.rkbapps.canvas.util

import com.rkbapps.canvas.model.DrawingState

class ImageSharerNative: ImageSharer {
    override fun shareDrawing(drawingState: DrawingState, fileName: String) {
        // Full implementation would use iOS platform APIs like UIActivityViewController
    }
    override fun saveDrawing(drawingState: DrawingState, fileName: String) {
        // Not implemented for Native
    }
}
