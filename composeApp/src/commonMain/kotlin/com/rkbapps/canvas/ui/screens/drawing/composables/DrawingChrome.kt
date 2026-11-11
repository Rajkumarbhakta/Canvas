package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.FullscreenExit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import canvas.composeapp.generated.resources.exit_focus_mode
import canvas.composeapp.generated.resources.rename
import canvas.composeapp.generated.resources.untitled_drawing
import com.rkbapps.canvas.model.CanvasPage
import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.ui.composables.DrawingCanvas
import com.rkbapps.canvas.util.desktopScrollZoom
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Redo
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import canvas.composeapp.generated.resources.Res
import canvas.composeapp.generated.resources.brush
import canvas.composeapp.generated.resources.fit_to_screen
import canvas.composeapp.generated.resources.ink_erase
import canvas.composeapp.generated.resources.outline_stylus_brush
import canvas.composeapp.generated.resources.page_size
import canvas.composeapp.generated.resources.redo
import canvas.composeapp.generated.resources.shapes
import canvas.composeapp.generated.resources.tool_eraser
import canvas.composeapp.generated.resources.tool_select
import canvas.composeapp.generated.resources.undo
import canvas.composeapp.generated.resources.zoom_in
import canvas.composeapp.generated.resources.zoom_out
import com.rkbapps.canvas.ui.screens.drawing.DrawingAction
import com.rkbapps.canvas.ui.screens.drawing.DrawingScreenState
import com.rkbapps.canvas.ui.screens.drawing.utils.DrawingTool
import com.rkbapps.canvas.ui.screens.drawing.utils.SelectToolIcon
import com.rkbapps.canvas.util.Platforms
import com.rkbapps.canvas.util.getPlatform
import androidx.compose.ui.geometry.Offset
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt
import kotlin.math.sqrt

// ─────────────────────────────────────────────────────────────────────────────
// Building blocks shared by every drawing layout: buttons, tool set, history,
// zoom and page-size controls.
// ─────────────────────────────────────────────────────────────────────────────

/** Corner shape shared by tool buttons, chips and floating bars. */
internal val ChromeShape = RoundedCornerShape(12.dp)

/** Zoom factor applied by one press of the zoom buttons or shortcuts. */
const val ZOOM_STEP = 1.25f

/** Keyboard shortcuts are only worth advertising where there is a keyboard. */
private val showShortcuts = getPlatform() == Platforms.DESKTOP

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Tip(text: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    // TooltipBox puts its modifier on an inner node, so layout modifiers (weight,
    // align, padding) would never reach the parent; this outer Box carries them.
    Box(modifier) {
        TooltipBox(
            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
            tooltip = { PlainTooltip { Text(text) } },
            state = rememberTooltipState(),
            content = content
        )
    }
}

/**
 * Square icon button used across the drawing chrome.
 *
 * Unselected buttons have no container, so a row of them reads as one quiet
 * strip and the selected tool is the only filled shape in it.
 *
 * @param label accessible name, also shown as the tooltip
 * @param shortcut key hint appended to the tooltip on desktop
 */
@Composable
fun ChromeButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    shortcut: String? = null,
    size: Dp = 44.dp,
    /** Quieter selected state, for toggles that are not the active tool. */
    subtle: Boolean = false,
    content: @Composable () -> Unit,
) {
    val container by animateColorAsState(
        when {
            !selected -> Color.Transparent
            subtle -> MaterialTheme.colorScheme.surfaceContainerHighest
            else -> MaterialTheme.colorScheme.primaryContainer
        },
        label = "ChromeButtonContainer"
    )
    val contentColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        selected && subtle -> MaterialTheme.colorScheme.onSurface
        selected -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Tip(if (shortcut != null && showShortcuts) "$label ($shortcut)" else label, modifier) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(ChromeShape)
                .background(container)
                .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
                .semantics {
                    contentDescription = label
                    this.selected = selected
                },
            contentAlignment = Alignment.Center
        ) {
            CompositionLocalProvider(LocalContentColor provides contentColor, content = content)
        }
    }
}

