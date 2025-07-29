package com.rkbapps.canvas.db

import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.util.json
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.encodeToString

actual class SqliteDatabase {
    private val storageKey = "canvas_drawings"
    
    actual suspend fun insertDrawing(drawing: SavedDesign) {
        val drawings = getAllDrawingsFromStorage().toMutableList()
        val existingIndex = drawings.indexOfFirst { it.id == drawing.id }
        
        if (existingIndex >= 0) {
            drawings[existingIndex] = drawing
        } else {
            drawings.add(drawing)
        }
        
        saveDrawingsToStorage(drawings)
    }
    
    actual suspend fun getAllDrawings(): List<SavedDesign> {
        return getAllDrawingsFromStorage().sortedByDescending { it.time }
    }
    
    actual suspend fun getDrawingById(id: String): SavedDesign? {
        return getAllDrawingsFromStorage().find { it.id == id }
    }
    
    actual suspend fun deleteDrawingById(id: String) {
        val drawings = getAllDrawingsFromStorage().toMutableList()
        drawings.removeAll { it.id == id }
        saveDrawingsToStorage(drawings)
    }
    
    actual suspend fun updateDrawing(drawing: SavedDesign) {
        insertDrawing(drawing)
    }
    
    private fun getAllDrawingsFromStorage(): List<SavedDesign> {
        return try {
            val storedData = localStorage.getItem(storageKey)
            if (storedData != null) {
                json.decodeFromString<List<SavedDesign>>(storedData)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private fun saveDrawingsToStorage(drawings: List<SavedDesign>) {
        try {
            val jsonData = json.encodeToString(drawings)
            localStorage.setItem(storageKey, jsonData)
        } catch (e: Exception) {
            console.error("Failed to save drawings to storage", e)
        }
    }
} 