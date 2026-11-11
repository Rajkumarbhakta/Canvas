package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import canvas.composeapp.generated.resources.Res
import canvas.composeapp.generated.resources.color
import canvas.composeapp.generated.resources.custom_color
import canvas.composeapp.generated.resources.delete_selection
import canvas.composeapp.generated.resources.eraser_hint
import canvas.composeapp.generated.resources.select_hint
import canvas.composeapp.generated.resources.stroke_style
import canvas.composeapp.generated.resources.stroke_width
import canvas.composeapp.generated.resources.tool_shape
import com.rkbapps.canvas.ui.composables.drawPath
import com.rkbapps.canvas.ui.composables.drawShape
import com.rkbapps.canvas.ui.screens.drawing.DrawingAction
import com.rkbapps.canvas.ui.screens.drawing.utils.DrawingTool
import com.rkbapps.canvas.ui.screens.drawing.utils.PaintingStyleType
import com.rkbapps.canvas.ui.screens.drawing.utils.ShapeType
import com.rkbapps.canvas.ui.screens.drawing.utils.inkPalette
import com.rkbapps.canvas.ui.screens.drawing.utils.paintingStyles
import com.rkbapps.canvas.ui.screens.drawing.utils.shapeOptions
import com.rkbapps.canvas.ui.screens.drawing.utils.widthPresets
import org.jetbrains.compose.resources.stringResource
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

// ─────────────────────────────────────────────────────────────────────────────
// Tool inspector — the options that belong to the active tool, and nothing else.
// Used as the side panel on wide screens and as the pull-up sheet on phones.
// ─────────────────────────────────────────────────────────────────────────────

private const val MIN_WIDTH = 1f
private const val MAX_WIDTH = 200f

@Composable
fun ToolInspector(
    tool: DrawingTool,
    color: Color,
    thickness: Float,
    style: PaintingStyleType,
    shape: ShapeType,
    paperColor: Color,
    hasSelection: Boolean,
    onAction: (DrawingAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        when (tool) {
            DrawingTool.SELECT -> {
                Hint(stringResource(Res.string.select_hint))
                if (hasSelection) {
                    OutlinedButton(
                        onClick = { onAction(DrawingAction.OnDeleteSelectedPath) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = ChromeShape
                    ) {
                        Icon(Icons.Outlined.Delete, null, Modifier.size(18.dp))
                        Text(stringResource(Res.string.delete_selection), Modifier.padding(start = 8.dp))
                    }
                }
            }

            DrawingTool.BRUSH, DrawingTool.SHAPE -> {
                StrokePreview(
                    color = color,
                    thickness = thickness,
                    style = style,
                    shape = if (tool == DrawingTool.SHAPE) shape else ShapeType.NONE,
                    paperColor = paperColor
                )
                if (tool == DrawingTool.SHAPE) {
                    Section(stringResource(Res.string.tool_shape)) {
                        ShapeGrid(shape) { onAction(DrawingAction.OnShapeTypeChange(it)) }
                    }
                }
                Section(stringResource(Res.string.color)) {
                    ColorSwatches(
                        palette = inkPalette,
                        selected = color,
                        onSelected = { onAction(DrawingAction.OnSelectColor(it)) }
                    )
                }
                Section(stringResource(Res.string.stroke_style)) {
                    StyleTiles(style) { onAction(DrawingAction.OnPathEffectChange(it)) }
                }
                WidthSection(thickness, color, isEraser = false) {
                    onAction(DrawingAction.OnThicknessChange(it))
                }
            }

            DrawingTool.ERASER -> {
                Hint(stringResource(Res.string.eraser_hint))
                WidthSection(thickness, color, isEraser = true) {
                    onAction(DrawingAction.OnThicknessChange(it))
                }
            }
        }
    }
}

@Composable
fun Section(
    title: String,
    modifier: Modifier = Modifier,
    trailing: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            trailing()
        }
        content()
    }
}

