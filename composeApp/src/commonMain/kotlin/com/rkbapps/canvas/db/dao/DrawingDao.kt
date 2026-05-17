package com.rkbapps.canvas.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.rkbapps.canvas.db.entities.DesignEntity
import com.rkbapps.canvas.db.entities.DesignWithPaths
import com.rkbapps.canvas.db.entities.PathEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DrawingDao {
    @Query("SELECT * FROM designs ORDER BY time DESC")
    fun getAllDesigns(): Flow<List<DesignEntity>>

    @Transaction
    @Query("SELECT * FROM designs WHERE stringId = :stringId")
    suspend fun getDesignWithPathsByStringId(stringId: String): DesignWithPaths?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertDesign(design: DesignEntity): Long

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertPaths(paths: List<PathEntity>)

    @Query("DELETE FROM paths WHERE designId = :designId")
    suspend fun deletePathsByDesignId(designId: Long)

    @Transaction
    suspend fun upsertDesignWithPaths(design: DesignEntity, paths: List<PathEntity>): Long {
        val designId = insertDesign(design)
        deletePathsByDesignId(designId)
        val pathsWithActualId = paths.map { it.copy(designId = designId) }
        insertPaths(pathsWithActualId)
        return designId
    }

    @Query("DELETE FROM designs WHERE id = :id")
    suspend fun deleteDesignById(id: Long)
}