package com.rkbapps.canvas.ui.screens.drawing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.model.PathData
import com.rkbapps.canvas.ui.screens.drawing.composables.PaintingStyleType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock

sealed interface DrawingAction {
    data object OnNewPathStart : DrawingAction
    data class OnDraw(val offset: Offset) : DrawingAction
    data object OnPathEnd : DrawingAction
    data class OnSelectColor(val color: Color) : DrawingAction
    data object OnClearCanvasList : DrawingAction
    data class OnThicknessChange(val thickness: Float) : DrawingAction
    data class OnPathEffectChange(val pathEffect: PaintingStyleType) : DrawingAction
    data class OnToggleEraser(val isEraser: Boolean) : DrawingAction
    data class OnBackgroundColorChange(val color: Color) : DrawingAction
}

class DrawingRepository {

    private val _state = MutableStateFlow(DrawingState())
    val state = _state.asStateFlow()

    fun onThicknessChange(f: Float) {
        _state.update {
            it.copy(
                selectedThickness = f
            )
        }
    }

    fun onClearCanvasClick() {
        _state.update {
            it.copy(
                currentPath = null,
                paths = emptyList()
            )
        }
    }

    fun onSelectColor(color: Color) {
        _state.update {
            it.copy(selectedColor = color)
        }
    }

    fun onPathEnd() {
        val currentPathData = state.value.currentPath ?: return
        _state.update {
            it.copy(
                currentPath = null,
                paths = it.paths + currentPathData
            )
        }
    }

    fun onNewPathStart() {
        _state.update {
            it.copy(
                currentPath = PathData(
                    id = Clock.System.now().toString(),
                    color = it.selectedColor,
                    thickness = it.selectedThickness,
                    pathEffect = it.selectedPathEffect,
                    isEraser = it.isEraserMode,
                    path = emptyList()
                )
            )
        }
    }

    fun onDraw(offset: Offset) {
        val currentPathData = state.value.currentPath ?: return
        _state.update {
            it.copy(
                currentPath = currentPathData.copy(
                    path = currentPathData.path + offset
                )
            )
        }
    }

    fun onPathEffectChange(pathEffect: PaintingStyleType) {
        _state.update {
            it.copy(
                selectedPathEffect = pathEffect
            )
        }
    }

    fun onToggleEraser(isEraser: Boolean) {
        _state.update {
            it.copy(
                isEraserMode = isEraser
            )
        }
    }

    fun onBackgroundColorChange(color: Color) {
        _state.update {
            it.copy(
                backgroundColor = color
            )
        }
    }


}