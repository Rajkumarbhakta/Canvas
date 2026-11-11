package com.rkbapps.canvas.ui.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StampedPathEffectStyle
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.rkbapps.canvas.model.PathData
import com.rkbapps.canvas.model.buildSmoothPath
import com.rkbapps.canvas.ui.screens.drawing.DrawingAction
import com.rkbapps.canvas.ui.screens.drawing.DrawingScreenState
import com.rkbapps.canvas.ui.screens.drawing.utils.PaintingStyleType
import com.rkbapps.canvas.ui.screens.drawing.utils.ShapeType
import com.rkbapps.canvas.util.detectCanvasTransformGestures
import com.rkbapps.canvas.util.detectTapAndDrag
import kotlin.math.*

// ─────────────────────────────────────────────────────────────────────────────
// DrawingCanvas
//
// Two render modes controlled by [isLegacy]:
//
//  • isLegacy = true  → original full-screen canvas (no page frame, no zoom).
//                       Renders exactly as before so old drawings are never broken.
//
//  • isLegacy = false → new paged canvas:
//                        - outer Box = grey "desk" that accepts 2-finger pan/zoom
//                        - inner Canvas = white page with a shadow, laid out at
//                          exactly [pageWidth]×[pageHeight] px, so stroke
//                          coordinates are page units on every screen density.
//                        - graphicsLayer scales the page to fit the desk, then
//                          applies [zoom] + [panOffset] on top (zoom 1 = fit).
//                        - 1-finger gestures hit the inner Canvas only.
//
// ─────────────────────────────────────────────────────────────────────────────

/** Background colour shown around the page in paged mode (the "desk"). */
private val DeskColor = Color(0xFF9E9E9E)

/** Gap kept between the page and the desk edges when the page is fitted (zoom = 1). */
private val DeskMargin = 16.dp

/** Test tag of the page frame in paged mode. */
const val DrawingPageTestTag = "drawing_page"

