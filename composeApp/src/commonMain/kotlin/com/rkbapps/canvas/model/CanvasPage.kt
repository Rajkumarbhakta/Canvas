package com.rkbapps.canvas.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.time.Clock

/**
 * Represents a single page within a multi-page drawing.
 *
 * New drawings use [DrawingState.pages] (a list of [CanvasPage]).
 * Legacy drawings (isLegacy = true) still use [DrawingState.paths] directly
 * and never touch this model.
 */
@Stable
@Immutable
@Serializable
data class CanvasPage(
    /** Unique identifier for this page. */
    val id: String = Clock.System.now().toString(),

    /** Background fill colour for this specific page. */
    @Contextual
    val backgroundColor: Color = Color.White,

    /** All committed strokes/shapes on this page. */
    val paths: List<PathData> = emptyList(),

    /** Per-page undo stack — each entry is a full snapshot of [paths]. */
    val undoStack: List<List<PathData>> = emptyList(),

    /** Per-page redo stack. */
    val redoStack: List<List<PathData>> = emptyList(),
)
