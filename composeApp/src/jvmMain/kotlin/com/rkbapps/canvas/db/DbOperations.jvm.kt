package com.rkbapps.canvas.db


import com.russhwolf.settings.Settings
import java.io.File

actual fun saveDrawingData(json: String,settings: Settings) {
    try {
        writeTextFile(content = json)
    }catch (e: Exception){
        e.printStackTrace()
    }
}

actual fun loadDrawingData(settings: Settings): String {
    return try {
        readTextFile()?:DbOperations.DEFAULT_VALUE
    }catch (e: Exception){
        DbOperations.DEFAULT_VALUE
    }
}

fun writeTextFile(fileName: String = "${DbOperations.KEY}.json", content: String) {
    val file = File(System.getProperty("user.home"), fileName)
    file.writeText(content)
}

fun readTextFile(fileName: String = "${DbOperations.KEY}.json"): String? {
    val file = File(System.getProperty("user.home"), fileName)
    return if (file.exists()) file.readText() else null
}