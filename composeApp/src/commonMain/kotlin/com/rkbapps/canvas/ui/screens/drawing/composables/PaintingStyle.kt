package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import canvas.composeapp.generated.resources.Res
import canvas.composeapp.generated.resources.brush
import canvas.composeapp.generated.resources.brush_style_dot
import canvas.composeapp.generated.resources.brush_style_fill
import canvas.composeapp.generated.resources.brush_style_pencil
import canvas.composeapp.generated.resources.brush_style_scallop
import canvas.composeapp.generated.resources.brush_style_stroke
import canvas.composeapp.generated.resources.choose_brush_style
import canvas.composeapp.generated.resources.outline_stylus_brush
import com.rkbapps.canvas.ui.screens.drawing.utils.PaintingStyleType
import com.rkbapps.canvas.ui.screens.drawing.utils.paintingStyles
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * Mobile: A single Brush tool button that opens a dialog to pick style.
 */
@Composable
fun PaintingStyle(
    selected: PaintingStyleType = PaintingStyleType.STROKE,
    onSelected: (PaintingStyleType) -> Unit = {}
) {
    var isPaintingStyleDialogVisible by remember { mutableStateOf(false) }

    if (isPaintingStyleDialogVisible) {
        PaintingStyleDialog(
            selected = selected,
            onDismissDialog = { isPaintingStyleDialogVisible = false }
        ) {
            onSelected(it)
            isPaintingStyleDialogVisible = false
        }
    }

    CompactToolButton(
        isActive = false,
        onClick = { isPaintingStyleDialogVisible = true }
    ) {
        Icon(
            imageVector = vectorResource(resource = Res.drawable.outline_stylus_brush),
            contentDescription = stringResource(Res.string.brush),
            modifier = Modifier.size(22.dp)
        )
    }
}

/**
 * Tablet / Desktop: Inline FilterChip row — no dialog needed.
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BrushStyleInlineSelector(
    selected: PaintingStyleType,
    onSelected: (PaintingStyleType) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        maxItemsInEachRow = 3,
    ) {
        paintingStyles.forEach { style ->
            val isSelected = selected == style.type
            FilterChip(
                selected = isSelected,
                onClick = { onSelected(style.type) },
                label = {
                    Text(
                        text = stringResource(style.title),
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = style.icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaintingStyleDialog(
    selected: PaintingStyleType,
    onDismissDialog: () -> Unit,
    onSelected: (PaintingStyleType) -> Unit
) {
    Dialog(onDismissRequest = onDismissDialog) {
        androidx.compose.material3.Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 6.dp
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(Res.string.choose_brush_style),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(paintingStyles) { style ->
                        val isSelected = selected == style.type
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelected(style.type) },
                            label = {
                                Text(
                                    text = stringResource(style.title),
                                    style = MaterialTheme.typography.labelSmall,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = style.icon,
                                    contentDescription = stringResource(style.title),
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        )
                    }
                }
            }
        }
    }
}
