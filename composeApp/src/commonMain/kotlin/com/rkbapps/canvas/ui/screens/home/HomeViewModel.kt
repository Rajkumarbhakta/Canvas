package com.rkbapps.canvas.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkbapps.canvas.db.migrator.DataMigrationManager
import com.rkbapps.canvas.model.SavedDesigns
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: HomeRepository,
    private val dataMigrationManager: DataMigrationManager
): ViewModel() {
    val allDesign = repository.allDesign.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), SavedDesigns())

    init {
        viewModelScope.launch {
            dataMigrationManager.migrateIfRequired()
        }
    }

    fun getAllDesign(){
        // No-op as allDesign is a Flow now
    }
    fun deleteDesign(id: Long){
        viewModelScope.launch {
            repository.deleteDesign(id)
        }
    }

}