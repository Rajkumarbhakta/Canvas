package com.rkbapps.canvas.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Clock.System
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class SavedDesign(
    val id:String = System.now().toString(),
    val name:String,
    val time: Instant = System.now(),
    val state: DrawingState
)
