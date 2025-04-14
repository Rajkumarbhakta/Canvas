package com.rkbapps.canvas.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: HomeRepository
): ViewModel() {
    val allDesign = repository.allDesign

    init {
        getAllDesign()
    }

    fun getAllDesign(){
        viewModelScope.launch {
            repository.getAllDesign(viewModelScope)
        }
    }
    fun deleteDesign(id:String){
        viewModelScope.launch {
            repository.deleteDesign(id,viewModelScope)
        }
    }

}