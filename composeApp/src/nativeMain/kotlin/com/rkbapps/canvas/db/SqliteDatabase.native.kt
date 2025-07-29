package com.rkbapps.canvas.db

import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.util.json
import kotlinx.cinterop.*
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSString
import platform.Foundation.stringByAppendingPathComponent
import platform.posix.*
import sqlite3.*

actual class SqliteDatabase {
    private var db: CPointer<sqlite3>? = null
    private val dbPath: String
    
    init {
        val homeDir = NSString.create(string = NSHomeDirectory())
        val dbFileName = "canvas_database.db"
        dbPath = homeDir.stringByAppendingPathComponent(dbFileName)
        initializeDatabase()
    }
    
    private fun initializeDatabase() {
        val result = sqlite3_open(dbPath, db.ptr)
        if (result != SQLITE_OK) {
            throw Exception("Failed to open database: ${sqlite3_errmsg(db)}")
        }
        createTableIfNotExists()
    }
    
    private fun createTableIfNotExists() {
        val createTableSQL = """
            CREATE TABLE IF NOT EXISTS drawings (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                time TEXT NOT NULL,
                state TEXT NOT NULL
            )
        """.trimIndent()
        
        executeSQL(createTableSQL)
    }
    
    private fun executeSQL(sql: String) {
        val result = sqlite3_exec(db, sql, null, null, null)
        if (result != SQLITE_OK) {
            throw Exception("SQL execution failed: ${sqlite3_errmsg(db)}")
        }
    }
    
    actual suspend fun insertDrawing(drawing: SavedDesign) {
        val sql = "INSERT OR REPLACE INTO drawings (id, name, time, state) VALUES (?, ?, ?, ?)"
        val stmt = prepareStatement(sql)
        
        try {
            sqlite3_bind_text(stmt, 1, drawing.id, -1, null)
            sqlite3_bind_text(stmt, 2, drawing.name, -1, null)
            sqlite3_bind_text(stmt, 3, drawing.time.toString(), -1, null)
            sqlite3_bind_text(stmt, 4, json.encodeToString(com.rkbapps.canvas.model.DrawingState.serializer(), drawing.state), -1, null)
            
            val result = sqlite3_step(stmt)
            if (result != SQLITE_DONE) {
                throw Exception("Failed to insert drawing: ${sqlite3_errmsg(db)}")
            }
        } finally {
            sqlite3_finalize(stmt)
        }
    }
    
    actual suspend fun getAllDrawings(): List<SavedDesign> {
        val sql = "SELECT * FROM drawings ORDER BY time DESC"
        val stmt = prepareStatement(sql)
        val drawings = mutableListOf<SavedDesign>()
        
        try {
            while (sqlite3_step(stmt) == SQLITE_ROW) {
                drawings.add(stmt.toSavedDesign())
            }
        } finally {
            sqlite3_finalize(stmt)
        }
        
        return drawings
    }
    
    actual suspend fun getDrawingById(id: String): SavedDesign? {
        val sql = "SELECT * FROM drawings WHERE id = ?"
        val stmt = prepareStatement(sql)
        
        try {
            sqlite3_bind_text(stmt, 1, id, -1, null)
            
            return if (sqlite3_step(stmt) == SQLITE_ROW) {
                stmt.toSavedDesign()
            } else null
        } finally {
            sqlite3_finalize(stmt)
        }
    }
    
    actual suspend fun deleteDrawingById(id: String) {
        val sql = "DELETE FROM drawings WHERE id = ?"
        val stmt = prepareStatement(sql)
        
        try {
            sqlite3_bind_text(stmt, 1, id, -1, null)
            val result = sqlite3_step(stmt)
            if (result != SQLITE_DONE) {
                throw Exception("Failed to delete drawing: ${sqlite3_errmsg(db)}")
            }
        } finally {
            sqlite3_finalize(stmt)
        }
    }
    
    actual suspend fun updateDrawing(drawing: SavedDesign) {
        insertDrawing(drawing) // SQLite INSERT OR REPLACE handles updates
    }
    
    private fun prepareStatement(sql: String): CPointer<sqlite3_stmt> {
        val stmtPtr = alloc<CPointerVar<sqlite3_stmt>>()
        val result = sqlite3_prepare_v2(db, sql, -1, stmtPtr.ptr, null)
        if (result != SQLITE_OK) {
            throw Exception("Failed to prepare statement: ${sqlite3_errmsg(db)}")
        }
        return stmtPtr.value!!
    }
    
    private fun CPointer<sqlite3_stmt>.toSavedDesign(): SavedDesign {
        val id = sqlite3_column_text(this, 0)?.toKString() ?: ""
        val name = sqlite3_column_text(this, 1)?.toKString() ?: ""
        val time = sqlite3_column_text(this, 2)?.toKString() ?: ""
        val stateJson = sqlite3_column_text(this, 3)?.toKString() ?: ""
        
        val state = json.decodeFromString(com.rkbapps.canvas.model.DrawingState.serializer(), stateJson)
        val instant = kotlinx.datetime.Instant.parse(time)
        
        return SavedDesign(id = id, name = name, time = instant, state = state)
    }
} 