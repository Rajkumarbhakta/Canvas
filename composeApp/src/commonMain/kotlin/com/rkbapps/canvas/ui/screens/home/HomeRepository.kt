package com.rkbapps.canvas.ui.screens.home

import androidx.compose.ui.graphics.Color
import com.rkbapps.canvas.db.dao.DrawingDao
import com.rkbapps.canvas.db.utils.toDomain
import com.rkbapps.canvas.model.CanvasPage
import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.model.PathData
import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.model.SavedDesigns
import com.rkbapps.canvas.ui.screens.drawing.utils.PaintingStyleType
import com.rkbapps.canvas.ui.screens.drawing.utils.ShapeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class HomeRepository (
    private val drawingDao: DrawingDao
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

    suspend fun deleteDesign(id: Long){
        drawingDao.deleteDesignById(id)
        _selectedProjectForDelete.value = null
    }


}