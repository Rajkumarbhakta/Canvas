package com.rkbapps.canvas.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.time.Instant

@Entity(
    tableName = "designs",
    indices = [Index(value = ["stringId"], unique = true)]
)
data class DesignEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val stringId: String,
    val name: String,
    val time: Instant,
    val backgroundColor: Int,
    val selectedColor: Int,
    val selectedThickness: Float,
    val selectedPathEffect: String,
    val selectedShapeType: String,
    val isEraserMode: Boolean,

    // ── Milestone 1: page geometry ───────────────────────────────────────────
    /** Width of the drawing canvas in logical dp (A4 portrait = 794). */
    val pageWidth: Float = 794f,
    /** Height of the drawing canvas in logical dp (A4 portrait = 1123). */
    val pageHeight: Float = 1123f,
    /** Human-readable label, e.g. "A4 Portrait". */
    val pageSizeLabel: String = "A4 Portrait",
    /**
     * True for drawings that were created *before* the page-coordinate system
     * was introduced. Their [PathEntity.pathPoints] offsets are raw screen pixels
     * and must be rendered without a page frame (full-screen legacy mode).
     *
     * The Room migration sets this to 1 for all pre-existing rows, so old
     * drawings are never broken.
     */
    val isLegacy: Boolean = false,

    // ── Milestone 2: multi-page count ────────────────────────────────────────
    /**
     * Total number of pages in this drawing. Stored explicitly so that
     * blank trailing pages (no strokes) are preserved across save/reload.
     * Defaults to 1 (single-page drawing).
     */
    val pageCount: Int = 1,
)
