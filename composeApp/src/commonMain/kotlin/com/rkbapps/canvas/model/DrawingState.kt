package com.rkbapps.canvas.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.rkbapps.canvas.ui.screens.drawing.utils.PaintingStyleType
import com.rkbapps.canvas.ui.screens.drawing.utils.ShapeType
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable


@Immutable
@Stable
@Serializable
data class DrawingState(
    // ── Page geometry (Milestone 1) ────────────────────────────────────────
    val pageWidth: Float = 794f,       // A4 portrait logical dp (96 dpi)
    val pageHeight: Float = 1123f,     // A4 portrait logical dp (96 dpi)
    val pageSizeLabel: String = "A4 Portrait",

    // ── Tool state ────────────────────────────────────────────────────────
    @Contextual
    val selectedColor: Color = Color.Blue,
    val selectedThickness: Float = 10f,
    val selectedPathEffect: PaintingStyleType = PaintingStyleType.STROKE,
    val selectedShapeType: ShapeType = ShapeType.NONE,
    val currentPath: PathData? = null,

    // ── Multi-page content (Milestone 2) ──────────────────────────────────
    // New drawings use `pages`. Legacy drawings keep using `paths` directly.
    val pages: List<CanvasPage> = listOf(CanvasPage()),

    // ── Legacy flat path list (kept for backward compatibility) ───────────
    val paths: List<PathData> = emptyList(),
    @Contextual
    val backgroundColor: Color = Color.White,
    @Contextual
    val dragOffset: Offset = Offset.Zero,
    val undoStack: List<List<PathData>> = emptyList(),
    val redoStack: List<List<PathData>> = emptyList()
)
