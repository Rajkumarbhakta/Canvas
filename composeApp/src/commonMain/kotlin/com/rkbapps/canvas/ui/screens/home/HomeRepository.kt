package com.rkbapps.canvas.ui.screens.home

import com.rkbapps.canvas.db.DbOperations
import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.model.SavedDesigns
import com.rkbapps.canvas.util.Log
import com.rkbapps.canvas.util.json
import com.russhwolf.settings.ExperimentalSettingsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Repository for managing home screen data operations including designs management,
 * search functionality, and caching.
 */
class HomeRepository(
    private val dbOperations: DbOperations
) {
    // Core design data flow
    private val _allDesign = MutableStateFlow(SavedDesigns())
    val allDesign: StateFlow<SavedDesigns> = _allDesign.asStateFlow()
    
    // Loading and error states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // Search functionality
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // Filtered designs based on search
    val filteredDesigns: StateFlow<SavedDesigns> = combine(
        allDesign,
        searchQuery
    ) { designs, query ->
        if (query.isBlank()) {
            designs
        } else {
            SavedDesigns(
                designs = designs.designs.filter { design ->
                    design.name.contains(query, ignoreCase = true) ||
                    // You can add more search criteria here, like tags, creation date, etc.
                    design.id.contains(query, ignoreCase = true)
                }
            )
        }
    }.distinctUntilChanged()
    .stateIn(
        scope = CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SavedDesigns()
    )
    
    // Statistics
    val designCount: StateFlow<Int> = allDesign
        .map { it.designs.size }
        .distinctUntilChanged()
        .stateIn(
            scope = CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
    
    /**
     * Updates the search query for filtering designs
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    /**
     * Clears the current search query
     */
    fun clearSearch() {
        _searchQuery.value = ""
    }
    
    /**
     * Gets all designs from the database with proper error handling and loading states
     */
    @OptIn(ExperimentalSettingsApi::class)
    suspend fun getAllDesign(scope: CoroutineScope) {
        _isLoading.value = true
        _error.value = null
        
        try {
            // First, try to get data from flow (real-time updates)
            dbOperations.getAllFlow()
                .distinctUntilChanged()
                .catch { exception ->
                    Log.e("HomeRepository", "Error in getAllFlow: ${exception.message}")
                    _error.value = "Failed to load designs: ${exception.message}"
                    
                    // Fallback to direct database call
                    try {
                        val designs = dbOperations.getAll()
                        _allDesign.value = designs
                    } catch (fallbackException) {
                        Log.e("HomeRepository", "Fallback failed: ${fallbackException.message}")
                        _error.value = "Unable to load designs. Please try again."
                    }
                }
                .stateIn(
                    scope = scope,
                    started = SharingStarted.Eagerly,
                    initialValue = SavedDesigns()
                )
                .collect { designs ->
                    _allDesign.value = designs
                    Log.d("HomeRepository", "Loaded ${designs.designs.size} designs")
                }
        } catch (e: Exception) {
            Log.e("HomeRepository", "Error in getAllDesign: ${e.message}")
            _error.value = "Failed to load designs: ${e.message}"
            
            // Final fallback - try direct call one more time
            try {
                val designs = dbOperations.getAll()
                _allDesign.value = designs
                _error.value = null // Clear error if successful
            } catch (fallbackException) {
                Log.e("HomeRepository", "Final fallback failed: ${fallbackException.message}")
                _error.value = "Unable to load designs. Please restart the app."
            }
        } finally {
            _isLoading.value = false
        }
    }
    
    /**
     * Deletes a design by ID with optimistic updates and error handling
     */
    suspend fun deleteDesign(id: String, scope: CoroutineScope) {
        _isLoading.value = true
        _error.value = null
        
        // Store original state for potential rollback
        val originalDesigns = _allDesign.value
        
        try {
            // Optimistic update - remove from UI immediately
            val updatedDesigns = SavedDesigns(
                designs = originalDesigns.designs.filter { it.id != id }
            )
            _allDesign.value = updatedDesigns
            
            // Perform actual deletion
            dbOperations.delete(id)
            
            // Refresh data to ensure consistency
            refreshDesigns(scope)
            
            Log.d("HomeRepository", "Successfully deleted design with id: $id")
            
        } catch (e: Exception) {
            Log.e("HomeRepository", "Error deleting design: ${e.message}")
            
            // Rollback optimistic update
            _allDesign.value = originalDesigns
            _error.value = "Failed to delete design: ${e.message}"
            
            throw e // Re-throw so UI can handle it
        } finally {
            _isLoading.value = false
        }
    }
    
    /**
     * Refreshes the design list from database
     */
    suspend fun refreshDesigns(scope: CoroutineScope) {
        try {
            getAllDesign(scope)
        } catch (e: Exception) {
            Log.e("HomeRepository", "Error refreshing designs: ${e.message}")
            _error.value = "Failed to refresh designs"
        }
    }
    
    /**
     * Gets a specific design by ID
     */
    suspend fun getDesignById(id: String): SavedDesign? {
        return try {
            _allDesign.value.designs.find { it.id == id }
                ?: dbOperations.getAll().designs.find { it.id == id }
        } catch (e: Exception) {
            Log.e("HomeRepository", "Error getting design by id: ${e.message}")
            null
        }
    }
    
    /**
     * Clears any error messages
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * Checks if designs list is empty
     */
    fun isEmpty(): Boolean = _allDesign.value.designs.isEmpty()
    
    /**
     * Gets recent designs (last 5)
     */
    fun getRecentDesigns(): List<SavedDesign> {
        return _allDesign.value.designs
            .sortedByDescending { it.id } // Assuming ID represents creation order
            .take(5)
    }
    
    /**
     * Gets design statistics
     */
    data class DesignStats(
        val totalCount: Int,
        val filteredCount: Int,
        val hasSearchActive: Boolean
    )
    
    fun getDesignStats(): StateFlow<DesignStats> = combine(
        allDesign,
        filteredDesigns,
        searchQuery
    ) { all, filtered, query ->
        DesignStats(
            totalCount = all.designs.size,
            filteredCount = filtered.designs.size,
            hasSearchActive = query.isNotBlank()
        )
    }.stateIn(
        scope = CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DesignStats(0, 0, false)
    )
}
