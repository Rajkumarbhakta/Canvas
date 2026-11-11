package com.rkbapps.canvas

import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.ui.screens.drawing.DrawingAction
import com.rkbapps.canvas.ui.screens.drawing.DrawingScreenState
import com.rkbapps.canvas.ui.screens.drawing.utils.DrawingTool
import com.rkbapps.canvas.ui.screens.drawing.utils.ShapeType
import com.rkbapps.canvas.ui.screens.drawing.utils.activeTool
import com.rkbapps.canvas.ui.screens.drawing.utils.toolAction
import kotlin.test.Test
import kotlin.test.assertEquals

class DrawingToolTest {

    @Test
    fun activeToolFoldsTheThreeStateFlagsIntoOneChoice() {
        val brush = DrawingState()
        val shape = DrawingState(selectedShapeType = ShapeType.CIRCLE)

        assertEquals(DrawingTool.BRUSH, activeTool(brush, DrawingScreenState()))
        assertEquals(DrawingTool.SHAPE, activeTool(shape, DrawingScreenState()))
        assertEquals(DrawingTool.ERASER, activeTool(brush, DrawingScreenState(isEraserSelected = true)))
        // Selection wins over a shape left selected from before.
        assertEquals(DrawingTool.SELECT, activeTool(shape, DrawingScreenState(isSelectionMode = true)))
    }

    @Test
    fun eachToolIsReachedByASingleAction() {
        assertEquals(
            DrawingAction.OnToggleSelectionMode(true),
            toolAction(DrawingTool.SELECT, ShapeType.STAR)
        )
        assertEquals(
            DrawingAction.OnShapeTypeChange(ShapeType.NONE),
            toolAction(DrawingTool.BRUSH, ShapeType.STAR)
        )
        // The shape tool comes back with the shape that was used last.
        assertEquals(
            DrawingAction.OnShapeTypeChange(ShapeType.STAR),
            toolAction(DrawingTool.SHAPE, ShapeType.STAR)
        )
        assertEquals(
            DrawingAction.OnToggleEraser(true),
            toolAction(DrawingTool.ERASER, ShapeType.STAR)
        )
    }
}
