item {
    ShapeSelector(
        selectedShape = state.selectedShapeType,   // from ViewModel state
        shapes = shapeOptions,                     // from ShapeOptions.kt
        onShapeSelected = { shapeType ->
            viewModel.onAction(DrawingAction.OnEraserUnselected)
            viewModel.onAction(DrawingAction.OnToggleEraser(false))
            viewModel.onAction(DrawingAction.OnShapeTypeChange(shapeType)) // update ViewModel
        }
    )
}