@Composable
fun DrawingCanvas(
    paths: List<PathData>,
    currentPath: PathData?,
    backgroundColor: Color,
    onAction: (DrawingAction) -> Unit,
    isSelectionMode: Boolean = false,
    selectedPathId: String? = null,
    dragOffset: Offset = Offset.Zero,
    // ── Milestone 1 params ─────────────────────────────────────────────────
    zoom: Float = 1f,
    panOffset: Offset = Offset.Zero,
    pageWidth: Float = 794f,
    pageHeight: Float = 1123f,
    isLegacy: Boolean = false,
    /** Colour of the desk around the page (paged mode only). */
    deskColor: Color = DeskColor,
    /** Area of the desk covered by floating UI; the fitted page keeps clear of it. */
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier
) {
    if (isLegacy) {
        // ── LEGACY MODE: original full-screen canvas ──────────────────────
        LegacyCanvas(
            paths = paths,
            currentPath = currentPath,
            backgroundColor = backgroundColor,
            onAction = onAction,
            isSelectionMode = isSelectionMode,
            selectedPathId = selectedPathId,
            dragOffset = dragOffset,
            modifier = modifier
        )
        return
    }

    // ── PAGED MODE ────────────────────────────────────────────────────────────
    //
    // KEY DESIGN: local Compose state drives graphicsLayer directly.
    //
    // Why: pointerInput(Unit) never restarts the gesture coroutine mid-pinch.
    //      graphicsLayer reads localZoom/localPan at draw-phase only (no recompose).
    //      LaunchedEffect syncs from ViewModel only on external resets (Fit, page switch).
    //      onAction still notifies ViewModel so zoom/pan survives page switches and saves.
    //
    // Re-initialise local state whenever the page size changes (size change always resets view).
    var localZoom by remember(pageWidth, pageHeight) { mutableFloatStateOf(zoom) }
    var localPan  by remember(pageWidth, pageHeight) { mutableStateOf(panOffset) }

    // External reset (Fit button, page switch, design load, page size change) — sync from ViewModel.
    LaunchedEffect(zoom, panOffset, pageWidth, pageHeight) {
        localZoom = zoom
        localPan  = panOffset
    }

    // Read these inside pointerInput(Unit) without restarting the coroutine.
    val currentIsSelectionMode by rememberUpdatedState(isSelectionMode)
    val currentSelectedPathIdForDesk by rememberUpdatedState(selectedPathId)

    // Desk size in px — read at draw-phase only, to fit the page inside the desk.
    var deskSize by remember { mutableStateOf(IntSize.Zero) }

    // Screen px per page unit: the base scale that fits the whole page inside the desk,
    // times zoom. Only ever read at draw-phase / in gesture callbacks (no recompose).
    val deskMarginPx = with(LocalDensity.current) { DeskMargin.toPx() * 2 }
    val viewScale = {
        val fitScale = minOf(
            (deskSize.width - deskMarginPx) / pageWidth,
            (deskSize.height - deskMarginPx) / pageHeight
        ).takeIf { it > 0f } ?: 1f
        fitScale * localZoom
    }

    Box(
        modifier = modifier
            // The zoomed page may be larger than the desk; keep it off neighbouring UI.
            .clipToBounds()
            .background(deskColor)
            .pointerInput(Unit) {       // Unit key → coroutine NEVER restarts mid-gesture
                detectCanvasTransformGestures(
                    // Decide ownership BEFORE consuming anything, otherwise the desk
                    // steals gestures the page canvas needs. The desk sits outside the
                    // graphicsLayer, so its touch slop is in screen px while the page's
                    // is in page px (screen ÷ zoom) — zoomed in, the desk always wins
                    // the slop race, which is exactly what must not happen for a stroke.
                    canClaim = { pointerCount ->
                        when {
                            // Zoom/pan is a selection-mode affordance today.
                            !currentIsSelectionMode -> false
                            // Two fingers are unambiguously a pinch.
                            pointerCount > 1 -> true
                            // One finger is only ours when the page canvas declined it,
                            // i.e. nothing is selected to drag. Mirrors `canDrag` below.
                            else -> currentSelectedPathIdForDesk == null
                        }
                    }
                ) { pan, zoomChange ->
                    // Update local state immediately — graphicsLayer redraws at draw-phase
                    // with no recomposition overhead.
                    localZoom = (localZoom * zoomChange)
                        .coerceIn(DrawingScreenState.MIN_ZOOM, DrawingScreenState.MAX_ZOOM)
                    localPan += pan
                    // Notify ViewModel so state persists across page-switches and saves.
                    // This does NOT drive graphicsLayer — local state does.
                    onAction(DrawingAction.OnZoomPan(zoomChange, pan))
                }
            }
    ) {
        // The page is fitted to, and centred in, the part of the desk not covered by floating UI.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .onSizeChanged { deskSize = it },
            contentAlignment = Alignment.Center
        ) {
            // One page unit = one layout px, whatever the screen density.
            val (pageWidthDp, pageHeightDp) = with(LocalDensity.current) {
                pageWidth.toDp() to pageHeight.toDp()
            }

            // Page frame with drop shadow — transform driven by LOCAL state (draw-phase only)
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        val scale = viewScale()
                        scaleX       = scale
                        scaleY       = scale
                        translationX = localPan.x
                        translationY = localPan.y
                    }
                    // requiredSize, not size: the desk's constraints are the viewport, and
                    // size() would clamp every page larger than the viewport down to it.
                    .requiredSize(pageWidthDp, pageHeightDp)
                    .testTag(DrawingPageTestTag)
                    .shadow(elevation = 8.dp)
            ) {
                // Inner Canvas = actual drawing surface in page coordinates.
                // Compose maps touch coordinates through graphicsLayer automatically.
                PagedCanvas(
                    paths = paths,
                    currentPath = currentPath,
                    backgroundColor = backgroundColor,
                    onAction = onAction,
                    isSelectionMode = isSelectionMode,
                    selectedPathId = selectedPathId,
                    dragOffset = dragOffset,
                    viewScale = viewScale,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Inner composables
// ─────────────────────────────────────────────────────────────────────────────

/**
 * The original full-screen canvas used for legacy drawings.
 * This is byte-for-byte identical to the pre-refactor DrawingCanvas body.
 */
@Composable
private fun LegacyCanvas(
    paths: List<PathData>,
    currentPath: PathData?,
    backgroundColor: Color,
    onAction: (DrawingAction) -> Unit,
    isSelectionMode: Boolean,
    selectedPathId: String?,
    dragOffset: Offset,
    modifier: Modifier
) {
    val currentPaths by rememberUpdatedState(paths)
    val currentSelectedPathId by rememberUpdatedState(selectedPathId)
    val currentOnAction by rememberUpdatedState(onAction)

    Canvas(
        modifier = modifier
            .clipToBounds()
            .background(backgroundColor)
            .pointerInput(isSelectionMode) {
                detectTapAndDrag(
                    onTap = { offset ->
                        handleTap(offset, isSelectionMode, currentPaths, currentSelectedPathId, currentOnAction)
                    },
                    onDragStart = { offset ->
                        if (!isSelectionMode) {
                            currentOnAction(DrawingAction.OnNewPathStart)
                            // Anchor the stroke at the touch point itself so no
                            // lead-in is lost, and so shapes start where you
                            // actually pressed rather than where slop was crossed.
                            currentOnAction(DrawingAction.OnDraw(offset))
                        }
                    },
                    onDrag = { change, dragAmount ->
                        handleDrag(change, dragAmount, isSelectionMode, currentSelectedPathId, currentOnAction)
                    },
                    onDragEnd = { currentOnAction(DrawingAction.OnPathEnd) },
                    onDragCancel = { currentOnAction(DrawingAction.OnPathEnd) },
                    canDrag = { !isSelectionMode || currentSelectedPathId != null },
                    // Drawing inks immediately; only selection needs slop to tell
                    // a tap (select) apart from a drag (move).
                    requireSlop = { isSelectionMode }
                )
            }
    ) {
        drawPaths(paths, currentPath, backgroundColor, isSelectionMode, selectedPathId, dragOffset)
    }
}

/**
 * The new page-sized drawing canvas.
 * Compose maps touch coordinates to this composable's local space automatically,
 * even after the graphicsLayer transform on the parent — no manual math needed.
 */
@Composable
private fun PagedCanvas(
    paths: List<PathData>,
    currentPath: PathData?,
    backgroundColor: Color,
    onAction: (DrawingAction) -> Unit,
    isSelectionMode: Boolean,
    selectedPathId: String?,
    dragOffset: Offset,
    /** Screen px per page unit; keeps selection handles a constant size on screen. */
    viewScale: () -> Float,
    modifier: Modifier
) {
    val currentPaths by rememberUpdatedState(paths)
    val currentSelectedPathId by rememberUpdatedState(selectedPathId)
    val currentOnAction by rememberUpdatedState(onAction)
    val currentViewScale by rememberUpdatedState(viewScale)

    Canvas(
        modifier = modifier
            .clipToBounds()
            .background(backgroundColor)
            .pointerInput(isSelectionMode) {
                detectTapAndDrag(
                    onTap = { offset ->
                        handleTap(
                            offset, isSelectionMode, currentPaths, currentSelectedPathId, currentOnAction,
                            decorationScale = density / currentViewScale()
                        )
                    },
                    onDragStart = { offset ->
                        if (!isSelectionMode) {
                            currentOnAction(DrawingAction.OnNewPathStart)
                            // Anchor the stroke at the touch point itself so no
                            // lead-in is lost, and so shapes start where you
                            // actually pressed rather than where slop was crossed.
                            currentOnAction(DrawingAction.OnDraw(offset))
                        }
                    },
                    onDrag = { change, dragAmount ->
                        handleDrag(change, dragAmount, isSelectionMode, currentSelectedPathId, currentOnAction)
                    },
                    onDragEnd = { currentOnAction(DrawingAction.OnPathEnd) },
                    onDragCancel = { currentOnAction(DrawingAction.OnPathEnd) },
                    // In selection mode with nothing selected a drag is a no-op,
                    // so don't claim the gesture — it belongs to pinch zoom/pan.
                    canDrag = { !isSelectionMode || currentSelectedPathId != null },
                    // Drawing inks immediately; only selection needs slop to tell
                    // a tap (select) apart from a drag (move).
                    requireSlop = { isSelectionMode }
                )
            }
    ) {
        drawPaths(
            paths, currentPath, backgroundColor, isSelectionMode, selectedPathId, dragOffset,
            decorationScale = density / viewScale()
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared gesture handlers
// ─────────────────────────────────────────────────────────────────────────────

private fun handleTap(
    offset: Offset,
    isSelectionMode: Boolean,
    paths: List<PathData>,
    selectedPathId: String?,
    onAction: (DrawingAction) -> Unit,
    /** Canvas units per on-screen dp for the selection handles; 1 = legacy raw px. */
    decorationScale: Float = 1f
) {
    if (!isSelectionMode) return
    val selectedPath = paths.firstOrNull { it.id == selectedPathId }
    if (selectedPath != null) {
        val box = selectedPath.getBoundingBox()
        if (box != null) {
            val deleteButtonCenter = Offset(box.right, box.top)
            if ((offset - deleteButtonCenter).getDistance() <= 35f * decorationScale) {
                onAction(DrawingAction.OnDeleteSelectedPath)
                return
            }
        }
    }
    val hitPath = paths.lastOrNull { it.isHit(offset) }
    onAction(DrawingAction.OnSelectPath(hitPath?.id))
}

private fun handleDrag(
    change: PointerInputChange,
    dragAmount: Offset,
    isSelectionMode: Boolean,
    selectedPathId: String?,
    onAction: (DrawingAction) -> Unit
) {
    if (isSelectionMode) {
        if (selectedPathId != null) onAction(DrawingAction.OnDragSelectedPath(dragAmount))
    } else {
        // The platform batches several touch samples into one frame's event.
        // Using only `position` throws the rest away, which is what makes fast
        // strokes render as a chain of straight segments instead of a curve.
        change.historical.fastForEach { onAction(DrawingAction.OnDraw(it.position)) }
        onAction(DrawingAction.OnDraw(change.position))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DrawScope extension: render all paths + selection decorations
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawPaths(
    paths: List<PathData>,
    currentPath: PathData?,
    backgroundColor: Color,
    isSelectionMode: Boolean,
    selectedPathId: String?,
    dragOffset: Offset,
    /** Canvas units per on-screen dp for the selection handles; 1 = legacy raw px. */
    decorationScale: Float = 1f
) {
    paths.fastForEach { pathData ->
        val isSelected = isSelectionMode && pathData.id == selectedPathId
        if (isSelected && dragOffset != Offset.Zero) {
            drawContext.canvas.save()
            drawContext.canvas.translate(dragOffset.x, dragOffset.y)
            drawPath(
                pathData.path, pathData.color, pathData.thickness,
                pathData.pathEffect, pathData.isEraser, backgroundColor,
                pathData.shapeType, pathData.shapePoints, pathData.renderPath()
            )
            drawContext.canvas.restore()
        } else {
            drawPath(
                pathData.path, pathData.color, pathData.thickness,
                pathData.pathEffect, pathData.isEraser, backgroundColor,
                pathData.shapeType, pathData.shapePoints, pathData.renderPath()
            )
        }
    }

    // The in-progress stroke is a new PathData per sample, so its cache is
    // always cold — building it once per frame here is the unavoidable cost.
    currentPath?.let {
        drawPath(
            it.path, it.color, it.thickness,
            it.pathEffect, it.isEraser, backgroundColor,
            it.shapeType, it.shapePoints, it.renderPath()
        )
    }

    // Selection decorations
    if (isSelectionMode && selectedPathId != null) {
        val selectedPath = paths.firstOrNull { it.id == selectedPathId }
        if (selectedPath != null) {
            var box = selectedPath.getBoundingBox()
            if (box != null) {
                if (dragOffset != Offset.Zero) box = box.translate(dragOffset)
                drawRect(
                    color = Color(0xFF2196F3),
                    topLeft = box.topLeft,
                    size = box.size,
                    style = Stroke(
                        width = 2f * decorationScale,
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(10f * decorationScale, 10f * decorationScale), 0f
                        )
                    )
                )
                val deleteButtonCenter = Offset(box.right, box.top)
                drawCircle(color = Color.Red, radius = 14f * decorationScale, center = deleteButtonCenter)
                val crossSize = 6f * decorationScale
                drawLine(
                    Color.White,
                    deleteButtonCenter - Offset(crossSize, crossSize),
                    deleteButtonCenter + Offset(crossSize, crossSize),
                    strokeWidth = 3f * decorationScale
                )
                drawLine(
                    Color.White,
                    deleteButtonCenter - Offset(crossSize, -crossSize),
                    deleteButtonCenter + Offset(crossSize, -crossSize),
                    strokeWidth = 3f * decorationScale
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Bounding box & hit detection (unchanged)
// ─────────────────────────────────────────────────────────────────────────────

fun PathData.getBoundingBox(): Rect? {
    if (shapeType == ShapeType.NONE) {
        if (path.isEmpty()) return null
        var minX = Float.MAX_VALUE; var maxX = Float.MIN_VALUE
        var minY = Float.MAX_VALUE; var maxY = Float.MIN_VALUE
        path.forEach {
            if (it.x < minX) minX = it.x; if (it.x > maxX) maxX = it.x
            if (it.y < minY) minY = it.y; if (it.y > maxY) maxY = it.y
        }
        val halfThickness = maxOf(thickness, 10f) / 2f
        return Rect(minX - halfThickness, minY - halfThickness, maxX + halfThickness, maxY + halfThickness)
    } else {
        if (shapePoints.size < 2) return null
        val start = shapePoints[0]; val end = shapePoints[1]
        val halfThickness = maxOf(thickness, 10f) / 2f
        return when (shapeType) {
            ShapeType.LINE -> Rect(
                minOf(start.x, end.x) - halfThickness, minOf(start.y, end.y) - halfThickness,
                maxOf(start.x, end.x) + halfThickness, maxOf(start.y, end.y) + halfThickness
            )
            ShapeType.RECTANGLE, ShapeType.SQUARE -> {
                if (shapeType == ShapeType.SQUARE) {
                    val size = maxOf(abs(end.x - start.x), abs(end.y - start.y))
                    val endX = if (end.x >= start.x) start.x + size else start.x - size
                    val endY = if (end.y >= start.y) start.y + size else start.y - size
                    Rect(minOf(start.x, endX) - halfThickness, minOf(start.y, endY) - halfThickness,
                        maxOf(start.x, endX) + halfThickness, maxOf(start.y, endY) + halfThickness)
                } else {
                    Rect(minOf(start.x, end.x) - halfThickness, minOf(start.y, end.y) - halfThickness,
                        maxOf(start.x, end.x) + halfThickness, maxOf(start.y, end.y) + halfThickness)
                }
            }
            ShapeType.CIRCLE -> {
                val radius = sqrt((end.x - start.x).pow(2) + (end.y - start.y).pow(2))
                Rect(start.x - radius - halfThickness, start.y - radius - halfThickness,
                    start.x + radius + halfThickness, start.y + radius + halfThickness)
            }
            ShapeType.OVAL -> {
                val w = abs(end.x - start.x) * 2; val h = abs(end.y - start.y) * 2
                Rect(start.x - w / 2 - halfThickness, start.y - h / 2 - halfThickness,
                    start.x + w / 2 + halfThickness, start.y + h / 2 + halfThickness)
            }
            ShapeType.TRIANGLE -> Rect(
                minOf(start.x, end.x) - halfThickness, minOf(start.y, end.y) - halfThickness,
                maxOf(start.x, end.x) + halfThickness, maxOf(start.y, end.y) + halfThickness
            )
            ShapeType.ARROW_RIGHT, ShapeType.ARROW_LEFT, ShapeType.ARROW_UP, ShapeType.ARROW_DOWN -> Rect(
                minOf(start.x, end.x) - halfThickness - 20f, minOf(start.y, end.y) - halfThickness - 20f,
                maxOf(start.x, end.x) + halfThickness + 20f, maxOf(start.y, end.y) + halfThickness + 20f
            )
            ShapeType.STAR, ShapeType.PENTAGON, ShapeType.HEXAGON -> {
                val radius = sqrt((end.x - start.x).pow(2) + (end.y - start.y).pow(2))
                Rect(start.x - radius - halfThickness, start.y - radius - halfThickness,
                    start.x + radius + halfThickness, start.y + radius + halfThickness)
            }
        }
    }
}

fun distanceToSegment(p: Offset, a: Offset, b: Offset): Float {
    val ab = b - a; val ap = p - a
    val abLenSq = ab.x * ab.x + ab.y * ab.y
    if (abLenSq == 0f) return (p - a).getDistance()
    val t = ((ap.x * ab.x + ap.y * ab.y) / abLenSq).coerceIn(0f, 1f)
    return (p - (a + ab * t)).getDistance()
}

fun distanceToPath(p: Offset, path: List<Offset>): Float {
    if (path.isEmpty()) return Float.MAX_VALUE
    if (path.size == 1) return (p - path[0]).getDistance()
    var minDistance = Float.MAX_VALUE
    for (i in 0 until path.size - 1) {
        val d = distanceToSegment(p, path[i], path[i + 1])
        if (d < minDistance) minDistance = d
    }
    return minDistance
}

fun PathData.isHit(offset: Offset): Boolean {
    val threshold = maxOf(thickness, 10f) / 2f + 30f
    if (shapeType == ShapeType.NONE) {
        if (path.isEmpty()) return false
        return distanceToPath(offset, path) <= threshold
    } else {
        if (shapePoints.size < 2) return false
        val start = shapePoints[0]; val end = shapePoints[1]
        return when (shapeType) {
            ShapeType.LINE -> distanceToSegment(offset, start, end) <= threshold
            ShapeType.RECTANGLE, ShapeType.SQUARE, ShapeType.TRIANGLE ->
                getBoundingBox()?.contains(offset) ?: false
            ShapeType.CIRCLE -> {
                val radius = sqrt((end.x - start.x).pow(2) + (end.y - start.y).pow(2))
                (offset - start).getDistance() <= radius + threshold
            }
            ShapeType.OVAL, ShapeType.STAR, ShapeType.PENTAGON, ShapeType.HEXAGON ->
                getBoundingBox()?.contains(offset) ?: false
            else -> false
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DrawScope path/shape rendering (unchanged from original)
// ─────────────────────────────────────────────────────────────────────────────

fun DrawScope.drawPath(
    path: List<Offset>,
    color: Color,
    thickness: Float = 10f,
    pathEffect: PaintingStyleType,
    isEraser: Boolean = false,
    backgroundColor: Color = Color.White,
    shapeType: ShapeType = ShapeType.NONE,
    shapePoints: List<Offset> = emptyList(),
    /** Pre-built geometry for [path]; pass [PathData.renderPath] to skip rebuilding per frame. */
    prebuiltPath: Path? = null
) {
    if (shapeType != ShapeType.NONE && shapePoints.size >= 2) {
        drawShape(shapePoints, color, thickness, pathEffect, shapeType)
        return
    }
    val smoothPath = prebuiltPath ?: buildSmoothPath(path)
    if (isEraser) {
        drawPath(
            path = smoothPath, color = backgroundColor,
            style = Stroke(width = thickness, cap = StrokeCap.Round, join = StrokeJoin.Round),
            blendMode = BlendMode.Src
        )
    } else {
        when (pathEffect) {
            PaintingStyleType.PENCIL -> drawPath(
                path = smoothPath, color = color.copy(alpha = 0.15f),
                style = Stroke(width = thickness * 2f, cap = StrokeCap.Round)
            )
            else -> {}
        }
        drawPath(path = smoothPath, color = color, style = getDrawStyle(thickness, pathEffect))
    }
}

fun getDrawStyle(thickness: Float, style: PaintingStyleType): DrawStyle {
    return when (style) {
        PaintingStyleType.DOT -> Stroke(
            width = thickness, cap = StrokeCap.Round, join = StrokeJoin.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(thickness, thickness * 2.5f), 0f)
        )
        PaintingStyleType.STROKE -> Stroke(width = thickness, cap = StrokeCap.Round, join = StrokeJoin.Round)
        PaintingStyleType.FILL -> Fill
        PaintingStyleType.SCALLOP -> Stroke(width = thickness, pathEffect = scallopEffect(thickness))
        PaintingStyleType.PENCIL -> Stroke(width = thickness * 0.6f, cap = StrokeCap.Round)
    }
}

fun scallopEffect(thickness: Float): PathEffect {
    val wavePath = Path().apply {
        moveTo(0f, 0f)
        quadraticTo(thickness, thickness, thickness * 2, 0f)
    }
    return PathEffect.stampedPathEffect(
        shape = wavePath, advance = thickness * 2, phase = 0f,
        style = StampedPathEffectStyle.Rotate
    )
}

fun DrawScope.drawShape(
    points: List<Offset>,
    color: Color,
    thickness: Float,
    pathEffect: PaintingStyleType,
    shapeType: ShapeType
) {
    if (points.size < 2) return
    val start = points[0]; val end = points[1]
    val style = getDrawStyle(thickness, pathEffect)
    val shapePath = Path()
    when (shapeType) {
        ShapeType.LINE -> drawLine(color, start, end, thickness, StrokeCap.Round)
        ShapeType.RECTANGLE -> {
            shapePath.apply { moveTo(start.x, start.y); lineTo(end.x, start.y); lineTo(end.x, end.y); lineTo(start.x, end.y); close() }
            drawPath(shapePath, color, style = style)
        }
        ShapeType.SQUARE -> {
            val size = maxOf(abs(end.x - start.x), abs(end.y - start.y))
            val endX = if (end.x >= start.x) start.x + size else start.x - size
            val endY = if (end.y >= start.y) start.y + size else start.y - size
            shapePath.apply { moveTo(start.x, start.y); lineTo(endX, start.y); lineTo(endX, endY); lineTo(start.x, endY); close() }
            drawPath(shapePath, color, style = style)
        }
        ShapeType.CIRCLE -> {
            val radius = sqrt((end.x - start.x).pow(2) + (end.y - start.y).pow(2))
            drawCircle(color, radius, start, style = style)
        }
        ShapeType.OVAL -> {
            val w = abs(end.x - start.x) * 2; val h = abs(end.y - start.y) * 2
            drawOval(color, Offset(start.x - w / 2, start.y - h / 2), androidx.compose.ui.geometry.Size(w, h), style = style)
        }
        ShapeType.TRIANGLE -> {
            shapePath.apply { moveTo(start.x, end.y); lineTo((start.x + end.x) / 2, start.y); lineTo(end.x, end.y); close() }
            drawPath(shapePath, color, style = style)
        }
        ShapeType.ARROW_RIGHT -> {
            val ah = abs(end.y - start.y) / 3
            drawLine(color, start, Offset(end.x - ah, start.y), thickness, StrokeCap.Round)
            shapePath.apply { moveTo(end.x, start.y); lineTo(end.x - ah, start.y - ah); lineTo(end.x - ah, start.y + ah); close() }
            drawPath(shapePath, color, style = style)
        }
        ShapeType.ARROW_LEFT -> {
            val ah = abs(end.y - start.y) / 3
            drawLine(color, start, Offset(end.x + ah, start.y), thickness, StrokeCap.Round)
            shapePath.apply { moveTo(end.x, start.y); lineTo(end.x + ah, start.y - ah); lineTo(end.x + ah, start.y + ah); close() }
            drawPath(shapePath, color, style = style)
        }
        ShapeType.ARROW_UP -> {
            val aw = abs(end.x - start.x) / 3
            drawLine(color, start, Offset(start.x, end.y + aw), thickness, StrokeCap.Round)
            shapePath.apply { moveTo(start.x, end.y); lineTo(start.x - aw, end.y + aw); lineTo(start.x + aw, end.y + aw); close() }
            drawPath(shapePath, color, style = style)
        }
        ShapeType.ARROW_DOWN -> {
            val aw = abs(end.x - start.x) / 3
            drawLine(color, start, Offset(start.x, end.y - aw), thickness, StrokeCap.Round)
            shapePath.apply { moveTo(start.x, end.y); lineTo(start.x - aw, end.y - aw); lineTo(start.x + aw, end.y - aw); close() }
            drawPath(shapePath, color, style = style)
        }
        ShapeType.STAR -> {
            val radius = sqrt((end.x - start.x).pow(2) + (end.y - start.y).pow(2))
            val innerRadius = radius * 0.4f
            shapePath.apply {
                for (i in 0 until 10) {
                    val angle = PI / 2 + i * PI / 5
                    val r = if (i % 2 == 0) radius else innerRadius
                    val x = start.x + (r * cos(angle)).toFloat()
                    val y = start.y - (r * sin(angle)).toFloat()
                    if (i == 0) moveTo(x, y) else lineTo(x, y)
                }
                close()
            }
            drawPath(shapePath, color, style = style)
        }
        ShapeType.PENTAGON -> {
            val radius = sqrt((end.x - start.x).pow(2) + (end.y - start.y).pow(2))
            shapePath.apply {
                for (i in 0 until 5) {
                    val angle = PI / 2 + i * 2 * PI / 5
                    val x = start.x + (radius * cos(angle)).toFloat()
                    val y = start.y - (radius * sin(angle)).toFloat()
                    if (i == 0) moveTo(x, y) else lineTo(x, y)
                }
                close()
            }
            drawPath(shapePath, color, style = style)
        }
        ShapeType.HEXAGON -> {
            val radius = sqrt((end.x - start.x).pow(2) + (end.y - start.y).pow(2))
            shapePath.apply {
                for (i in 0 until 6) {
                    val angle = i * 2 * PI / 6
                    val x = start.x + (radius * cos(angle)).toFloat()
                    val y = start.y + (radius * sin(angle)).toFloat()
                    if (i == 0) moveTo(x, y) else lineTo(x, y)
                }
                close()
            }
            drawPath(shapePath, color, style = style)
        }
        else -> { /* NONE — handled above */ }
    }
}
