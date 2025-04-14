package com.rkbapps.canvas.db

import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.model.SavedDesigns
import com.rkbapps.canvas.util.Platforms
import com.rkbapps.canvas.util.getPlatform
import com.rkbapps.canvas.util.json
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.getStringFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


expect fun saveDrawingData(json: String,settings: Settings)
expect fun loadDrawingData(settings: Settings): String


class DbOperations(private val dbManager: DbManager) {

    companion object{
        const val KEY = "drawings"
        val DEFAULT_VALUE = json.encodeToString(SavedDesigns.serializer(), SavedDesigns())
    }

    suspend fun save(drawing: SavedDesign) = withContext(Dispatchers.Default){
        val data = loadDrawingData(settings = dbManager.getSettings())
        val savedDesigns = json.decodeFromString(SavedDesigns.serializer(),data)
        if (savedDesigns.designs.isNotEmpty()){
            val oldDesign = savedDesigns.designs.find { it.id == drawing.id }
            if(oldDesign!=null){
                val filteredData = savedDesigns.designs.filterNot { it.id == drawing.id }
                val newData = savedDesigns.copy(designs = filteredData + drawing)
                saveDrawingData(json.encodeToString(SavedDesigns.serializer(),newData), settings = dbManager.getSettings())
            }else{
                val newData = savedDesigns.copy(designs = savedDesigns.designs + drawing)
                saveDrawingData(json.encodeToString(SavedDesigns.serializer(),newData), settings = dbManager.getSettings())
            }
        }else{
            val newData = savedDesigns.copy(designs = savedDesigns.designs + drawing)
            saveDrawingData(json.encodeToString(SavedDesigns.serializer(),newData), settings = dbManager.getSettings())
        }
    }

    suspend fun delete(id:String) = withContext(Dispatchers.Default){
        val data = loadDrawingData(settings = dbManager.getSettings())
        val savedDesigns = json.decodeFromString(SavedDesigns.serializer(),data)
        val newData = savedDesigns.copy(designs = savedDesigns.designs.filterNot { it.id == id })
        saveDrawingData(json.encodeToString(SavedDesigns.serializer(),newData), settings = dbManager.getSettings())
    }

    suspend fun delete(design: SavedDesign) = withContext(Dispatchers.Default){
        val data = loadDrawingData(settings = dbManager.getSettings())
        val savedDesigns = json.decodeFromString(SavedDesigns.serializer(),data)
        val newData = savedDesigns.copy(designs = savedDesigns.designs.filterNot { it.id == design.id })
        saveDrawingData(json.encodeToString(SavedDesigns.serializer(),newData), settings = dbManager.getSettings())
    }

    fun getDesign(id:String):SavedDesign?{
        val data = loadDrawingData(settings = dbManager.getSettings())
        val savedDesigns = json.decodeFromString(SavedDesigns.serializer(),data)
        return savedDesigns.designs.find { it.id == id }
    }

    fun getAll():SavedDesigns {
        val data = loadDrawingData(settings = dbManager.getSettings())
        val savedDesigns = json.decodeFromString(SavedDesigns.serializer(),data)
        return savedDesigns
    }

    @ExperimentalSettingsApi
    fun getAllFlow():Flow<SavedDesigns>{
        val observableSettings: ObservableSettings? = if (getPlatform() == Platforms.DESKTOP) null else dbManager.getObservableSettings()
        return observableSettings!!.getStringFlow(KEY,DEFAULT_VALUE).map {
            json.decodeFromString(SavedDesigns.serializer(),it)
        }
    }


}