package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rkbapps.canvas.ui.screens.drawing.DrawingAction

/** Standard page presets. */
private data class PagePreset(val label: String, val width: Float, val height: Float)

private val PAGE_PRESETS = listOf(
    PagePreset("A4 Portrait",    794f,  1123f),
    PagePreset("A4 Landscape",  1123f,   794f),
    PagePreset("A3 Portrait",   1123f,  1587f),
    PagePreset("A3 Landscape",  1587f,  1123f),
    PagePreset("Letter Portrait", 816f, 1056f),
    PagePreset("Letter Landscape", 1056f, 816f),
    PagePreset("Square (800×800)", 800f, 800f),
)

/**
 * Dialog that lets the user pick a standard page size for the current drawing.
 * Fires [onAction] with [DrawingAction.OnSetPageSize] and then [DrawingAction.OnClosePageSizePicker].
 */
@Composable
fun PageSizePickerDialog(
    currentLabel: String,
    onAction: (DrawingAction) -> Unit,
) {
    var selectedLabel by remember { mutableStateOf(currentLabel) }

    AlertDialog(
        onDismissRequest = { onAction(DrawingAction.OnClosePageSizePicker) },
        title = {
            Text(
                "Page Size",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column {
                Text(
                    "Applies to new drawings. Existing strokes are not rescaled.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                PAGE_PRESETS.forEach { preset ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedLabel = preset.label }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedLabel == preset.label,
                                onClick = { selectedLabel = preset.label }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(preset.label, style = MaterialTheme.typography.bodyMedium)
                        }
                        Text(
                            "${preset.width.toInt()} × ${preset.height.toInt()} dp",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val preset = PAGE_PRESETS.firstOrNull { it.label == selectedLabel }
                    ?: PAGE_PRESETS.first()
                onAction(DrawingAction.OnSetPageSize(preset.width, preset.height, preset.label))
                onAction(DrawingAction.OnClosePageSizePicker)
            }) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = { onAction(DrawingAction.OnClosePageSizePicker) }) {
                Text("Cancel")
            }
        },
        shape = MaterialTheme.shapes.extraLarge
    )
}