@Composable
fun ToolIcon(tool: DrawingTool, modifier: Modifier = Modifier) {
    when (tool) {
        DrawingTool.SELECT -> Icon(SelectToolIcon, null, modifier.size(22.dp))
        DrawingTool.BRUSH -> Icon(painterResource(Res.drawable.outline_stylus_brush), null, modifier.size(22.dp))
        DrawingTool.SHAPE -> Icon(painterResource(Res.drawable.shapes), null, modifier.size(22.dp))
        DrawingTool.ERASER -> Icon(painterResource(Res.drawable.ink_erase), null, modifier.size(22.dp))
    }
}

@Composable
fun toolLabel(tool: DrawingTool): String = stringResource(
    when (tool) {
        DrawingTool.SELECT -> Res.string.tool_select
        DrawingTool.BRUSH -> Res.string.brush
        DrawingTool.SHAPE -> Res.string.shapes
        DrawingTool.ERASER -> Res.string.tool_eraser
    }
)

private fun toolShortcut(tool: DrawingTool) = when (tool) {
    DrawingTool.SELECT -> "V"
    DrawingTool.BRUSH -> "B"
    DrawingTool.SHAPE -> "U"
    DrawingTool.ERASER -> "E"
}

/** The four tool buttons. Emits them bare so the caller decides Row or Column. */
@Composable
fun ToolButtons(
    activeTool: DrawingTool,
    onSelectTool: (DrawingTool) -> Unit,
    buttonSize: Dp = 44.dp,
) {
    DrawingTool.entries.forEach { tool ->
        ChromeButton(
            label = toolLabel(tool),
            shortcut = toolShortcut(tool),
            selected = tool == activeTool,
            size = buttonSize,
            onClick = { onSelectTool(tool) }
        ) { ToolIcon(tool) }
    }
}

/** Undo and redo. Always present so the toolbar never shifts; disabled when there is nothing to do. */
@Composable
fun UndoRedoButtons(
    canUndo: Boolean,
    canRedo: Boolean,
    onAction: (DrawingAction) -> Unit,
    buttonSize: Dp = 44.dp,
) {
    ChromeButton(
        label = stringResource(Res.string.undo),
        shortcut = "Ctrl+Z",
        enabled = canUndo,
        size = buttonSize,
        onClick = { onAction(DrawingAction.OnUndo) }
    ) { Icon(Icons.AutoMirrored.Rounded.Undo, null, Modifier.size(22.dp)) }
    ChromeButton(
        label = stringResource(Res.string.redo),
        shortcut = "Ctrl+Y",
        enabled = canRedo,
        size = buttonSize,
        onClick = { onAction(DrawingAction.OnRedo) }
    ) { Icon(Icons.AutoMirrored.Rounded.Redo, null, Modifier.size(22.dp)) }
}

/**
 * The current nib at a glance: a dot in the ink colour whose size follows the
 * stroke width. The eraser shows as a hollow ring.
 */
@Composable
fun NibPreview(
    color: Color,
    thickness: Float,
    isEraser: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
    /** Width drawn at full size; lower it to spread out a row of small presets. */
    fullSizeAt: Float = 200f,
) {
    val outline = MaterialTheme.colorScheme.outline
    Canvas(modifier.size(size)) {
        val maxRadius = this.size.minDimension / 2f
        val minRadius = 2.dp.toPx()
        // Square-root scale: thin strokes stay distinguishable, thick ones still fit.
        val radius = minRadius + sqrt((thickness / fullSizeAt).coerceIn(0f, 1f)) * (maxRadius - minRadius)
        if (isEraser) {
            drawCircle(outline, radius - 0.75.dp.toPx(), style = Stroke(1.5.dp.toPx()))
        } else {
            drawCircle(color, radius)
            drawCircle(outline.copy(alpha = 0.5f), radius, style = Stroke(1.dp.toPx()))
        }
    }
}

/** The nib inside a ring, so it reads as a control and not as a stray dot. */
@Composable
fun NibBadge(color: Color, thickness: Float, isEraser: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(32.dp)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        NibPreview(color, thickness, isEraser, size = 24.dp)
    }
}

