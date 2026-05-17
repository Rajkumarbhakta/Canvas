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
    val isEraserMode: Boolean
)
