package com.rkbapps.canvas.util

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope

suspend fun PointerInputScope.detectTapAndDrag(
    onTap: (Offset) -> Unit,
    onDragStart: (Offset) -> Unit = {},
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit,
    onDragEnd: () -> Unit = {},
    onDragCancel: () -> Unit = {}
) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)

        // ── Gesture arbitration: 2+ fingers = pan/zoom on the outer desk ──────
        // When a second finger lands mid-gesture, bail immediately.
        // This prevents detectTapAndDrag competing with the outer desk's
        // detectTransformGestures when the user switches from draw to pinch.
        if (currentEvent.changes.size > 1) return@awaitEachGesture

        var isDragging = false
        var drag: PointerInputChange?

        do {
            drag = awaitTouchSlopOrCancellation(down.id) { change, _ ->
                change.consume()
                isDragging = true
            }
        } while (drag != null && !isDragging)

        if (isDragging && drag != null) {
            onDragStart(drag.position)

            val dragCompleted = drag(drag.id) { change ->
                // Abort drawing drag if a second finger joins mid-stroke
                if (currentEvent.changes.size > 1) {
                    onDragCancel()
                    return@drag
                }
                val delta = change.position - change.previousPosition
                onDrag(change, delta)
                change.consume()
            }
            if (dragCompleted) {
                onDragEnd()
            } else {
                onDragCancel()
            }
        } else {
            val up = waitForUpOrCancellation()
            if (up != null) {
                onTap(up.position)
            }
        }
    }
}