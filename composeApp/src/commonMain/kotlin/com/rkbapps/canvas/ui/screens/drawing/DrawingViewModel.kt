package com.rkbapps.canvas.ui.screens.drawing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.model.PathData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock


class DrawingViewModel(
    private val repository: DrawingRepository
) : ViewModel() {

    val state = repository.state
    val currentDesign = repository.currentDesign

    fun updateDrawingName(name:String) =repository.updateDrawingName(name)

    fun onAction(action: DrawingAction) {
        when (action) {
            is DrawingAction.OnDraw -> repository.onDraw(action.offset)
            DrawingAction.OnNewPathStart -> repository.onNewPathStart()
            DrawingAction.OnPathEnd -> repository.onPathEnd()
            is DrawingAction.OnSelectColor -> repository.onSelectColor(action.color)
            DrawingAction.OnClearCanvasList -> repository.onClearCanvasClick()
            is DrawingAction.OnThicknessChange -> repository.onThicknessChange(action.thickness)
            is DrawingAction.OnPathEffectChange -> repository.onPathEffectChange(action.pathEffect)
            is DrawingAction.OnToggleEraser -> repository.onToggleEraser(action.isEraser)
            is DrawingAction.OnBackgroundColorChange -> repository.onBackgroundColorChange(action.color)
            DrawingAction.OnRedo -> repository.onRedo()
            DrawingAction.OnUndo -> repository.onUndo()
            is DrawingAction.SaveDesign -> viewModelScope.launch {
                repository.saveDesign(action.drawingState,action.name)
            }
        }
    }
}