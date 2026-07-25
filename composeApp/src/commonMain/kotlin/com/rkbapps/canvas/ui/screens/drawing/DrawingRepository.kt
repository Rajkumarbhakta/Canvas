package com.rkbapps.canvas.ui.screens.drawing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.rkbapps.canvas.db.dao.DrawingDao
import com.rkbapps.canvas.db.utils.toDomain
import com.rkbapps.canvas.db.utils.toEntity
import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.model.PathData
import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.navigation.Draw
import com.rkbapps.canvas.ui.screens.drawing.utils.PaintingStyleType
import com.rkbapps.canvas.ui.screens.drawing.utils.ShapeType
import com.rkbapps.canvas.util.ImageSharer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

class DrawingRepository(
    private val drawingDao: DrawingDao,
    private val imageSharer: ImageSharer,
    saveStateHandle: SavedStateHandle
) {

    private val _state = MutableStateFlow(DrawingState())
    val state = _state.asStateFlow()

    private val _currentDesign = MutableStateFlow<SavedDesign>(SavedDesign(name = "Untitled drawing", state = DrawingState()))
    val currentDesign = _currentDesign.asStateFlow()


    private val _uiState = MutableStateFlow(DrawingScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        val draw = saveStateHandle.toRoute<Draw>()
        draw.id?.let {
            CoroutineScope(Dispatchers.IO).launch {
                delay(200.milliseconds)
                val designWithPaths = drawingDao.getDesignWithPathsByStringId(it)
                if (designWithPaths!=null){
                    val design = designWithPaths.toDomain()
                    _currentDesign.update { design }
                    _state.update { design.state }
                }
            }
        }
    }

    fun showHideNameEditorDialog(value: Boolean){
        _uiState.update {
            it.copy(isEditDrawingNameDialogVisible = value)
        }
    }
    fun changeFullScreen(value: Boolean){
        _uiState.update {
            it.copy(
                isFullScreen = value
            )
        }
    }

    fun changeEraserSelection(value: Boolean){
        _uiState.update {
            it.copy(
                isEraserSelected = value
            )
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
            it.copy(
                selectedColor = color,
                isSelectionMode = false,
                selectedPathId = null
            )
        }
    }

    fun onPathEnd() {
        val currentPathData = state.value.currentPath
        if (currentPathData != null) {
            _state.update {
                it.copy(
                    currentPath = null,
                    paths = it.paths + currentPathData,
                    undoStack = _state.value.undoStack + listOf(_state.value.paths),
                    redoStack = emptyList()
                )
            }
        } else if (state.value.isSelectionMode) {
            val selectedId = state.value.selectedPathId
            val finalOffset = state.value.dragOffset
            if (selectedId != null && finalOffset != Offset.Zero) {
                _state.update { currentState ->
                    val updatedPaths = currentState.paths.map { pathData ->
                        if (pathData.id == selectedId) {
                            if (pathData.shapeType == ShapeType.NONE) {
                                pathData.copy(path = pathData.path.map { it + finalOffset })
                            } else {
                                pathData.copy(shapePoints = pathData.shapePoints.map { it + finalOffset })
                            }
                        } else {
                            pathData
                        }
                    }
                    currentState.copy(
                        paths = updatedPaths,
                        dragOffset = Offset.Zero
                    )
                }
            }
            hasPushedUndoForCurrentDrag = false
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
                    path = emptyList(),
                    shapeType = it.selectedShapeType,
                    shapePoints = emptyList()
                )
            )
        }
    }

    fun onDraw(offset: Offset) {
        val currentPathData = state.value.currentPath ?: return
        
        if (currentPathData.shapeType == ShapeType.NONE) {
            // Regular drawing
            _state.update {
                it.copy(currentPath = currentPathData.copy(path = currentPathData.path + offset))
            }
        } else {
            // Shape drawing - store start and current point
            val shapePoints = if (currentPathData.shapePoints.isEmpty()) {
                // First point (start)
                listOf(offset, offset)
            } else {
                // Update end point
                listOf(currentPathData.shapePoints.first(), offset)
            }
            
            _state.update {
                it.copy(currentPath = currentPathData.copy(shapePoints = shapePoints))
            }
        }
    }

    fun onPathEffectChange(pathEffect: PaintingStyleType) {
        _state.update {
            it.copy(
                selectedPathEffect = pathEffect,
                isSelectionMode = false,
                selectedPathId = null
            )
        }
    }
    
    fun onShapeTypeChange(shapeType: ShapeType) {
        _state.update {
            it.copy(
                selectedShapeType = shapeType,
                isEraserMode = false,
                isSelectionMode = false,
                selectedPathId = null
            )
        }
        // Update UI state to unselect eraser when shape is selected
        if (shapeType != ShapeType.NONE) {
            changeEraserSelection(false)
        }
    }

    fun onToggleEraser(isEraser: Boolean) {
        _state.update {
            it.copy(
                isEraserMode = isEraser,
                isSelectionMode = false,
                selectedPathId = null
            )
        }
    }

    private var hasPushedUndoForCurrentDrag = false

    fun onToggleSelectionMode(isSelection: Boolean) {
        _state.update {
            it.copy(
                isSelectionMode = isSelection,
                selectedPathId = if (isSelection) it.selectedPathId else null,
                isEraserMode = if (isSelection) false else it.isEraserMode,
                selectedShapeType = if (isSelection) ShapeType.NONE else it.selectedShapeType
            )
        }
        if (isSelection) {
            changeEraserSelection(false)
        }
    }

    fun onSelectPath(pathId: String?) {
        _state.update {
            it.copy(
                selectedPathId = pathId,
                dragOffset = Offset.Zero
            )
        }
        // When drag gesture starts, reset the undo flag
        hasPushedUndoForCurrentDrag = false
    }

    fun onDragSelectedPath(dragAmount: Offset) {
        val selectedId = _state.value.selectedPathId ?: return
        // Push current paths to undo stack on the first movement of this drag session
        if (!hasPushedUndoForCurrentDrag) {
            _state.update {
                it.copy(
                    undoStack = it.undoStack + listOf(it.paths),
                    redoStack = emptyList()
                )
            }
            hasPushedUndoForCurrentDrag = true
        }
        _state.update { currentState ->
            currentState.copy(dragOffset = currentState.dragOffset + dragAmount)
        }
    }

    fun onDeleteSelectedPath() {
        val selectedId = _state.value.selectedPathId ?: return
        _state.update { currentState ->
            currentState.copy(
                paths = currentState.paths.filter { it.id != selectedId },
                selectedPathId = null,
                undoStack = currentState.undoStack + listOf(currentState.paths),
                redoStack = emptyList()
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
        val current = _currentDesign.value.copy(
            name = name, 
            state = drawingState.copy(
                undoStack = listOf(),
                redoStack = listOf()
            )
        )
        
        val designEntity = current.toEntity()
        val pathEntities = current.state.paths.mapIndexed { index, pathData ->
            pathData.toEntity(current.pId, index)
        }
        
        val newDbId = drawingDao.upsertDesignWithPaths(designEntity, pathEntities)
        
        _currentDesign.update { current.copy(pId = newDbId) }
    }

    fun updateDrawingName(name:String){
        _currentDesign.update {
            it.copy(
                name = name
            )
        }
    }

    fun onShareDrawing() {
        imageSharer.shareDrawing(state.value, currentDesign.value.name)
    }

    fun onSaveAsImage() {
        imageSharer.saveDrawing(state.value, currentDesign.value.name)
    }

}