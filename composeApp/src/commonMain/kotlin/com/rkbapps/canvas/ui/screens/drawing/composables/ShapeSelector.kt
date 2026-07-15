package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.ChangeHistory
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Hexagon
import androidx.compose.material.icons.outlined.Pentagon
import androidx.compose.material.icons.outlined.Rectangle
import androidx.compose.material.icons.outlined.Square
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import canvas.composeapp.generated.resources.Res
import canvas.composeapp.generated.resources.choose_shape
import canvas.composeapp.generated.resources.shape_arrow_down
import canvas.composeapp.generated.resources.shape_arrow_left
import canvas.composeapp.generated.resources.shape_arrow_right
import canvas.composeapp.generated.resources.shape_arrow_up
import canvas.composeapp.generated.resources.shape_circle
import canvas.composeapp.generated.resources.shape_hexagon
import canvas.composeapp.generated.resources.shape_line
import canvas.composeapp.generated.resources.shape_none
import canvas.composeapp.generated.resources.shape_pentagon
import canvas.composeapp.generated.resources.shape_rectangle
import canvas.composeapp.generated.resources.shape_square
import canvas.composeapp.generated.resources.shape_star
import canvas.composeapp.generated.resources.shape_triangle
import canvas.composeapp.generated.resources.shapes
import com.rkbapps.canvas.ui.screens.drawing.utils.ShapeType
import com.rkbapps.canvas.ui.screens.drawing.utils.shapeOptions
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource



/**
 * Mobile: A Shape button that opens a dialog picker.
 */
@Composable
fun ShapeSelector(
    selectedShape: ShapeType = ShapeType.NONE,
    onShapeSelected: (ShapeType) -> Unit
) {
    var isShapeDialogVisible by remember { mutableStateOf(false) }

    if (isShapeDialogVisible) {
        ShapeSelectorDialog(
            selected = selectedShape,
            onDismissDialog = { isShapeDialogVisible = false }
        ) {
            onShapeSelected(it)
            isShapeDialogVisible = false
        }
    }

    CompactToolButton(
        isActive = selectedShape != ShapeType.NONE,
        onClick = { isShapeDialogVisible = true }
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.shapes),
            contentDescription = stringResource(Res.string.shapes),
            modifier = Modifier.size(22.dp)
        )
    }
}

/**
 * Tablet / Desktop: Inline FlowRow of FilterChips — no dialog.
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ShapeInlineGrid(
    selectedShape: ShapeType,
    onShapeSelected: (ShapeType) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        maxItemsInEachRow = 4,
    ) {
        shapeOptions.forEach { shape ->
            val isSelected = selectedShape == shape.type
            FilterChip(
                selected = isSelected,
                onClick = { onShapeSelected(shape.type) },
                label = {
                    Text(
                        text = stringResource(shape.title),
                        style = MaterialTheme.typography.labelSmall,
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = shape.icon,
                        contentDescription = stringResource(shape.title),
                        modifier = Modifier.size(14.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShapeSelectorDialog(
    selected: ShapeType,
    onDismissDialog: () -> Unit,
    onSelected: (ShapeType) -> Unit
) {
    Dialog(onDismissRequest = onDismissDialog) {

       Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(Res.string.choose_shape),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 100.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(shapeOptions) { shape ->
                        val isSelected = selected == shape.type
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelected(shape.type) },
                            modifier = Modifier.height(50.dp),
                            label = {
                                Text(
                                    text = stringResource(shape.title),
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = shape.icon,
                                    contentDescription = stringResource(shape.title),
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                selectedLeadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        )
                    }
                }
            }
        }
    }
}