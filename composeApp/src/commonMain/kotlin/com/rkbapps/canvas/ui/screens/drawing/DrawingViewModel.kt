fun onAction(action: DrawingAction) {
    when (action) {
        is DrawingAction.SaveDesign -> saveDesign(action.state, action.name)
        DrawingAction.OnUndo -> undo()
        DrawingAction.OnRedo -> redo()
        DrawingAction.OnClearCanvasList -> clearCanvas()
        DrawingAction.OnEraserSelected -> selectEraser()
        DrawingAction.OnEraserUnselected -> unselectEraser()
        is DrawingAction.OnToggleEraser -> toggleEraser(action.enabled)
        is DrawingAction.OnSelectColor -> selectColor(action.color)
        is DrawingAction.OnBackgroundColorChange -> changeBackgroundColor(action.color)
        is DrawingAction.OnThicknessChange -> changeThickness(action.thickness)
        is DrawingAction.OnPathEffectChange -> changePathEffect(action.effect)

        // 👇 NEW HANDLER
        is DrawingAction.OnShapeTypeChange -> {
            _state.value = _state.value.copy(selectedShapeType = action.shapeType)
        }

        DrawingAction.OnOpenNameEditDialog -> openNameDialog()
        DrawingAction.OnCloseNameEditDialog -> closeNameDialog()
        DrawingAction.OnEnterFullScreen -> enterFullScreen()
        DrawingAction.OnExitFullScreen -> exitFullScreen()
    }
}
