package com.rkbapps.canvas.db.migrator

import com.rkbapps.canvas.db.AppDatabase
import com.rkbapps.canvas.db.PreferenceManager
import com.rkbapps.canvas.db.old_db.DbManager
import com.rkbapps.canvas.db.old_db.DbOperations
import com.rkbapps.canvas.db.old_db.loadDrawingData
import com.rkbapps.canvas.db.utils.toEntity
import com.rkbapps.canvas.model.SavedDesigns
import com.rkbapps.canvas.util.serializers.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class DataMigrationManager(
    private val dbManager: DbManager,
    private val appDatabase: AppDatabase,
    private val preferenceManager: PreferenceManager
) {
    suspend fun migrateIfRequired() = withContext(Dispatchers.Default) {
        val isMigrated = preferenceManager.getBooleanPreference(
            PreferenceManager.IS_MIGRATED_TO_ROOM,
            false
        ).first()

        if (!isMigrated) {
            try {
                val settings = dbManager.getSettings()
                // On JVM, loadDrawingData reads from a file, on others it might use settings.
                // To be safe, let's use the actual loadDrawingData logic if possible,
                // but since it's expect/actual, it's easier to just replicate it here for migration.

                val legacyData = loadDrawingData(settings)

                if (legacyData != DbOperations.Companion.DEFAULT_VALUE) {
                    val savedDesigns = json.decodeFromString(SavedDesigns.serializer(), legacyData)
                    val dao = appDatabase.drawingDao()
                    savedDesigns.designs.forEach { design ->
                        val designEntity = design.toEntity()
                        val pathEntities = design.state.paths.mapIndexed { index, pathData ->
                            // Use 0L as temporary designId, it will be updated by dao.upsertDesignWithPaths
                            pathData.toEntity(0L, index)
                        }
                        dao.upsertDesignWithPaths(designEntity, pathEntities)
                    }
                }


                preferenceManager.saveBooleanPreference(
                    PreferenceManager.Companion.IS_MIGRATED_TO_ROOM,
                    true
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}