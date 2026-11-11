package com.rkbapps.canvas.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import com.rkbapps.canvas.ui.screens.drawing.utils.PaintingStyleType
import com.rkbapps.canvas.ui.screens.drawing.utils.ShapeType
import com.rkbapps.canvas.util.serializers.SafeOffsetListSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Stable
@Immutable
@Serializable
data class PathData(
    val id: String,
    @Contextual
    val color: Color,
    val thickness: Float = 10f,
    val pathEffect: PaintingStyleType = PaintingStyleType.STROKE,
    @Serializable(with = SafeOffsetListSerializer::class)
    val path: List<Offset> = emptyList(),
    val isEraser: Boolean = false,
    val shapeType: ShapeType = ShapeType.NONE,
    @Serializable(with = SafeOffsetListSerializer::class)
    val shapePoints: List<Offset> = emptyList() // Start and end points for shapes
) {
    /**
     * Memoised render geometry. Building the [Path] is O(points), and every
     * finished stroke used to be rebuilt from scratch on *every* frame — so the
     * cost of a single frame grew with the size of the whole drawing. A
     * [PathData] is immutable, so its geometry can only be built once.
     *
     * Not part of the constructor, so `copy()` correctly starts a fresh cache,
     * and `equals`/`hashCode` still compare by content only.
     */
    @Transient
    private var cachedRenderPath: Path? = null

    /** Smoothed [Path] for [path], built on first use and reused afterwards. */
    fun renderPath(): Path =
        cachedRenderPath ?: buildSmoothPath(path).also { cachedRenderPath = it }
}

/**
 * Builds a smooth stroke through [points] using the midpoint-quadratic method:
 * each sample becomes a Bézier control point and the midpoint between adjacent
 * samples becomes the on-curve anchor. That yields a C1-continuous curve while
 * keeping **every** sample — unlike a distance threshold, which silently drops
 * points and leaves gaps in slow strokes (and in any stroke drawn while zoomed
 * in, where a large finger movement is a small movement in page coordinates).
 */
fun buildSmoothPath(points: List<Offset>): Path = Path().apply {
    if (points.isEmpty()) return@apply

    val first = points.first()
    moveTo(first.x, first.y)

    if (points.size == 1) {
        // Zero-length segment — a round stroke cap renders this as a dot.
        lineTo(first.x, first.y)
        return@apply
    }

    for (i in 1 until points.size) {
        val prev = points[i - 1]
        val cur = points[i]
        quadraticTo(prev.x, prev.y, (prev.x + cur.x) / 2f, (prev.y + cur.y) / 2f)
    }

    val last = points.last()
    lineTo(last.x, last.y)
}
