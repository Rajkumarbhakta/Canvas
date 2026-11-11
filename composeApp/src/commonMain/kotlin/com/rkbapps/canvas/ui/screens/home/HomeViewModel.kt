package com.rkbapps.canvas.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkbapps.canvas.db.migrator.DataMigrationManager
import com.rkbapps.canvas.model.CanvasFileError
import com.rkbapps.canvas.model.PathData
import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.model.SavedDesigns
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Something the home screen tells the user once, in a snackbar. */
sealed interface HomeMessage {
    data class Imported(val design: SavedDesign) : HomeMessage
    data class ImportFailed(val error: CanvasFileError) : HomeMessage
    data object Exported : HomeMessage
    data object ExportFailed : HomeMessage
}

class HomeViewModel(
    private val repository: HomeRepository,
    private val dataMigrationManager: DataMigrationManager
): ViewModel() {
    val allDesign = repository.allDesign.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), SavedDesigns())
    val migrationState = dataMigrationManager.migrationState

    val selectedProjectForDelete = repository.selectedProjectForDelete

    fun selectDesignForDelete(design: SavedDesign?) = repository.selectDesignForDelete(design)

    // Outcomes wait in the channel until the home screen is on screen to show them,
    // e.g. when a file is opened from outside while a drawing is open.
    private val _messages = Channel<HomeMessage>(Channel.BUFFERED)
    val messages = _messages.receiveAsFlow()


    init {
        viewModelScope.launch {
            dataMigrationManager.migrateIfRequired()
        }
        viewModelScope.launch {
            repository.incomingFiles.collect { importFile(it) }
        }
    }

    // ── Canvas files ──────────────────────────────────────────────────────────
    /** [bytes] is the content of a Canvas file, or null when the file could not be read. */
    fun importFile(bytes: ByteArray?) {
        viewModelScope.launch {
            _messages.send(repository.importDesign(bytes))
        }
    }

    suspend fun buildCanvasFile(design: SavedDesign): ByteArray? = repository.buildCanvasFile(design.id)

    fun onFileExported(saved: Boolean) {
        _messages.trySend(if (saved) HomeMessage.Exported else HomeMessage.ExportFailed)
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