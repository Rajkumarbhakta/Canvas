package com.rkbapps.canvas.util

import com.rkbapps.canvas.model.DrawingState

interface ImageSharer {
    fun shareDrawing(drawingState: DrawingState, fileName: String)
    fun saveDrawing(drawingState: DrawingState, fileName: String)
}
