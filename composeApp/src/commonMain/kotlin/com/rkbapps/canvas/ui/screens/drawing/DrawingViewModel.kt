package com.rkbapps.canvas.ui.screens.drawing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.model.PathData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock


class DrawingViewModel(
    private val repository: DrawingRepository
) : ViewModel() {

    val state = repository.state

    fun onAction(action: DrawingAction) {
        when (action) {
            is DrawingAction.OnDraw -> repository.onDraw(action.offset)
            DrawingAction.OnNewPathStart -> repository.onNewPathStart()
            DrawingAction.OnPathEnd -> repository.onPathEnd()
            is DrawingAction.OnSelectColor -> repository.onSelectColor(action.color)
            DrawingAction.OnClearCanvasList -> repository.onClearCanvasClick()
            is DrawingAction.OnThicknessChange -> repository.onThicknessChange(action.thickness)
        }
    }




}