/** Floating surface for controls that sit on top of the desk. */
@Composable
fun FloatingBar(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = 3.dp,
        content = content
    )
}

/** Zoom out, current zoom (press to fit), zoom in. Works with every tool. */
@Composable
fun ZoomControl(
    zoom: Float,
    onAction: (DrawingAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    FloatingBar(modifier) {
        Row(Modifier.padding(2.dp), verticalAlignment = Alignment.CenterVertically) {
            ChromeButton(
                label = stringResource(Res.string.zoom_out),
                shortcut = "Ctrl+−",
                enabled = zoom > DrawingScreenState.MIN_ZOOM,
                size = 36.dp,
                onClick = { onAction(DrawingAction.OnZoomPan(1f / ZOOM_STEP, Offset.Zero)) }
            ) { Icon(Icons.Rounded.Remove, null, Modifier.size(18.dp)) }
            ZoomLabel(zoom, onAction)
            ChromeButton(
                label = stringResource(Res.string.zoom_in),
                shortcut = "Ctrl++",
                enabled = zoom < DrawingScreenState.MAX_ZOOM,
                size = 36.dp,
                onClick = { onAction(DrawingAction.OnZoomPan(ZOOM_STEP, Offset.Zero)) }
            ) { Icon(Icons.Rounded.Add, null, Modifier.size(18.dp)) }
        }
    }
}

/** Zoom percentage (100% = page fitted to the screen); pressing it fits the page again. */
@Composable
fun ZoomLabel(
    zoom: Float,
    onAction: (DrawingAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val fit = stringResource(Res.string.fit_to_screen)
    Tip(if (showShortcuts) "$fit (Ctrl+0)" else fit, modifier) {
        Text(
            text = "${(zoom * 100).roundToInt()}%",
            modifier = Modifier
                .clip(ChromeShape)
                .clickable(role = Role.Button, onClickLabel = fit) { onAction(DrawingAction.OnResetView) }
                .widthIn(min = 52.dp)
                .padding(horizontal = 6.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

/** Shows the current page size and opens the size picker. */
@Composable
fun PageSizeChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val description = stringResource(Res.string.page_size)
    Tip(description, modifier) {
        FloatingBar {
            Row(
                modifier = Modifier
                    .clickable(role = Role.Button, onClickLabel = description, onClick = onClick)
                    .height(40.dp)
                    .padding(start = 10.dp, end = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Description, null,
                    Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    Icons.Rounded.KeyboardArrowDown, null,
                    Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/** Thin separator between groups inside a horizontal bar. */
@Composable
fun BarDivider(modifier: Modifier = Modifier) {
    Box(
        modifier
            .padding(horizontal = 4.dp)
            .width(1.dp)
            .height(24.dp)
            .background(MaterialTheme.colorScheme.outlineVariant)
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Pieces shared by the wide and the phone layout
// ─────────────────────────────────────────────────────────────────────────────

/** The drawing's name; pressing it renames the drawing. */
@Composable
fun DrawingTitle(name: String, onRename: () -> Unit, modifier: Modifier = Modifier) {
    val rename = stringResource(Res.string.rename)
    Tip(rename, modifier) {
        Row(
            modifier = Modifier
                .clip(ChromeShape)
                .clickable(role = Role.Button, onClickLabel = rename, onClick = onRename)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name.ifBlank { stringResource(Res.string.untitled_drawing) },
                modifier = Modifier.weight(1f, fill = false),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.width(6.dp))
            Icon(
                Icons.Outlined.Edit, null,
                Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** An icon button that opens a dropdown menu. [content] receives a function that closes it. */
@Composable
fun MenuButton(
    label: String,
    icon: ImageVector,
    content: @Composable ColumnScope.(close: () -> Unit) -> Unit,
) {
    var open by remember { mutableStateOf(false) }
    Box {
        ChromeButton(label = label, onClick = { open = true }) { Icon(icon, null, Modifier.size(22.dp)) }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            content { open = false }
        }
    }
}

@Composable
fun MenuItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    destructive: Boolean = false,
    enabled: Boolean = true,
) {
    val tint = if (destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
    DropdownMenuItem(
        text = { Text(text, color = if (destructive) MaterialTheme.colorScheme.error else Color.Unspecified) },
        leadingIcon = { Icon(icon, null, Modifier.size(20.dp), tint = tint) },
        enabled = enabled,
        onClick = onClick
    )
}

/** The only chrome left in focus mode: tools, history and the way out. */
@Composable
fun FocusDock(
    activeTool: DrawingTool,
    canUndo: Boolean,
    canRedo: Boolean,
    onSelectTool: (DrawingTool) -> Unit,
    onAction: (DrawingAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    FloatingBar(modifier) {
        Row(Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
            ToolButtons(activeTool, onSelectTool, buttonSize = 40.dp)
            BarDivider()
            UndoRedoButtons(canUndo, canRedo, onAction, buttonSize = 40.dp)
            BarDivider()
            ChromeButton(
                label = stringResource(Res.string.exit_focus_mode),
                shortcut = "Esc",
                size = 40.dp,
                onClick = { onAction(DrawingAction.OnExitFullScreen) }
            ) { Icon(Icons.Rounded.FullscreenExit, null, Modifier.size(22.dp)) }
        }
    }
}

/** The canvas wired to the drawing state; the same call for every layout. */
@Composable
fun DrawingSurface(
    state: DrawingState,
    uiState: DrawingScreenState,
    currentPage: CanvasPage,
    onAction: (DrawingAction) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    DrawingCanvas(
        paths = if (uiState.isLegacy) state.paths else currentPage.paths,
        currentPath = state.currentPath,
        backgroundColor = if (uiState.isLegacy) state.backgroundColor else currentPage.backgroundColor,
        onAction = onAction,
        isSelectionMode = uiState.isSelectionMode,
        selectedPathId = uiState.selectedPathId,
        dragOffset = state.dragOffset,
        zoom = uiState.zoom,
        panOffset = uiState.panOffset,
        pageWidth = state.pageWidth,
        pageHeight = state.pageHeight,
        isLegacy = uiState.isLegacy,
        // A neutral, slightly dimmed desk so the paper colour reads true in both themes.
        deskColor = MaterialTheme.colorScheme.surfaceDim,
        contentPadding = contentPadding,
        modifier = modifier.desktopScrollZoom(
            isLegacy = uiState.isLegacy,
            isSelectionMode = uiState.isSelectionMode,
            onAction = onAction,
        ),
    )
}

/** Dialogs a layout can ask for; the page-size picker is driven by [DrawingScreenState] instead. */
enum class DrawingDialog { CLEAR_PAGE, DELETE_PAGE, PAPER_COLOR }

@Composable
fun DrawingDialogs(
    dialog: DrawingDialog?,
    state: DrawingState,
    uiState: DrawingScreenState,
    paperColor: Color,
    onAction: (DrawingAction) -> Unit,
    onDismiss: () -> Unit,
) {
    when (dialog) {
        DrawingDialog.CLEAR_PAGE -> ClearPageDialog(
            onConfirm = {
                onAction(DrawingAction.OnClearCanvasList)
                onDismiss()
            },
            onDismiss = onDismiss
        )

        DrawingDialog.DELETE_PAGE -> DeletePageDialog(
            pageNumber = uiState.currentPageIndex + 1,
            onConfirm = {
                onAction(DrawingAction.OnDeletePage(uiState.currentPageIndex))
                onDismiss()
            },
            onDismiss = onDismiss
        )

        DrawingDialog.PAPER_COLOR -> PaperColorDialog(
            current = paperColor,
            onSelected = { onAction(DrawingAction.OnBackgroundColorChange(it)) },
            onDismiss = onDismiss
        )

        null -> Unit
    }
    if (uiState.isPageSizePickerVisible) {
        PageSizePickerDialog(currentLabel = state.pageSizeLabel, onAction = onAction)
    }
}
