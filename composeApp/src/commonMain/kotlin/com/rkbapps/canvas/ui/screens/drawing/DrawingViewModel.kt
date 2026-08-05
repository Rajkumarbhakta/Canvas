package com.rkbapps.canvas.ui.screens.drawing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch


class DrawingViewModel(
    private val repository: DrawingRepository
) : ViewModel() {

    val state = repository.state
    val currentDesign = repository.currentDesign
    val uiState = repository.uiState

    fun updateDrawingName(name: String) = repository.updateDrawingName(name)

    fun onAction(action: DrawingAction) {
        when (action) {
            // ── Drawing ──────────────────────────────────────────────────────
            is DrawingAction.OnDraw -> repository.onDraw(action.offset)
            DrawingAction.OnNewPathStart -> repository.onNewPathStart()
            DrawingAction.OnPathEnd -> repository.onPathEnd()

            // ── Tool selection ────────────────────────────────────────────────
            is DrawingAction.OnSelectColor -> repository.onSelectColor(action.color)
            DrawingAction.OnClearCanvasList -> repository.onClearCanvasClick()
            is DrawingAction.OnThicknessChange -> repository.onThicknessChange(action.thickness)
            is DrawingAction.OnPathEffectChange -> repository.onPathEffectChange(action.pathEffect)
            is DrawingAction.OnShapeTypeChange -> repository.onShapeTypeChange(action.shapeType)
            is DrawingAction.OnToggleEraser -> repository.onToggleEraser(action.isEraser)
            is DrawingAction.OnBackgroundColorChange -> repository.onBackgroundColorChange(action.color)

            // ── History ────────────────────────────────────────────────────────
            DrawingAction.OnRedo -> repository.onRedo()
            DrawingAction.OnUndo -> repository.onUndo()

            // ── Persistence ────────────────────────────────────────────────────
            is DrawingAction.SaveDesign -> viewModelScope.launch(Dispatchers.IO) {
                repository.saveDesign(action.drawingState, action.name)
            }

            // ── UI dialogs ─────────────────────────────────────────────────────
            DrawingAction.OnCloseNameEditDialog -> repository.showHideNameEditorDialog(false)
            DrawingAction.OnOpenNameEditDialog -> repository.showHideNameEditorDialog(true)
            DrawingAction.OnEnterFullScreen -> repository.changeFullScreen(true)
            DrawingAction.OnExitFullScreen -> repository.changeFullScreen(false)
            DrawingAction.OnEraserSelected -> repository.changeEraserSelection(true)
            DrawingAction.OnEraserUnselected -> repository.changeEraserSelection(false)

            // ── Share / export ─────────────────────────────────────────────────
            DrawingAction.OnShareDrawing -> repository.onShareDrawing()
            DrawingAction.OnSaveAsImage -> repository.onSaveAsImage()

            // ── Selection mode ─────────────────────────────────────────────────
            is DrawingAction.OnToggleSelectionMode ->
                repository.onToggleSelectionMode(action.isSelection)
            is DrawingAction.OnSelectPath -> repository.onSelectPath(action.pathId)
            is DrawingAction.OnDragSelectedPath -> repository.onDragSelectedPath(action.dragAmount)
            DrawingAction.OnDeleteSelectedPath -> repository.onDeleteSelectedPath()

            // ── Milestone 1: Zoom / Pan / Page size ───────────────────────────
            is DrawingAction.OnZoomPan -> repository.onZoomPan(action.zoomDelta, action.panDelta)
            DrawingAction.OnResetView -> repository.onResetView()
            is DrawingAction.OnSetPageSize ->
                repository.onSetPageSize(action.width, action.height, action.label)
            DrawingAction.OnOpenPageSizePicker -> repository.showHidePageSizePicker(true)
            DrawingAction.OnClosePageSizePicker -> repository.showHidePageSizePicker(false)

            // ── Milestone 2: Multi-page ────────────────────────────────────────
            DrawingAction.OnAddPage -> repository.onAddPage()
            is DrawingAction.OnSelectPage -> repository.onSelectPage(action.index)
            is DrawingAction.OnDeletePage -> repository.onDeletePage(action.index)
        }
    }
}