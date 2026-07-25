package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rkbapps.canvas.ui.screens.drawing.DrawingAction
import com.rkbapps.canvas.ui.screens.drawing.utils.PaintingStyle
import com.rkbapps.canvas.ui.screens.drawing.utils.PaintingStyleType
import com.rkbapps.canvas.ui.screens.drawing.utils.ShapeType

@Composable
fun RightPanelUiForLargeScreen(
    selectedPaintingStyle: PaintingStyleType,
    selectedShape: ShapeType,
    selectedColor: Color,
    selectedThickness: Float,
    onAction:(action: DrawingAction) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(240.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Section: Brush Style
            PropertySection(title = "Brush Style") {
                BrushStyleInlineSelector(
                    selected = selectedPaintingStyle,
                    onSelected = { pathEffect-> onAction(DrawingAction.OnPathEffectChange(pathEffect)) }
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Section: Shapes
            PropertySection(title = "Shapes") {
                ShapeInlineGrid(
                    selectedShape = selectedShape,
                    onShapeSelected = {shapeType->
                        onAction(DrawingAction.OnEraserUnselected)
                        onAction(DrawingAction.OnToggleEraser(false))
                        onAction(DrawingAction.OnShapeTypeChange(shapeType))
                    }
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Section: Stroke Color
            PropertySection(title = "Stroke Color") {
                ColorSwatchPanel(
                    selectedColor = selectedColor,
                    onColorSelected = {color->
                        onAction(DrawingAction.OnEraserUnselected)
                        onAction(DrawingAction.OnToggleEraser(false))
                        onAction(DrawingAction.OnSelectColor(color))
                    }
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Section: Background Color
            PropertySection(title = "Canvas Background") {
                BackgroundColorChangeItem { color ->
                    onAction(DrawingAction.OnBackgroundColorChange(color))
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Section: Thickness
            PropertySection(title = "Stroke Width") {
                ThicknessManagement(value = selectedThickness) {thickness ->
                    onAction(DrawingAction.OnThicknessChange(thickness))
                }
            }
        }
    }
}