@Composable
private fun Hint(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

// ── Preview ──────────────────────────────────────────────────────────────────

/**
 * A sample of what the next stroke (or shape) will look like, drawn by the same
 * code that renders the page, on the page's own paper colour.
 */
@Composable
private fun StrokePreview(
    color: Color,
    thickness: Float,
    style: PaintingStyleType,
    shape: ShapeType,
    paperColor: Color,
) {
    Canvas(
        Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(ChromeShape)
            .background(paperColor)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, ChromeShape)
    ) {
        if (shape == ShapeType.NONE) {
            // Shown at the real width, capped so a 200-unit stroke still fits.
            val width = thickness.coerceAtMost(size.height * 0.45f)
            drawPath(wavePoints(size, inset = 20.dp.toPx() + width / 2), color, width, style)
        } else {
            // A shape this small is a scale model, so its outline is scaled down with it.
            val width = (thickness * 0.3f).coerceIn(1.5.dp.toPx(), 5.dp.toPx())
            drawShape(shapePreviewPoints(shape, size), color, width, style, shape)
        }
    }
}

/** [periods] of a sine wave across [size], as stroke sample points. */
private fun wavePoints(
    size: Size,
    inset: Float,
    amplitude: Float = 0.22f,
    periods: Float = 1.5f,
): List<Offset> {
    val steps = 48
    val span = (size.width - inset * 2).coerceAtLeast(1f)
    return List(steps + 1) { i ->
        val t = i / steps.toFloat()
        Offset(
            x = inset + span * t,
            y = size.height / 2 - sin(t * periods * 2 * PI).toFloat() * size.height * amplitude
        )
    }
}

/** Start/end points that make [shape] sit centred in a preview of [size]. */
private fun shapePreviewPoints(shape: ShapeType, size: Size): List<Offset> {
    val c = Offset(size.width / 2, size.height / 2)
    val r = size.height * 0.36f
    return when (shape) {
        ShapeType.CIRCLE, ShapeType.STAR, ShapeType.PENTAGON, ShapeType.HEXAGON ->
            listOf(c, c + Offset(r, 0f))
        ShapeType.OVAL -> listOf(c, c + Offset(r * 1.6f, r))
        ShapeType.LINE -> listOf(c + Offset(-r * 2, r * 0.6f), c + Offset(r * 2, -r * 0.6f))
        ShapeType.ARROW_RIGHT -> listOf(c + Offset(-r * 1.6f, 0f), c + Offset(r * 1.6f, r))
        ShapeType.ARROW_LEFT -> listOf(c + Offset(r * 1.6f, 0f), c + Offset(-r * 1.6f, r))
        ShapeType.ARROW_UP -> listOf(c + Offset(0f, r), c + Offset(r, -r))
        ShapeType.ARROW_DOWN -> listOf(c + Offset(0f, -r), c + Offset(r, r))
        ShapeType.SQUARE -> listOf(c - Offset(r, r), c + Offset(r, r))
        else -> listOf(c - Offset(r * 1.5f, r), c + Offset(r * 1.5f, r))
    }
}

// ── Colour ───────────────────────────────────────────────────────────────────

