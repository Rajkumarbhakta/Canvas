package com.rkbapps.canvas.ui.screens.drawing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.rkbapps.canvas.db.DbOperations
import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.model.PathData
import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.navigation.Draw
import com.rkbapps.canvas.ui.screens.drawing.composables.PaintingStyleType
import com.rkbapps.canvas.util.Log
import com.rkbapps.canvas.util.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    data object OnUndo : DrawingAction
    data object OnRedo : DrawingAction
    data class SaveDesign(val drawingState: DrawingState, val name: String) : DrawingAction
}

class DrawingRepository(
    private val dbOperations: DbOperations,
    saveStateHandle: SavedStateHandle
) {

    private val _state = MutableStateFlow(DrawingState())
    val state = _state.asStateFlow()

    private val _currentDesign = MutableStateFlow<SavedDesign>(SavedDesign(name = "Untitled drawing", state = DrawingState()))
    val currentDesign = _currentDesign.asStateFlow()

    init {
        val draw = saveStateHandle.toRoute<Draw>()
        draw.id?.let {
            CoroutineScope(Dispatchers.Default).launch {
                delay(200)
                val design = dbOperations.getDesign(it)
                if (design!=null){
                    _currentDesign.update { design }
                    _state.update { design.state }
                }
            }
        }
    }


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
                paths = emptyList(),
                undoStack = _state.value.undoStack + listOf(_state.value.paths),
                redoStack = emptyList()
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
                paths = it.paths + currentPathData,
                undoStack = _state.value.undoStack + listOf(_state.value.paths),
                redoStack = emptyList()
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
            it.copy(currentPath = currentPathData.copy(path = currentPathData.path + offset))
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

    fun onUndo() {
        val undoStack = _state.value.undoStack
        if (undoStack.isNotEmpty()) {
            val previous = undoStack.last()
            _state.value = _state.value.copy(
                paths = previous,
                undoStack = undoStack.dropLast(1),
                redoStack = _state.value.redoStack + listOf(_state.value.paths)
            )
        }
    }

    fun onRedo() {
        val redoStack = _state.value.redoStack
        if (redoStack.isNotEmpty()) {
            val next = redoStack.last()
            _state.value = _state.value.copy(
                paths = next,
                redoStack = redoStack.dropLast(1),
                undoStack = _state.value.undoStack + listOf(_state.value.paths)
            )
        }
    }

    suspend fun saveDesign(drawingState: DrawingState, name: String) {
        _currentDesign.update {
            it.copy(name = name, state = drawingState.copy(
                undoStack = listOf(),
                redoStack = listOf()
            ))
        }
        val design = currentDesign.value

        dbOperations.save(design)
    }

    fun updateDrawingName(name:String){
        _currentDesign.update {
            it.copy(
                name = name
            )
        }
    }



}