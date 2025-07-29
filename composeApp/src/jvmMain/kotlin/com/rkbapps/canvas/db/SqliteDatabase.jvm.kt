package com.rkbapps.canvas.db

import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.util.json
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet

actual class SqliteDatabase {
    private var connection: Connection? = null
    private val dbFile = File(System.getProperty("user.home"), "canvas_database.db")
    
    private fun getConnection(): Connection {
        if (connection?.isClosed != false) {
            Class.forName("org.sqlite.JDBC")
            connection = DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}")
            createTableIfNotExists()
        }
        return connection!!
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
        
        getConnection().createStatement().execute(createTableSQL)
    }
    
    actual suspend fun insertDrawing(drawing: SavedDesign) {
        val sql = "INSERT OR REPLACE INTO drawings (id, name, time, state) VALUES (?, ?, ?, ?)"
        getConnection().prepareStatement(sql).use { stmt ->
            stmt.setString(1, drawing.id)
            stmt.setString(2, drawing.name)
            stmt.setString(3, drawing.time.toString())
            stmt.setString(4, json.encodeToString(com.rkbapps.canvas.model.DrawingState.serializer(), drawing.state))
            stmt.executeUpdate()
        }
    }
    
    actual suspend fun getAllDrawings(): List<SavedDesign> {
        val sql = "SELECT * FROM drawings ORDER BY time DESC"
        val drawings = mutableListOf<SavedDesign>()
        
        getConnection().createStatement().use { stmt ->
            val rs = stmt.executeQuery(sql)
            while (rs.next()) {
                drawings.add(rs.toSavedDesign())
            }
        }
        return drawings
    }
    
    actual suspend fun getDrawingById(id: String): SavedDesign? {
        val sql = "SELECT * FROM drawings WHERE id = ?"
        getConnection().prepareStatement(sql).use { stmt ->
            stmt.setString(1, id)
            val rs = stmt.executeQuery()
            return if (rs.next()) rs.toSavedDesign() else null
        }
    }
    
    actual suspend fun deleteDrawingById(id: String) {
        val sql = "DELETE FROM drawings WHERE id = ?"
        getConnection().prepareStatement(sql).use { stmt ->
            stmt.setString(1, id)
            stmt.executeUpdate()
        }
    }
    
    actual suspend fun updateDrawing(drawing: SavedDesign) {
        insertDrawing(drawing) // SQLite INSERT OR REPLACE handles updates
    }
    
    private fun ResultSet.toSavedDesign(): SavedDesign {
        val id = getString("id")
        val name = getString("name")
        val time = getString("time")
        val stateJson = getString("state")
        
        val state = json.decodeFromString(com.rkbapps.canvas.model.DrawingState.serializer(), stateJson)
        val instant = kotlinx.datetime.Instant.parse(time)
        
        return SavedDesign(id = id, name = name, time = instant, state = state)
    }
} 