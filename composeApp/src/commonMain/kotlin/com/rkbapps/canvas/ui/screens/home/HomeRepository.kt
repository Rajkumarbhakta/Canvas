package com.rkbapps.canvas.ui.screens.home

import androidx.compose.ui.graphics.Color
import com.rkbapps.canvas.db.dao.DrawingDao
import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.model.SavedDesigns
import com.rkbapps.canvas.ui.screens.drawing.composables.PaintingStyleType
import com.rkbapps.canvas.ui.screens.drawing.composables.ShapeType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HomeRepository (
    private val drawingDao: DrawingDao
) {
    val allDesign: Flow<SavedDesigns> = drawingDao.getAllDesigns().map { entities ->
        SavedDesigns(entities.map { entity ->
            SavedDesign(
                pId = entity.id,
                id = entity.stringId,
                name = entity.name,
                time = entity.time,
                state = DrawingState(
                    backgroundColor = Color(entity.backgroundColor),
                    // These are defaults or from entity if needed for preview
                    selectedColor = Color(entity.selectedColor),
                    selectedThickness = entity.selectedThickness,
                    selectedPathEffect = PaintingStyleType.valueOf(entity.selectedPathEffect),
                    selectedShapeType = ShapeType.valueOf(entity.selectedShapeType),
                    isEraserMode = entity.isEraserMode,
                    paths = emptyList() // We don't load heavy paths for the list
                )
            )
        })
    }

    suspend fun getAllDesign(){
        // Room Flow handles updates automatically, no need to manually trigger collect here 
        // if the ViewModel is already collecting allDesign.
    }

    suspend fun deleteDesign(id: Long){
        drawingDao.deleteDesignById(id)
    }


}