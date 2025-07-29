package com.rkbapps.canvas.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.util.json

actual class SqliteDatabase {
    private lateinit var dbHelper: DrawingDatabaseHelper
    
    fun initialize(context: Context) {
        dbHelper = DrawingDatabaseHelper(context)
    }
    
    actual suspend fun insertDrawing(drawing: SavedDesign) {
        val db = dbHelper.writableDatabase
        val contentValues = android.content.ContentValues().apply {
            put("id", drawing.id)
            put("name", drawing.name)
            put("time", drawing.time.toString())
            put("state", json.encodeToString(com.rkbapps.canvas.model.DrawingState.serializer(), drawing.state))
        }
        db.insert("drawings", null, contentValues)
    }
    
    actual suspend fun getAllDrawings(): List<SavedDesign> {
        val db = dbHelper.readableDatabase
        val cursor = db.query("drawings", null, null, null, null, null, "time DESC")
        val drawings = mutableListOf<SavedDesign>()
        
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getString(it.getColumnIndexOrThrow("id"))
                val name = it.getString(it.getColumnIndexOrThrow("name"))
                val time = it.getString(it.getColumnIndexOrThrow("time"))
                val stateJson = it.getString(it.getColumnIndexOrThrow("state"))
                
                val state = json.decodeFromString(com.rkbapps.canvas.model.DrawingState.serializer(), stateJson)
                val instant = kotlinx.datetime.Instant.parse(time)
                
                drawings.add(SavedDesign(id = id, name = name, time = instant, state = state))
            }
        }
        return drawings
    }
    
    actual suspend fun getDrawingById(id: String): SavedDesign? {
        val db = dbHelper.readableDatabase
        val cursor = db.query("drawings", null, "id = ?", arrayOf(id), null, null, null)
        
        return cursor.use {
            if (it.moveToFirst()) {
                val name = it.getString(it.getColumnIndexOrThrow("name"))
                val time = it.getString(it.getColumnIndexOrThrow("time"))
                val stateJson = it.getString(it.getColumnIndexOrThrow("state"))
                
                val state = json.decodeFromString(com.rkbapps.canvas.model.DrawingState.serializer(), stateJson)
                val instant = kotlinx.datetime.Instant.parse(time)
                
                SavedDesign(id = id, name = name, time = instant, state = state)
            } else null
        }
    }
    
    actual suspend fun deleteDrawingById(id: String) {
        val db = dbHelper.writableDatabase
        db.delete("drawings", "id = ?", arrayOf(id))
    }
    
    actual suspend fun updateDrawing(drawing: SavedDesign) {
        val db = dbHelper.writableDatabase
        val contentValues = android.content.ContentValues().apply {
            put("name", drawing.name)
            put("time", drawing.time.toString())
            put("state", json.encodeToString(com.rkbapps.canvas.model.DrawingState.serializer(), drawing.state))
        }
        db.update("drawings", contentValues, "id = ?", arrayOf(drawing.id))
    }
}

private class DrawingDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "canvas_database", null, 1) {
    
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE drawings (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                time TEXT NOT NULL,
                state TEXT NOT NULL
            )
        """.trimIndent())
    }
    
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS drawings")
        onCreate(db)
    }
} 