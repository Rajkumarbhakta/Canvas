package com.rkbapps.canvas.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Home screen that manages UI state, search functionality,
 * and coordinates with the repository for data operations.
 */
class HomeViewModel(
    private val repository: HomeRepository
): ViewModel() {
    
    // Repository flows
    val allDesign = repository.allDesign
    val filteredDesigns = repository.filteredDesigns
    val isLoading = repository.isLoading
    val error = repository.error
    val designCount = repository.designCount
    val designStats = repository.getDesignStats()
    
    // UI State
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    
    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _isGridView = MutableStateFlow(true)
    val isGridView: StateFlow<Boolean> = _isGridView.asStateFlow()
    
    // Events for one-time UI actions
    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents: SharedFlow<UiEvent> = _uiEvents.asSharedFlow()
    
    // Search debounce job
    private var searchJob: Job? = null
    
    // Initialization
    init {
        getAllDesign()
    }
    
    /**
     * Gets all designs from repository
     */
    fun getAllDesign() {
        viewModelScope.launch {
            try {
                repository.getAllDesign(viewModelScope)
            } catch (e: Exception) {
                _uiEvents.emit(UiEvent.ShowError("Failed to load designs: ${e.message}"))
            }
        }
    }
    
    /**
     * Refreshes designs with pull-to-refresh UI feedback
     */
    fun refreshDesigns() {
        viewModelScope.launch {
            try {
                _isRefreshing.value = true
                repository.refreshDesigns(viewModelScope)
                _uiEvents.emit(UiEvent.ShowSuccess("Designs refreshed"))
            } catch (e: Exception) {
                _uiEvents.emit(UiEvent.ShowError("Failed to refresh: ${e.message}"))
            } finally {
                _isRefreshing.value = false
            }
        }
    }
    
    /**
     * Deletes a design with proper error handling and user feedback
     */
    fun deleteDesign(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteDesign(id, viewModelScope)
                _uiEvents.emit(UiEvent.ShowSuccess("Design deleted successfully"))
            } catch (e: Exception) {
                _uiEvents.emit(UiEvent.ShowError("Failed to delete design: ${e.message}"))
            }
        }
    }
    
    /**
     * Updates search query with debouncing to avoid excessive filtering
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        
        // Cancel previous search job
        searchJob?.cancel()
        
        // Debounce search to avoid excessive filtering while user is typing
        searchJob = viewModelScope.launch {
            delay(300) // Wait 300ms before executing search
            repository.updateSearchQuery(query)
        }
    }
    
    /**
     * Activates search mode
     */
    fun activateSearch() {
        _isSearchActive.value = true
    }
    
    /**
     * Deactivates search mode and clears search
     */
    fun deactivateSearch() {
        _isSearchActive.value = false
        clearSearch()
    }
    
    /**
     * Clears the search query
     */
    fun clearSearch() {
        searchJob?.cancel()
        _searchQuery.value = ""
        repository.clearSearch()
    }
    
    /**
     * Toggles between grid and list view
     */
    fun toggleViewMode() {
        _isGridView.value = !_isGridView.value
    }
    
    /**
     * Sets view mode explicitly
     */
    fun setViewMode(isGrid: Boolean) {
        _isGridView.value = isGrid
    }
    
    /**
     * Clears any error messages
     */
    fun clearError() {
        repository.clearError()
    }
    
    /**
     * Gets a specific design by ID
     */
    fun getDesignById(id: String, callback: (design: com.rkbapps.canvas.model.SavedDesign?) -> Unit) {
        viewModelScope.launch {
            val design = repository.getDesignById(id)
            callback(design)
        }
    }
    
    /**
     * Checks if there are any designs
     */
    fun hasDesigns(): Boolean = !repository.isEmpty()
    
    /**
     * Gets recent designs for quick access
     */
    fun getRecentDesigns() = repository.getRecentDesigns()
    
    /**
     * Handles retry action for failed operations
     */
    fun retry() {
        when {
            error.value != null -> {
                clearError()
                getAllDesign()
            }
            else -> getAllDesign()
        }
    }
    
    /**
     * Performs search with immediate feedback
     */
    fun performSearch(query: String) {
        updateSearchQuery(query)
        if (query.isNotBlank()) {
            viewModelScope.launch {
                _uiEvents.emit(UiEvent.SearchPerformed(query))
            }
        }
    }
    
    /**
     * Gets search suggestions based on existing design names
     */
    fun getSearchSuggestions(query: String): List<String> {
        if (query.isBlank()) return emptyList()
        
        return allDesign.value.designs
            .map { it.name }
            .filter { it.contains(query, ignoreCase = true) }
            .distinct()
            .take(5)
    }
    
    /**
     * Handles batch operations
     */
    fun deleteMultipleDesigns(ids: List<String>) {
        viewModelScope.launch {
            try {
                var successCount = 0
                var failureCount = 0
                
                ids.forEach { id ->
                    try {
                        repository.deleteDesign(id, viewModelScope)
                        successCount++
                    } catch (e: Exception) {
                        failureCount++
                    }
                }
                
                when {
                    failureCount == 0 -> {
                        _uiEvents.emit(UiEvent.ShowSuccess("$successCount designs deleted"))
                    }
                    successCount == 0 -> {
                        _uiEvents.emit(UiEvent.ShowError("Failed to delete designs"))
                    }
                    else -> {
                        _uiEvents.emit(UiEvent.ShowInfo("$successCount deleted, $failureCount failed"))
                    }
                }
            } catch (e: Exception) {
                _uiEvents.emit(UiEvent.ShowError("Batch delete failed: ${e.message}"))
            }
        }
    }
    
    /**
     * UI Events for one-time actions
     */
    sealed class UiEvent {
        data class ShowError(val message: String) : UiEvent()
        data class ShowSuccess(val message: String) : UiEvent()
        data class ShowInfo(val message: String) : UiEvent()
        data class SearchPerformed(val query: String) : UiEvent()
        object NavigateToCreateDesign : UiEvent()
        data class NavigateToEditDesign(val designId: String) : UiEvent()
    }
    
    /**
     * Cleanup when ViewModel is destroyed
     */
    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }
}
