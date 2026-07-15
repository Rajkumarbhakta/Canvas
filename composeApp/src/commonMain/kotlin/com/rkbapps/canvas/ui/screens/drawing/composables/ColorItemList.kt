package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import canvas.composeapp.generated.resources.Res
import canvas.composeapp.generated.resources.cancel
import canvas.composeapp.generated.resources.color_picker
import canvas.composeapp.generated.resources.done
import canvas.composeapp.generated.resources.selected
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.rkbapps.canvas.ui.composables.ColorPicker
import com.rkbapps.canvas.util.Constants
import org.jetbrains.compose.resources.stringResource

/**
 * Mobile toolbar: single palette button that opens a color picker dialog.
 */
@Composable
fun ColorItemList(
    selectedColor: Color,
    onColorItemClick: (Color) -> Unit
) {
    val colorPickerController = rememberColorPickerController()
    val isColorPickerDialogVisible = remember { mutableStateOf(false) }

    if (isColorPickerDialogVisible.value) {
        ColorPickerDialog(
            isColorPickerVisible = isColorPickerDialogVisible,
            controller = colorPickerController,
            onDone = onColorItemClick
        )
    }

    CompactToolButton(
        isActive = false,
        onClick = { isColorPickerDialogVisible.value = true }
    ) {
        // Show the active color as a circle inside the button
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(selectedColor)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    shape = CircleShape
                )
        )
    }
}

/**
 * Tablet / Desktop: Inline color swatch grid + custom color picker button.
 *
 * Uses FlowRow instead of LazyVerticalGrid to avoid "infinite height constraints"
 * when placed inside a vertically scrollable Column.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorSwatchPanel(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorPickerController = rememberColorPickerController()
    val isColorPickerDialogVisible = remember { mutableStateOf(false) }

    if (isColorPickerDialogVisible.value) {
        ColorPickerDialog(
            isColorPickerVisible = isColorPickerDialogVisible,
            controller = colorPickerController,
            onDone = onColorSelected
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Preset color swatches — FlowRow works inside verticalScroll (no infinite height issue)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 6,
        ) {
            Constants.colors.forEach { color ->
                ColorItems(
                    color = color,
                    isSelected = color == selectedColor,
                    showCheck = true,
                    onClick = onColorSelected
                )
            }
        }

        // Custom color button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Selected color preview swatch
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(selectedColor)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
            )
            OutlinedButton(
                onClick = { isColorPickerDialogVisible.value = true },
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.small
            ) {
                Icon(
                    imageVector = Icons.Outlined.Palette,
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 4.dp)
                )
                Text(
                    text = "Custom color",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
fun ColorItems(
    color: Color,
    isSelected: Boolean,
    showCheck: Boolean = false,
    imageVector: ImageVector? = null,
    onClick: (Color) -> Unit = {}
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "ColorItemScale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(CircleShape)
            .background(color = color)
            .size(32.dp)
            .clickable { onClick(color) }
            .border(
                width = if (isSelected) 3.dp else 1.5.dp,
                color = if (isSelected) {
                    if (isSystemInDarkTheme() || color == Color.Black) Color.White else Color.Black
                } else MaterialTheme.colorScheme.outlineVariant,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        imageVector?.let {
            Icon(it, contentDescription = stringResource(Res.string.color_picker), modifier = Modifier.size(18.dp))
        }
        if (showCheck) {
            AnimatedVisibility(visible = isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = stringResource(Res.string.selected),
                    modifier = Modifier.size(18.dp),
                    tint = if (isSystemInDarkTheme() || color == Color.Black) Color.White else Color.Black
                )
            }
        }
    }
}

/**
 * Color picker dialog.
 *
 * Uses FlowRow for the preset swatches (not LazyVerticalGrid) so it measures
 * correctly inside the dialog's unbounded-width but bounded-height Column.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorPickerDialog(
    isColorPickerVisible: MutableState<Boolean>,
    controller: ColorPickerController,
    onColorChanged: (ColorEnvelope) -> Unit = {},
    onDone: (Color) -> Unit = {}
) {
    Dialog(onDismissRequest = { isColorPickerVisible.value = false }) {
        androidx.compose.material3.Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Pick a Color",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                ColorPicker(controller, onColorChanged)
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                // FlowRow: bounded height, works inside dialog Column
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    maxItemsInEachRow = 7,
                ) {
                    Constants.colors.forEach { color ->
                        ColorItems(
                            color = color,
                            isSelected = color == controller.selectedColor.value,
                            showCheck = true,
                            onClick = { c ->
                                controller.selectByColor(c, true)
                                onDone(c)
                            }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = { isColorPickerVisible.value = false },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(stringResource(Res.string.cancel))
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onDone(controller.selectedColor.value)
                            isColorPickerVisible.value = false
                        },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(stringResource(Res.string.done))
                    }
                }
            }
        }
    }
}
