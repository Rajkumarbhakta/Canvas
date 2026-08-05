package com.rkbapps.canvas.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "paths",
    foreignKeys = [
        ForeignKey(
            entity = DesignEntity::class,
            parentColumns = ["id"],
            childColumns = ["designId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["designId"])]
)
data class PathEntity(
    @PrimaryKey(autoGenerate = true)
    val pathInternalId: Long = 0L,
    val designId: Long,
    val strokeId: String,
    val color: Int,
    val thickness: Float,
    val pathEffect: String,
    val isEraser: Boolean,
    val shapeType: String,
    val pathPoints: ByteArray,   // Binary stored offsets (page-local coordinates)
    val shapePoints: ByteArray,  // Binary stored offsets for shapes
    val orderIndex: Int,         // Maintains drawing order within a page

    // ── Milestone 2: multi-page ───────────────────────────────────────────────
    /** Zero-based index of the page this stroke belongs to.
     *  Migration4To5 defaults this to 0 so existing strokes go on page 0. */
    val pageIndex: Int = 0,
)
