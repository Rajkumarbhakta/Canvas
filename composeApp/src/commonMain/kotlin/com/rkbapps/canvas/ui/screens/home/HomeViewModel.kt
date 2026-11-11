package com.rkbapps.canvas.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkbapps.canvas.db.migrator.DataMigrationManager
import com.rkbapps.canvas.model.PathData
import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.model.SavedDesigns
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: HomeRepository,
    private val dataMigrationManager: DataMigrationManager
): ViewModel() {
    val allDesign = repository.allDesign.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), SavedDesigns())
    val migrationState = dataMigrationManager.migrationState

    val selectedProjectForDelete = repository.selectedProjectForDelete

    fun selectDesignForDelete(design: SavedDesign?) = repository.selectDesignForDelete(design)


    init {
        viewModelScope.launch {
            dataMigrationManager.migrateIfRequired()
        }
    }
    // ── Thumbnails ────────────────────────────────────────────────────────────
    // Last strokes loaded per drawing. Shown at once when a card scrolls back into
    // view, while [loadPreview] re-reads the database so an edited drawing is never
    // left with a stale thumbnail. Only touched from the main thread.
    private val previews = mutableMapOf<Long, List<PathData>>()

    fun cachedPreview(designId: Long): List<PathData>? = previews[designId]

    suspend fun loadPreview(designId: Long): List<PathData> =
        repository.loadFirstPage(designId).also { previews[designId] = it }

    fun deleteDesign(id: Long){
        viewModelScope.launch {
            repository.deleteDesign(id)
        }
    }

}