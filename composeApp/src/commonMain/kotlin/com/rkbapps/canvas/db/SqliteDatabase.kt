package com.rkbapps.canvas.db

import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.model.SavedDesigns
import com.rkbapps.canvas.util.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

expect class SqliteDatabase() {
    suspend fun insertDrawing(drawing: SavedDesign)
    suspend fun getAllDrawings(): List<SavedDesign>
    suspend fun getDrawingById(id: String): SavedDesign?
    suspend fun deleteDrawingById(id: String)
    suspend fun updateDrawing(drawing: SavedDesign)
}

class SqliteDatabaseOperations(private val database: SqliteDatabase) {
    
    suspend fun save(drawing: SavedDesign) = withContext(Dispatchers.Default) {
        database.insertDrawing(drawing)
    }
    
    suspend fun delete(id: String) = withContext(Dispatchers.Default) {
        database.deleteDrawingById(id)
    }
    
    suspend fun delete(design: SavedDesign) = withContext(Dispatchers.Default) {
        database.deleteDrawingById(design.id)
    }
    
    suspend fun getDesign(id: String): SavedDesign? = withContext(Dispatchers.Default) {
        database.getDrawingById(id)
    }
    
    suspend fun getAll(): SavedDesigns = withContext(Dispatchers.Default) {
        val drawings = database.getAllDrawings()
        SavedDesigns(designs = drawings)
    }
    
    fun getAllFlow(): Flow<SavedDesigns> = flow {
        val drawings = database.getAllDrawings()
        emit(SavedDesigns(designs = drawings))
    }
} 