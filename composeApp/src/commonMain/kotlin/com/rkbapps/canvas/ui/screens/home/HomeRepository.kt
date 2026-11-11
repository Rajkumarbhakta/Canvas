package com.rkbapps.canvas.ui.screens.home

import androidx.compose.ui.graphics.Color
import com.rkbapps.canvas.db.dao.DrawingDao
import com.rkbapps.canvas.db.utils.toDomain
import com.rkbapps.canvas.db.utils.toEntity
import com.rkbapps.canvas.db.utils.toPathEntities
import com.rkbapps.canvas.model.CanvasFileError
import com.rkbapps.canvas.model.CanvasFileException
import com.rkbapps.canvas.model.CanvasPage
import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.model.PathData
import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.model.SavedDesigns
import com.rkbapps.canvas.ui.screens.drawing.utils.PaintingStyleType
import com.rkbapps.canvas.ui.screens.drawing.utils.ShapeType
import com.rkbapps.canvas.util.IncomingFiles
import com.rkbapps.canvas.util.readCanvasFile
import com.rkbapps.canvas.util.toCanvasFile
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class HomeRepository (
    private val drawingDao: DrawingDao,
    incomingFiles: IncomingFiles
) {
    private val _selectedProjectForDelete = MutableStateFlow<SavedDesign?>(null)
    val selectedProjectForDelete = _selectedProjectForDelete.asStateFlow()

    fun selectDesignForDelete(design: SavedDesign?){
        _selectedProjectForDelete.value = design
    }


    val allDesign: Flow<SavedDesigns> = drawingDao.getAllDesigns().map { entities ->
        val allDesigns = entities.map { entity ->
            SavedDesign(
                pId = entity.id,
                id = entity.stringId,
                name = entity.name,
                time = entity.time,
                isLegacy = entity.isLegacy,
                state = DrawingState(
                    pageWidth = entity.pageWidth,
                    pageHeight = entity.pageHeight,
                    pageSizeLabel = entity.pageSizeLabel,
                    // Empty placeholders: the list only needs to know how many pages there are.
                    pages = List(entity.pageCount.coerceAtLeast(1)) { index ->
                        CanvasPage(
                            id = "page_${entity.id}_$index",
                            backgroundColor = Color(entity.backgroundColor)
                        )
                    },
                    backgroundColor = Color(entity.backgroundColor),
                    // These are defaults or from entity if needed for preview
                    selectedColor = Color(entity.selectedColor),
                    selectedThickness = entity.selectedThickness,
                    selectedPathEffect = PaintingStyleType.valueOf(entity.selectedPathEffect),
                    selectedShapeType = ShapeType.valueOf(entity.selectedShapeType),
                    paths = emptyList() // We don't load heavy paths for the list
                )
            )
        }
        SavedDesigns(allDesigns)
    }

    /** The first page's strokes of one drawing, for its thumbnail. Loaded on demand per card. */
    suspend fun loadFirstPage(designId: Long): List<PathData> = withContext(Dispatchers.Default) {
        drawingDao.getFirstPagePaths(designId).map { it.toDomain() }
    }

    // ── Canvas files ──────────────────────────────────────────────────────────

    /** Files opened from outside the app (file manager, "Open with"); imported like picked ones. */
    val incomingFiles: Flow<ByteArray?> = incomingFiles.files

    /**
     * A saved drawing packed as a Canvas file, or null when it cannot be exported
     * (legacy drawings, or a drawing that is no longer in the database).
     */
    suspend fun buildCanvasFile(stringId: String): ByteArray? = withContext(Dispatchers.Default) {
        try {
            drawingDao.getDesignWithPathsByStringId(stringId)
                ?.toDomain()
                ?.takeIf { !it.isLegacy }
                ?.toCanvasFile()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Adds the drawing in a Canvas file to the gallery as a new drawing; nothing
     * already saved is replaced.
     *
     * @param bytes The file's content, or null when the file could not be read.
     */
    suspend fun importDesign(bytes: ByteArray?): HomeMessage = withContext(Dispatchers.Default) {
        if (bytes == null) return@withContext HomeMessage.ImportFailed(CanvasFileError.NOT_A_CANVAS_FILE)
        try {
            val design = readCanvasFile(bytes)
            val dbId = drawingDao.upsertDesignWithPaths(design.toEntity(), design.toPathEntities())
            HomeMessage.Imported(design.copy(pId = dbId))
        } catch (e: CancellationException) {
            throw e
        } catch (e: CanvasFileException) {
            HomeMessage.ImportFailed(e.error)
        } catch (e: Exception) {
            e.printStackTrace()
            HomeMessage.ImportFailed(CanvasFileError.DAMAGED)
        }
    }

    suspend fun deleteDesign(id: Long){
        drawingDao.deleteDesignById(id)
        _selectedProjectForDelete.value = null
    }


}