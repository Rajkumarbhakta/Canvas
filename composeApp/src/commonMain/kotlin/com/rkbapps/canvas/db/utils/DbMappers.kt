package com.rkbapps.canvas.db.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.rkbapps.canvas.db.entities.DesignEntity
import com.rkbapps.canvas.db.entities.DesignWithPaths
import com.rkbapps.canvas.db.entities.PathEntity
import com.rkbapps.canvas.model.CanvasPage
import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.model.PathData
import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.ui.screens.drawing.utils.PaintingStyleType
import com.rkbapps.canvas.ui.screens.drawing.utils.ShapeType

// ── Domain → Entity ──────────────────────────────────────────────────────────

fun SavedDesign.toEntity(): DesignEntity {
    return DesignEntity(
        id = pId,
        stringId = id,
        name = name,
        time = time,
        backgroundColor = state.backgroundColor.toArgb(),
        selectedColor = state.selectedColor.toArgb(),
        selectedThickness = state.selectedThickness,
        selectedPathEffect = state.selectedPathEffect.name,
        selectedShapeType = state.selectedShapeType.name,
        isEraserMode = false,
        // ── Milestone 1 ────────────────────────────────────────────────────────
        pageWidth = state.pageWidth,
        pageHeight = state.pageHeight,
        pageSizeLabel = state.pageSizeLabel,
        isLegacy = isLegacy,
        // ── Milestone 2 ────────────────────────────────────────────────────────
        // Explicitly store page count so blank trailing pages survive reload.
        // For legacy drawings there are no pages; use 1 as the sentinel value.
        pageCount = if (isLegacy) 1 else state.pages.size.coerceAtLeast(1),
    )
}

/**
 * Converts a [PathData] to a [PathEntity].
 *
 * @param designId  The DB primary key of the owning design.
 * @param index     Stroke order within the page.
 * @param pageIndex The zero-based page index this stroke belongs to (default 0).
 */
fun PathData.toEntity(designId: Long, index: Int, pageIndex: Int = 0): PathEntity {
    return PathEntity(
        designId = designId,
        strokeId = id,
        color = color.toArgb(),
        thickness = thickness,
        pathEffect = pathEffect.name,
        isEraser = isEraser,
        shapeType = shapeType.name,
        pathPoints = path.toBinary(),
        shapePoints = shapePoints.toBinary(),
        orderIndex = index,
        pageIndex = pageIndex,
    )
}

// ── Entity → Domain ──────────────────────────────────────────────────────────

fun DesignWithPaths.toDomain(): SavedDesign {
    val isLegacy = design.isLegacy

    // Group paths by pageIndex
    val pathsByPage: Map<Int, List<PathEntity>> =
        paths.groupBy { it.pageIndex }

    val pages: List<CanvasPage> = if (isLegacy) {
        // Legacy drawings: flat list on a single page; page model is not used
        // (the canvas renders legacy drawings directly from DrawingState.paths)
        emptyList()
    } else {
        // Use the stored pageCount so blank trailing pages are preserved.
        // Previously this used maxOfOrNull { it.pageIndex } which dropped pages
        // that had no strokes (their index never appeared in the paths table).
        val targetCount = design.pageCount.coerceAtLeast(1)
        (0 until targetCount).map { pageIdx ->
            CanvasPage(
                id = "page_${design.id}_$pageIdx",
                backgroundColor = Color(design.backgroundColor),
                paths = (pathsByPage[pageIdx] ?: emptyList())
                    .sortedBy { it.orderIndex }
                    .map { it.toDomain() },
            )
        }
    }

    return SavedDesign(
        pId = design.id,
        id = design.stringId,
        name = design.name,
        time = design.time,
        isLegacy = isLegacy,
        state = DrawingState(
            // ── Page geometry ─────────────────────────────────────────────
            pageWidth = design.pageWidth,
            pageHeight = design.pageHeight,
            pageSizeLabel = design.pageSizeLabel,
            // ── Tool state ────────────────────────────────────────────────
            selectedColor = Color(design.selectedColor),
            selectedThickness = design.selectedThickness,
            selectedPathEffect = PaintingStyleType.valueOf(design.selectedPathEffect),
            selectedShapeType = ShapeType.valueOf(design.selectedShapeType),
            backgroundColor = Color(design.backgroundColor),
            // ── Pages / legacy paths ──────────────────────────────────────
            pages = pages,
            paths = if (isLegacy) {
                // Legacy: load all paths into the flat list (original behaviour)
                paths.sortedBy { it.orderIndex }.map { it.toDomain() }
            } else {
                emptyList()
            },
        ),
    )
}

fun PathEntity.toDomain(): PathData {
    return PathData(
        id = strokeId,
        color = Color(color),
        thickness = thickness,
        pathEffect = PaintingStyleType.valueOf(pathEffect),
        isEraser = isEraser,
        shapeType = ShapeType.valueOf(shapeType),
        path = pathPoints.toOffsets(),
        shapePoints = shapePoints.toOffsets()
    )
}

// ── Binary conversion helpers ────────────────────────────────────────────────

fun List<Offset>.toBinary(): ByteArray {
    val bytes = ByteArray(size * 8)
    forEachIndexed { i, offset ->
        val xBits = offset.x.toRawBits()
        val yBits = offset.y.toRawBits()
        bytes[i * 8 + 0] = (xBits shr 24).toByte()
        bytes[i * 8 + 1] = (xBits shr 16).toByte()
        bytes[i * 8 + 2] = (xBits shr 8).toByte()
        bytes[i * 8 + 3] = (xBits shr 0).toByte()
        bytes[i * 8 + 4] = (yBits shr 24).toByte()
        bytes[i * 8 + 5] = (yBits shr 16).toByte()
        bytes[i * 8 + 6] = (yBits shr 8).toByte()
        bytes[i * 8 + 7] = (yBits shr 0).toByte()
    }
    return bytes
}

fun ByteArray.toOffsets(): List<Offset> {
    val size = size / 8
    return List(size) { i ->
        val xBits = (this[i * 8 + 0].toInt() and 0xFF shl 24) or
                    (this[i * 8 + 1].toInt() and 0xFF shl 16) or
                    (this[i * 8 + 2].toInt() and 0xFF shl 8) or
                    (this[i * 8 + 3].toInt() and 0xFF)
        val yBits = (this[i * 8 + 4].toInt() and 0xFF shl 24) or
                    (this[i * 8 + 5].toInt() and 0xFF shl 16) or
                    (this[i * 8 + 6].toInt() and 0xFF shl 8) or
                    (this[i * 8 + 7].toInt() and 0xFF)
        Offset(Float.fromBits(xBits), Float.fromBits(yBits))
    }
}
