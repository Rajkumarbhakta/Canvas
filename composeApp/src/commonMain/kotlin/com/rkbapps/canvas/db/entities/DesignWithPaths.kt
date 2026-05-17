package com.rkbapps.canvas.db.entities

import androidx.room.Embedded
import androidx.room.Relation

data class DesignWithPaths(
    @Embedded val design: DesignEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "designId"
    )
    val paths: List<PathEntity>
)
