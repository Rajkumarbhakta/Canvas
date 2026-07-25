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