import java.sql.Connection
import java.sql.DriverManager
import java.io.File

fun main() {
    val dbFile = File(System.getProperty("user.home"), "canvas_database.db")
    
    if (!dbFile.exists()) {
        println("Database file not found at: ${dbFile.absolutePath}")
        return
    }
    
    println("Database file found: ${dbFile.absolutePath}")
    println("File size: ${dbFile.length()} bytes")
    
    try {
        Class.forName("org.sqlite.JDBC")
        val connection = DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}")
        
        // Check if tables exist
        val tablesResult = connection.createStatement().executeQuery(
            "SELECT name FROM sqlite_master WHERE type='table'"
        )
        
        println("\nTables in database:")
        while (tablesResult.next()) {
            println("- ${tablesResult.getString("name")}")
        }
        
        // Check drawings table
        val drawingsResult = connection.createStatement().executeQuery(
            "SELECT COUNT(*) as count FROM drawings"
        )
        
        if (drawingsResult.next()) {
            val count = drawingsResult.getInt("count")
            println("\nNumber of drawings saved: $count")
            
            if (count > 0) {
                println("\nRecent drawings:")
                val recentDrawings = connection.createStatement().executeQuery(
                    "SELECT id, name, time FROM drawings ORDER BY time DESC LIMIT 5"
                )
                
                while (recentDrawings.next()) {
                    println("- ${recentDrawings.getString("name")} (${recentDrawings.getString("id")})")
                }
            }
        }
        
        connection.close()
        
    } catch (e: Exception) {
        println("Error reading database: ${e.message}")
    }
} 