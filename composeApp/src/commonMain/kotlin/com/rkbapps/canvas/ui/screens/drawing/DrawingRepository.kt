package com.rkbapps.canvas.ui.screens.drawing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.rkbapps.canvas.db.dao.DrawingDao
import com.rkbapps.canvas.db.utils.toDomain
import com.rkbapps.canvas.db.utils.toEntity
import com.rkbapps.canvas.model.CanvasPage
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

    private val _currentDesign =
        MutableStateFlow(SavedDesign(name = "Untitled drawing", state = DrawingState()))
    val currentDesign = _currentDesign.asStateFlow()

    private val _uiState = MutableStateFlow(DrawingScreenState())
    val uiState = _uiState.asStateFlow()

    // ── Init: load existing design ────────────────────────────────────────────

    init {
        val draw = saveStateHandle.toRoute<Draw>()
        draw.id?.let {
            CoroutineScope(Dispatchers.IO).launch {
                delay(200.milliseconds)
                val designWithPaths = drawingDao.getDesignWithPathsByStringId(it)
                if (designWithPaths != null) {
                    val design = designWithPaths.toDomain()
                    _currentDesign.update { design }
                    _state.update { design.state }
                    // Propagate legacy flag into UI state
                    _uiState.update { ui -> ui.copy(isLegacy = design.isLegacy) }
                }
            }
        }
    }

    // ── UI dialog helpers ─────────────────────────────────────────────────────

    fun showHideNameEditorDialog(value: Boolean) {
        _uiState.update { it.copy(isEditDrawingNameDialogVisible = value) }
    }

    fun changeFullScreen(value: Boolean) {
        _uiState.update { it.copy(isFullScreen = value) }
    }

    fun changeEraserSelection(value: Boolean) {
        _uiState.update { it.copy(isEraserSelected = value) }
    }

    // ── Milestone 1: Zoom / Pan / Page size ──────────────────────────────────

    fun onZoomPan(zoomDelta: Float, panDelta: Offset) {
        _uiState.update {
            val newZoom = (it.zoom * zoomDelta).coerceIn(it.minZoom, it.maxZoom)
            it.copy(zoom = newZoom, panOffset = it.panOffset + panDelta)
        }
    }

    fun onResetView() {
        _uiState.update { it.copy(zoom = 1f, panOffset = Offset.Zero) }
    }

    fun onSetPageSize(width: Float, height: Float, label: String) {
        _state.update { it.copy(pageWidth = width, pageHeight = height, pageSizeLabel = label) }
        onResetView()
    }

    fun showHidePageSizePicker(visible: Boolean) {
        _uiState.update { it.copy(isPageSizePickerVisible = visible) }
    }

    // ── Milestone 2: Multi-page ───────────────────────────────────────────────

    /** Returns the page at [currentPageIndex], creating one if the list is empty. */
    private fun currentPage(): CanvasPage {
        val pages = _state.value.pages
        val idx = _uiState.value.currentPageIndex.coerceIn(0, (pages.size - 1).coerceAtLeast(0))
        return pages.getOrElse(idx) { CanvasPage() }
    }

    private fun updateCurrentPage(transform: (CanvasPage) -> CanvasPage) {
        val idx = _uiState.value.currentPageIndex
        _state.update { s ->
            val pages = s.pages.toMutableList()
            if (pages.isEmpty()) pages.add(CanvasPage())
            val safeIdx = idx.coerceIn(0, pages.size - 1)
            pages[safeIdx] = transform(pages[safeIdx])
            s.copy(pages = pages)
        }
    }

    fun onAddPage() {
        _state.update { s ->
            val newPage = CanvasPage(backgroundColor = s.backgroundColor)
            s.copy(pages = s.pages + newPage)
        }
        // Switch to the new (last) page
        _uiState.update { it.copy(currentPageIndex = _state.value.pages.size - 1) }
        onResetView()
    }

    fun onSelectPage(index: Int) {
        val pageCount = _state.value.pages.size
        if (index in 0 until pageCount) {
            _uiState.update { it.copy(currentPageIndex = index) }
            onResetView()
        }
    }

    fun onDeletePage(index: Int) {
        val pages = _state.value.pages
        if (pages.size <= 1) return // Cannot delete the last page
        val newPages = pages.toMutableList().also { it.removeAt(index) }
        val newIndex = (index - 1).coerceAtLeast(0)
        _state.update { it.copy(pages = newPages) }
        _uiState.update { it.copy(currentPageIndex = newIndex) }
    }

    // ── Drawing ───────────────────────────────────────────────────────────────

    fun onThicknessChange(f: Float) {
        _state.update { it.copy(selectedThickness = f) }
    }

    fun onClearCanvasClick() {
        if (_uiState.value.isLegacy) {
            // Legacy path
            _state.update {
                it.copy(
                    currentPath = null,
                    paths = emptyList(),
                    undoStack = it.undoStack + listOf(it.paths),
                    redoStack = emptyList()
                )
            }
        } else {
            updateCurrentPage { page ->
                page.copy(
                    paths = emptyList(),
                    undoStack = page.undoStack + listOf(page.paths),
                    redoStack = emptyList()
                )
            }
        }
    }

    fun onSelectColor(color: Color) {
        _state.update { it.copy(selectedColor = color) }
    }

    fun onPathEnd() {
        if (_uiState.value.isLegacy) {
            onPathEndLegacy()
        } else {
            onPathEndPaged()
        }
    }

    private fun onPathEndLegacy() {
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
        } else if (uiState.value.isSelectionMode) {
            commitDragLegacy()
        }
    }

    private fun onPathEndPaged() {
        val currentPathData = state.value.currentPath
        if (currentPathData != null) {
            updateCurrentPage { page ->
                page.copy(
                    paths = page.paths + currentPathData,
                    undoStack = page.undoStack + listOf(page.paths),
                    redoStack = emptyList()
                )
            }
            _state.update { it.copy(currentPath = null) }
        } else if (uiState.value.isSelectionMode) {
            commitDragPaged()
        }
    }

    fun onNewPathStart() {
        val isEraser = uiState.value.isEraserSelected
        _state.update {
            it.copy(
                currentPath = PathData(
                    id = Clock.System.now().toString(),
                    color = it.selectedColor,
                    thickness = it.selectedThickness,
                    pathEffect = it.selectedPathEffect,
                    isEraser = isEraser,
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
            _state.update {
                it.copy(currentPath = currentPathData.copy(path = currentPathData.path + offset))
            }
        } else {
            val shapePoints = if (currentPathData.shapePoints.isEmpty()) {
                listOf(offset, offset)
            } else {
                listOf(currentPathData.shapePoints.first(), offset)
            }
            _state.update {
                it.copy(currentPath = currentPathData.copy(shapePoints = shapePoints))
            }
        }
    }

    fun onPathEffectChange(pathEffect: PaintingStyleType) {
        _state.update { it.copy(selectedPathEffect = pathEffect) }
        _uiState.update { it.copy(isSelectionMode = false, selectedPathId = null) }
    }

    fun onShapeTypeChange(shapeType: ShapeType) {
        _state.update { it.copy(selectedShapeType = shapeType) }
        _uiState.update {
            it.copy(
                isEraserSelected = false,
                isSelectionMode = false,
                selectedPathId = null
            )
        }
        if (shapeType != ShapeType.NONE) changeEraserSelection(false)
    }

    fun onToggleEraser(isEraser: Boolean) {
        if (isEraser) {
            _state.update { it.copy(selectedShapeType = ShapeType.NONE) }
        }
        _uiState.update {
            it.copy(
                isEraserSelected = isEraser,
                isSelectionMode = false,
                selectedPathId = null
            )
        }
    }

    // ── Selection mode ────────────────────────────────────────────────────────

    private var hasPushedUndoForCurrentDrag = false

    fun onToggleSelectionMode(isSelection: Boolean) {
        _uiState.update {
            it.copy(
                isSelectionMode = isSelection,
                selectedPathId = if (isSelection) it.selectedPathId else null,
                isEraserSelected = if (isSelection) false else it.isEraserSelected,
                selectedShapeType = if (isSelection) ShapeType.NONE else it.selectedShapeType
            )
        }
        if (isSelection) changeEraserSelection(false)
    }

    fun onSelectPath(pathId: String?) {
        _uiState.update { it.copy(selectedPathId = pathId) }
        _state.update { it.copy(dragOffset = Offset.Zero) }
        hasPushedUndoForCurrentDrag = false
    }

    fun onDragSelectedPath(dragAmount: Offset) {
        uiState.value.selectedPathId ?: return
        if (!hasPushedUndoForCurrentDrag) {
            if (_uiState.value.isLegacy) {
                _state.update {
                    it.copy(
                        undoStack = it.undoStack + listOf(it.paths),
                        redoStack = emptyList()
                    )
                }
            } else {
                updateCurrentPage { page ->
                    page.copy(
                        undoStack = page.undoStack + listOf(page.paths),
                        redoStack = emptyList()
                    )
                }
            }
            hasPushedUndoForCurrentDrag = true
        }
        _state.update { it.copy(dragOffset = it.dragOffset + dragAmount) }
    }

    private fun commitDragLegacy() {
        val selectedId = uiState.value.selectedPathId
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
                    } else pathData
                }
                currentState.copy(paths = updatedPaths, dragOffset = Offset.Zero)
            }
        }
        hasPushedUndoForCurrentDrag = false
    }

    private fun commitDragPaged() {
        val selectedId = uiState.value.selectedPathId
        val finalOffset = state.value.dragOffset
        if (selectedId != null && finalOffset != Offset.Zero) {
            updateCurrentPage { page ->
                val updatedPaths = page.paths.map { pathData ->
                    if (pathData.id == selectedId) {
                        if (pathData.shapeType == ShapeType.NONE) {
                            pathData.copy(path = pathData.path.map { it + finalOffset })
                        } else {
                            pathData.copy(shapePoints = pathData.shapePoints.map { it + finalOffset })
                        }
                    } else pathData
                }
                page.copy(paths = updatedPaths)
            }
            _state.update { it.copy(dragOffset = Offset.Zero) }
        }
        hasPushedUndoForCurrentDrag = false
    }

    fun onDeleteSelectedPath() {
        val selectedId = uiState.value.selectedPathId ?: return
        _uiState.update { it.copy(selectedPathId = null) }
        if (_uiState.value.isLegacy) {
            _state.update { currentState ->
                currentState.copy(
                    paths = currentState.paths.filter { it.id != selectedId },
                    undoStack = currentState.undoStack + listOf(currentState.paths),
                    redoStack = emptyList()
                )
            }
        } else {
            updateCurrentPage { page ->
                page.copy(
                    paths = page.paths.filter { it.id != selectedId },
                    undoStack = page.undoStack + listOf(page.paths),
                    redoStack = emptyList()
                )
            }
        }
    }

    fun onBackgroundColorChange(color: Color) {
        if (_uiState.value.isLegacy) {
            _state.update { it.copy(backgroundColor = color) }
        } else {
            // Update the current page's background
            updateCurrentPage { it.copy(backgroundColor = color) }
            // Also update the state-level backgroundColor for new pages default
            _state.update { it.copy(backgroundColor = color) }
        }
    }

    // ── Undo / Redo ───────────────────────────────────────────────────────────

    fun onUndo() {
        if (_uiState.value.isLegacy) {
            val undoStack = _state.value.undoStack
            if (undoStack.isNotEmpty()) {
                val previous = undoStack.last()
                _state.value = _state.value.copy(
                    paths = previous,
                    undoStack = undoStack.dropLast(1),
                    redoStack = _state.value.redoStack + listOf(_state.value.paths)
                )
            }
        } else {
            updateCurrentPage { page ->
                if (page.undoStack.isEmpty()) return@updateCurrentPage page
                val previous = page.undoStack.last()
                page.copy(
                    paths = previous,
                    undoStack = page.undoStack.dropLast(1),
                    redoStack = page.redoStack + listOf(page.paths)
                )
            }
        }
    }

    fun onRedo() {
        if (_uiState.value.isLegacy) {
            val redoStack = _state.value.redoStack
            if (redoStack.isNotEmpty()) {
                val next = redoStack.last()
                _state.value = _state.value.copy(
                    paths = next,
                    redoStack = redoStack.dropLast(1),
                    undoStack = _state.value.undoStack + listOf(_state.value.paths)
                )
            }
        } else {
            updateCurrentPage { page ->
                if (page.redoStack.isEmpty()) return@updateCurrentPage page
                val next = page.redoStack.last()
                page.copy(
                    paths = next,
                    redoStack = page.redoStack.dropLast(1),
                    undoStack = page.undoStack + listOf(page.paths)
                )
            }
        }
    }

    // ── Persistence ───────────────────────────────────────────────────────────

    suspend fun saveDesign(drawingState: DrawingState, name: String) {
        // Strip undo/redo from all pages before saving
        val cleanPages = drawingState.pages.map { it.copy(undoStack = emptyList(), redoStack = emptyList()) }
        val current = _currentDesign.value.copy(
            name = name,
            state = drawingState.copy(
                pages = cleanPages,
                undoStack = emptyList(),
                redoStack = emptyList()
            )
        )

        val designEntity = current.toEntity()

        // Build path entities: iterate all pages for new drawings, flat list for legacy
        val pathEntities = if (current.isLegacy) {
            current.state.paths.mapIndexed { index, pathData ->
                pathData.toEntity(current.pId, index, pageIndex = 0)
            }
        } else {
            current.state.pages.flatMapIndexed { pageIdx, page ->
                page.paths.mapIndexed { strokeIdx, pathData ->
                    pathData.toEntity(current.pId, strokeIdx, pageIndex = pageIdx)
                }
            }
        }

        val newDbId = drawingDao.upsertDesignWithPaths(designEntity, pathEntities)
        _currentDesign.update { current.copy(pId = newDbId) }
    }

    fun updateDrawingName(name: String) {
        _currentDesign.update { it.copy(name = name) }
    }

    fun onShareDrawing() {
        imageSharer.shareDrawing(
            drawingState = state.value,
            fileName = currentDesign.value.name,
            isLegacy = _uiState.value.isLegacy,
            currentPageIndex = _uiState.value.currentPageIndex,
        )
    }

    fun onSaveAsImage() {
        imageSharer.saveDrawing(
            drawingState = state.value,
            fileName = currentDesign.value.name,
            isLegacy = _uiState.value.isLegacy,
            currentPageIndex = _uiState.value.currentPageIndex,
        )
    }
}