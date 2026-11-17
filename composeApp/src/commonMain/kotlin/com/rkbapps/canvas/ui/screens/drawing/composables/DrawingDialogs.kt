package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import canvas.composeapp.generated.resources.Res
import canvas.composeapp.generated.resources.apply
import canvas.composeapp.generated.resources.cancel
import canvas.composeapp.generated.resources.clear_page
import canvas.composeapp.generated.resources.clear_page_desc
import canvas.composeapp.generated.resources.clear_page_title
import canvas.composeapp.generated.resources.delete
import canvas.composeapp.generated.resources.delete_page_desc
import canvas.composeapp.generated.resources.delete_page_title
import canvas.composeapp.generated.resources.done
import canvas.composeapp.generated.resources.page_a3_landscape
import canvas.composeapp.generated.resources.page_a3_portrait
import canvas.composeapp.generated.resources.page_a4_landscape
import canvas.composeapp.generated.resources.page_a4_portrait
import canvas.composeapp.generated.resources.page_letter_landscape
import canvas.composeapp.generated.resources.page_letter_portrait
import canvas.composeapp.generated.resources.page_size
import canvas.composeapp.generated.resources.page_size_desc
import canvas.composeapp.generated.resources.page_square
import canvas.composeapp.generated.resources.paper_color
import canvas.composeapp.generated.resources.pick_a_color
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.rkbapps.canvas.ui.screens.drawing.DrawingAction
import com.rkbapps.canvas.ui.screens.drawing.utils.paperPalette
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

// ─────────────────────────────────────────────────────────────────────────────
// Dialogs of the drawing screen
// ─────────────────────────────────────────────────────────────────────────────

/** Clearing is undoable, so this only confirms intent — the copy says so. */
@Composable
fun ClearPageDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.clear_page_title)) },
        text = { Text(stringResource(Res.string.clear_page_desc)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(Res.string.clear_page), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(Res.string.cancel)) }
        }
    )
}

/** Deleting a page cannot be undone, so it is always confirmed. */
@Composable
fun DeletePageDialog(pageNumber: Int, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.delete_page_title, pageNumber)) },
        text = { Text(stringResource(Res.string.delete_page_desc)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(Res.string.delete), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(Res.string.cancel)) }
        }
    )
}

@Composable
fun ColorPickerDialog(
    initialColor: Color,
    onDismiss: () -> Unit,
    onDone: (Color) -> Unit,
) {
    val controller = rememberColorPickerController()
    var picked by remember { mutableStateOf(initialColor) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.pick_a_color)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                HsvColorPicker(
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    controller = controller,
                    initialColor = initialColor,
                    onColorChanged = { picked = it.color }
                )
                BrightnessSlider(
                    modifier = Modifier.fillMaxWidth().height(24.dp),
                    controller = controller,
                    borderRadius = 12.dp
                )
                AlphaSlider(
                    modifier = Modifier.fillMaxWidth().height(24.dp),
                    controller = controller,
                    borderRadius = 12.dp
                )
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .clip(ChromeShape)
                        .background(picked)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, ChromeShape)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onDone(picked) }) { Text(stringResource(Res.string.done)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(Res.string.cancel)) }
        }
    )
}

/** Paper colour as a dialog, for layouts with no room to show it inline. */
@Composable
fun PaperColorDialog(
    current: Color,
    onSelected: (Color) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.paper_color)) },
        text = { ColorSwatches(palette = paperPalette, selected = current, onSelected = onSelected) },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(Res.string.done)) }
        }
    )
}

/**
 * [label] is what gets stored with the drawing, so it stays in English;
 * [title] is the translated name shown to the user.
 */
private data class PagePreset(val label: String, val title: StringResource, val width: Float, val height: Float)

private val PAGE_PRESETS = listOf(
    PagePreset("A4 Portrait", Res.string.page_a4_portrait, 794f, 1123f),
    PagePreset("A4 Landscape", Res.string.page_a4_landscape, 1123f, 794f),
    PagePreset("A3 Portrait", Res.string.page_a3_portrait, 1123f, 1587f),
    PagePreset("A3 Landscape", Res.string.page_a3_landscape, 1587f, 1123f),
    PagePreset("Letter Portrait", Res.string.page_letter_portrait, 816f, 1056f),
    PagePreset("Letter Landscape", Res.string.page_letter_landscape, 1056f, 816f),
    PagePreset("Square", Res.string.page_square, 800f, 800f),
)

/** Longest side among the presets; the sheet outlines are drawn relative to it. */
private val LARGEST_SIDE = PAGE_PRESETS.maxOf { maxOf(it.width, it.height) }

// Older drawings stored the square preset with its size in the label.
private fun presetFor(label: String) = PAGE_PRESETS.firstOrNull { label.startsWith(it.label) }

/** The translated name of the page size a drawing stores as [label]. */
@Composable
fun pageSizeName(label: String): String = presetFor(label)?.let { stringResource(it.title) } ?: label

/**
 * Page size picker. Each preset is shown as a sheet outline drawn to scale with
 * the others, so orientation and relative size are visible before choosing.
 */
@Composable
fun PageSizePickerDialog(
    currentLabel: String,
    onAction: (DrawingAction) -> Unit,
) {
    var selected by remember {
        mutableStateOf(presetFor(currentLabel) ?: PAGE_PRESETS.first())
    }
    val dismiss = { onAction(DrawingAction.OnClosePageSizePicker) }

    AlertDialog(
        onDismissRequest = dismiss,
        title = { Text(stringResource(Res.string.page_size)) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                PAGE_PRESETS.forEach { preset ->
                    val isSelected = preset == selected
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(ChromeShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                            )
                            .selectable(selected = isSelected, role = Role.RadioButton) { selected = preset }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                            Box(
                                Modifier
                                    .size(
                                        width = 36.dp * (preset.width / LARGEST_SIDE),
                                        height = 36.dp * (preset.height / LARGEST_SIDE)
                                    )
                                    .background(Color.White, RoundedCornerShape(2.dp))
                                    .border(
                                        1.5.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.outline,
                                        RoundedCornerShape(2.dp)
                                    )
                            )
                        }
                        Text(
                            text = stringResource(preset.title),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${preset.width.toInt()} × ${preset.height.toInt()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = stringResource(Res.string.page_size_desc),
                    modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onAction(DrawingAction.OnSetPageSize(selected.width, selected.height, selected.label))
                dismiss()
            }) { Text(stringResource(Res.string.apply)) }
        },
        dismissButton = {
            TextButton(onClick = dismiss) { Text(stringResource(Res.string.cancel)) }
        }
    )
}
