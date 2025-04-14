package com.rkbapps.canvas.model

import kotlinx.serialization.Serializable

@Serializable
data class SavedDesigns(
    val designs: List<SavedDesign> = emptyList()
)
