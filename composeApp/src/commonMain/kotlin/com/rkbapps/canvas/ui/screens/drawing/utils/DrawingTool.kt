package com.rkbapps.canvas.ui.screens.drawing.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.ui.screens.drawing.DrawingAction
import com.rkbapps.canvas.ui.screens.drawing.DrawingScreenState

/**
 * The four mutually exclusive things a pointer can do on the page.
 *
 * The underlying state keeps this in three separate fields (selection mode,
 * eraser flag, shape type); [activeTool] folds them into the single choice the
 * person actually makes, and [toolAction] maps a choice back to one action.
 */
enum class DrawingTool { SELECT, BRUSH, SHAPE, ERASER }

fun activeTool(state: DrawingState, uiState: DrawingScreenState): DrawingTool = when {
    uiState.isSelectionMode -> DrawingTool.SELECT
    uiState.isEraserSelected -> DrawingTool.ERASER
    state.selectedShapeType != ShapeType.NONE -> DrawingTool.SHAPE
    else -> DrawingTool.BRUSH
}

/** The single action that switches to [tool]; each one also clears the other tools' flags. */
fun toolAction(tool: DrawingTool, lastShape: ShapeType): DrawingAction = when (tool) {
    DrawingTool.SELECT -> DrawingAction.OnToggleSelectionMode(true)
    DrawingTool.BRUSH -> DrawingAction.OnShapeTypeChange(ShapeType.NONE)
    DrawingTool.SHAPE -> DrawingAction.OnShapeTypeChange(lastShape)
    DrawingTool.ERASER -> DrawingAction.OnToggleEraser(true)
}

/** Inks offered as one-tap swatches; anything else goes through the custom picker. */
val inkPalette = listOf(
    Color(0xFF000000), // black
    Color(0xFF5F6368), // graphite
    Color(0xFFFFFFFF), // white
    Color(0xFFD93025), // red
    Color(0xFFF2811D), // orange
    Color(0xFFF4B400), // amber
    Color(0xFF2E9E5B), // green
    Color(0xFF12A0A0), // teal
    Color(0xFF2563EB), // blue
    Color(0xFF5B4FE0), // indigo
    Color(0xFFA24BD6), // violet
    Color(0xFFE8518D), // pink
    Color(0xFF8D5B3F), // brown
)

/** Paper tints offered for the page background. */
val paperPalette = listOf(
    Color(0xFFFFFFFF), // white
    Color(0xFFFBF5E6), // cream
    Color(0xFFEEF1F4), // cool grey
    Color(0xFFE6D5B8), // kraft
    Color(0xFF2A2C30), // charcoal
    Color(0xFF000000), // black
)

/** Stroke widths offered as one-tap presets, in page units. */
val widthPresets = listOf(2f, 5f, 10f, 20f, 40f)

/** Arrow-pointer icon for the select tool; Material's icon set has no plain cursor. */
val SelectToolIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "SelectTool",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        fill = null,
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.8f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
        pathFillType = PathFillType.NonZero
    ) {
        moveTo(5.5f, 3.5f)
        lineTo(5.5f, 19f)
        lineTo(9.8f, 15.2f)
        lineTo(12.6f, 21f)
        lineTo(15.1f, 19.8f)
        lineTo(12.3f, 14.1f)
        lineTo(18f, 14.1f)
        close()
    }.build()
}
