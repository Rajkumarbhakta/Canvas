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
    val state: DrawingState,
    /** True for drawings created before the page-coordinate system was introduced.
     *  Legacy drawings render with no page frame, filling the screen as before. */
    val isLegacy: Boolean = false,
)