/**
 * One-tap swatches plus a custom picker. A colour that is not in [palette]
 * (picked earlier, or loaded with the drawing) gets its own swatch so the
 * current choice is always visible.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorSwatches(
    palette: List<Color>,
    selected: Color,
    onSelected: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    var pickerOpen by remember { mutableStateOf(false) }
    if (pickerOpen) {
        ColorPickerDialog(
            initialColor = selected,
            onDismiss = { pickerOpen = false },
            onDone = {
                onSelected(it)
                pickerOpen = false
            }
        )
    }

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        palette.forEach { swatch ->
            Swatch(swatch, isSelected = swatch == selected) { onSelected(swatch) }
        }
        if (selected !in palette) {
            Swatch(selected, isSelected = true) { pickerOpen = true }
        }
        val custom = stringResource(Res.string.custom_color)
        Tip(custom) {
            Box(
                Modifier
                    .size(SwatchSize)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(Brush.sweepGradient(RainbowColors))
                    .clickable(role = Role.Button) { pickerOpen = true }
                    .semantics { contentDescription = custom }
            )
        }
    }
}

private val SwatchSize = 30.dp
private val RainbowColors = listOf(
    Color(0xFFE53935), Color(0xFFFDD835), Color(0xFF43A047),
    Color(0xFF00ACC1), Color(0xFF3949AB), Color(0xFFD81B60), Color(0xFFE53935),
)

/** A colour dot; the selected one gets a ring with a gap, which reads on any colour. */
@Composable
private fun Swatch(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .size(SwatchSize)
            .clip(CircleShape)
            .clickable(role = Role.RadioButton, onClick = onClick)
            .semantics { selected = isSelected }
            .then(
                if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                else Modifier
            )
            .padding(if (isSelected) 5.dp else 2.dp)
            .clip(CircleShape)
            .background(color)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
    )
}

// ── Style ────────────────────────────────────────────────────────────────────

/** One tile per stroke style, each showing a short sample drawn in that style. */
@Composable
private fun StyleTiles(selected: PaintingStyleType, onSelected: (PaintingStyleType) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        paintingStyles.forEach { option ->
            val isSelected = option.type == selected
            val label = stringResource(option.title)
            val ink = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onSurfaceVariant
            Tip(label, Modifier.weight(1f)) {
                Canvas(
                    Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(ChromeShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceContainerHigh
                        )
                        .clickable(role = Role.RadioButton) { onSelected(option.type) }
                        .semantics {
                            contentDescription = label
                            this.selected = isSelected
                        }
                ) {
                    drawPath(
                        path = wavePoints(size, inset = 8.dp.toPx(), amplitude = 0.17f, periods = 1f),
                        color = ink,
                        thickness = 3.dp.toPx(),
                        pathEffect = option.type
                    )
                }
            }
        }
    }
}

// ── Width ────────────────────────────────────────────────────────────────────

@Composable
private fun WidthSection(
    thickness: Float,
    color: Color,
    isEraser: Boolean,
    onChange: (Float) -> Unit,
) {
    Section(
        title = stringResource(Res.string.stroke_width),
        trailing = {
            Text(
                text = "${thickness.roundToInt()}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            widthPresets.forEach { preset ->
                val isSelected = abs(thickness - preset) < 0.5f
                Box(
                    Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(ChromeShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceContainerHigh
                        )
                        .clickable(role = Role.RadioButton) { onChange(preset) }
                        .semantics {
                            contentDescription = "${preset.roundToInt()}"
                            selected = isSelected
                        },
                    contentAlignment = Alignment.Center
                ) {
                    NibPreview(
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        thickness = preset,
                        isEraser = isEraser,
                        size = 30.dp,
                        fullSizeAt = 50f
                    )
                }
            }
        }
        // Quadratic slider: half the travel covers widths up to ~50, where most drawing happens.
        Slider(
            value = sqrt((thickness - MIN_WIDTH) / (MAX_WIDTH - MIN_WIDTH)).coerceIn(0f, 1f),
            onValueChange = { onChange(MIN_WIDTH + (MAX_WIDTH - MIN_WIDTH) * it * it) },
            modifier = Modifier.fillMaxWidth().height(32.dp)
        )
    }
}

// ── Shapes ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ShapeGrid(selected: ShapeType, onSelected: (ShapeType) -> Unit) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        shapeOptions.filter { it.type != ShapeType.NONE }.forEach { option ->
            ChromeButton(
                label = stringResource(option.title),
                selected = option.type == selected,
                size = 38.dp,
                onClick = { onSelected(option.type) }
            ) { Icon(option.icon, null, Modifier.size(20.dp)) }
        }
    }
}
