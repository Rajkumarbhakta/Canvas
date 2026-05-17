package com.rkbapps.canvas.model
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Instant


@Serializable
data class SavedDesign(
    val pId: Long = 0L,
    val id: String = Clock.System.now().toString(),
    val name: String,
    val time: Instant = Clock.System.now(),
    val state: DrawingState
)

