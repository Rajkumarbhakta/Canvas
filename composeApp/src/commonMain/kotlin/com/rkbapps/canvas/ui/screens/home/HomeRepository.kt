package com.rkbapps.canvas.ui.screens.home

import com.rkbapps.canvas.db.DbOperations
import com.rkbapps.canvas.model.SavedDesigns
import com.rkbapps.canvas.util.Log
import com.rkbapps.canvas.util.json
import com.russhwolf.settings.ExperimentalSettingsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn

class HomeRepository (
    private val dbOperations: DbOperations
) {
    private val _allDesign = MutableStateFlow(SavedDesigns())
    val allDesign = _allDesign.asStateFlow()

    @OptIn(ExperimentalSettingsApi::class)
    suspend fun getAllDesign(
        scope: CoroutineScope
    ){
        try {
            dbOperations.getAllFlow().stateIn(scope, SharingStarted.Eagerly,SavedDesigns()).collect {
                _allDesign.value = it
            }
        }catch (e: Exception){
            val designs = dbOperations.getAll()
            _allDesign.value = designs
        }
    }

    suspend fun deleteDesign(id: String,scope: CoroutineScope){
        dbOperations.delete(id)
        getAllDesign(scope)
    }

}