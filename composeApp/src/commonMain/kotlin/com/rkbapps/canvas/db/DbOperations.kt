package com.rkbapps.canvas.db

import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.util.Log
import com.rkbapps.canvas.util.json

class DbOperations(private val dbManager: DbManager) {

    companion object{
        private const val KEY = "drawings"
    }

    fun save(drawing: SavedDesign){
        val serialized = json.encodeToString(SavedDesign.serializer(), drawing)
        Log.d("Serialized",serialized)
    }

}