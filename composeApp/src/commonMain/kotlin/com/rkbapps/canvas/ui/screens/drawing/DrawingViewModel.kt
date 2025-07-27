package com.rkbapps.canvas.ui.screens.drawing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
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
            is DrawingAction.OnDraw -> repository.onDraw(action.offset)
            DrawingAction.OnNewPathStart -> repository.onNewPathStart()
            DrawingAction.OnPathEnd -> repository.onPathEnd()
            is DrawingAction.OnSelectColor -> repository.onSelectColor(action.color)
            DrawingAction.OnClearCanvasList -> repository.onClearCanvasClick()
            is DrawingAction.OnThicknessChange -> repository.onThicknessChange(action.thickness)
            is DrawingAction.OnPathEffectChange -> repository.onPathEffectChange(action.pathEffect)
            is DrawingAction.OnShapeTypeChange -> repository.onShapeTypeChange(action.shapeType)
            is DrawingAction.OnToggleEraser -> repository.onToggleEraser(action.isEraser)
            is DrawingAction.OnBackgroundColorChange -> repository.onBackgroundColorChange(action.color)
            DrawingAction.OnRedo -> repository.onRedo()
            DrawingAction.OnUndo -> repository.onUndo()
            is DrawingAction.SaveDesign -> viewModelScope.launch(Dispatchers.Default) {
                repository.saveDesign(action.drawingState, action.name)
            }

            DrawingAction.OnCloseNameEditDialog -> repository.showHideNameEditorDialog(false)
            DrawingAction.OnEnterFullScreen ->  repository.changeFullScreen(true)
            DrawingAction.OnEraserSelected -> repository.changeEraserSelection(true)
            DrawingAction.OnEraserUnselected -> repository.changeEraserSelection(false)
            DrawingAction.OnExitFullScreen -> repository.changeFullScreen(false)
            DrawingAction.OnOpenNameEditDialog -> repository.showHideNameEditorDialog(true)
        }
    }
}