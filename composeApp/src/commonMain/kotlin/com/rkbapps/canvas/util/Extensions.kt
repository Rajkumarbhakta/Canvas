package com.rkbapps.canvas.util

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastForEach
import kotlin.math.abs

/**
 * Single-pointer tap + drag detector for the drawing canvas.
 *
 * This runs on the *page* canvas, which is a child of the desk `Box` that owns
 * `detectTransformGestures` (pinch zoom/pan). Compose dispatches pointer events
 * child-first on the Main pass, so this detector always sees a touch before the
 * transform detector does — and `detectTransformGestures` cancels itself as soon
 * as it observes a consumed change. Every `consume()` below therefore takes the
 * gesture away from pinch-zoom, and the rules for when we're allowed to do that
 * are the whole point of this function:
 *
 *  1. [canDrag] — never claim a drag we can't act on. In selection mode with no
 *     path selected a drag is a no-op, so we stay out of the way entirely and
 *     only report taps.
 *  2. Multi-touch yields, continuously — not just on the first event. A pinch
 *     never lands both fingers on the same event, so a one-shot check at gesture
 *     start is always too early.
 */
suspend fun PointerInputScope.detectTapAndDrag(
    onTap: (Offset) -> Unit,
    onDragStart: (Offset) -> Unit = {},
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit,
    onDragEnd: () -> Unit = {},
    onDragCancel: () -> Unit = {},
    canDrag: () -> Boolean = { true },
    requireSlop: () -> Boolean = { true }
) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)

        // ── Gesture arbitration: 2+ fingers = pan/zoom on the outer desk ──────
        if (currentEvent.changes.size > 1) return@awaitEachGesture

        // Nothing to drag → never consume, so a pinch starting here is never
        // stolen. Taps still work: waitForUpOrCancellation yields null once the
        // transform detector consumes, so no phantom tap fires after a pinch.
        if (!canDrag()) {
            waitForUpOrCancellation()?.let { onTap(it.position) }
            return@awaitEachGesture
        }

        var isDragging = false
        var drag: PointerInputChange?

        if (!requireSlop()) {
            // Drawing: ink from the very first sample. Touch slop here is
            // measured in the page's own coordinate space, which the zoom layer
            // scales — at 5× it becomes ~40dp of finger travel with no ink, and
            // everything before the threshold is discarded, so strokes start
            // late and short. Nothing competes for one-finger gestures in this
            // mode, so there is nothing to arbitrate and no reason to wait.
            // The down itself is left unconsumed — the drag loop below consumes
            // movement, and this keeps two-finger gestures adoptable if pinch is
            // ever enabled while drawing.
            drag = down
            isDragging = true
        } else {
            do {
                drag = awaitTouchSlopOrCancellation(down.id) { change, _ ->
                    // A second finger is down: leave the change unconsumed so the
                    // desk's transform detector can claim the pinch. Not consuming
                    // keeps us in this loop until it does — at which point
                    // awaitTouchSlopOrCancellation returns null and we fall through
                    // to waitForUpOrCancellation, which also sees the consumption
                    // and returns null. Net effect: this detector goes silent.
                    if (currentEvent.changes.count { it.pressed } > 1) {
                        return@awaitTouchSlopOrCancellation
                    }
                    change.consume()
                    isDragging = true
                }
            } while (drag != null && !isDragging)
        }

        if (isDragging && drag != null) {
            onDragStart(drag.position)

            // Abort the stroke if a second finger joins mid-drag, and stop
            // consuming from that point on so the pinch can take over.
            var aborted = false
            val dragCompleted = drag(drag.id) { change ->
                if (aborted) return@drag
                if (currentEvent.changes.count { it.pressed } > 1) {
                    aborted = true
                    return@drag
                }
                val delta = change.position - change.previousPosition
                onDrag(change, delta)
                change.consume()
            }
            if (dragCompleted && !aborted) onDragEnd() else onDragCancel()
        } else {
            // awaitTouchSlopOrCancellation returns null both when the gesture was
            // taken from us and when the finger simply lifted — and in the latter
            // case it has already received the up event. Waiting for another up
            // here would swallow this tap and report it on the *next* gesture, so
            // read the up it left behind instead.
            val up = currentEvent.changes.fastFirstOrNull { it.id == down.id }
            if (up != null && up.changedToUp()) onTap(up.position)
        }
    }
}
/**
 * Pinch zoom / pan detector for the desk `Box` that hosts the page frame.
 *
 * This exists instead of the stock [androidx.compose.foundation.gestures.detectTransformGestures]
 * because that one **consumes every change once it is past touch slop**, whether
 * or not the caller's callback does anything with the gesture. The desk sits
 * *outside* the page's `graphicsLayer`, so it measures touch slop in screen
 * pixels while the page canvas measures it in page pixels (screen ÷ zoom). Once
 * zoomed in, the desk therefore crosses slop first on a plain one-finger drag,
 * consumes it, and the page canvas — which is the one that actually wanted it —
 * sees a consumed change and aborts. Strokes silently stop working.
 *
 * The fix is [canClaim]: decide up front whether a gesture with this many
 * pointers is ours at all, and if it isn't, never consume. Anything we decline
 * flows through to the page canvas untouched.
 *
 * @param canClaim receives the number of currently pressed pointers.
 * @param onGesture per-event pan delta (screen px) and relative zoom factor.
 */
suspend fun PointerInputScope.detectCanvasTransformGestures(
    canClaim: (pointerCount: Int) -> Boolean,
    onGesture: (pan: Offset, zoom: Float) -> Unit
) {
    awaitEachGesture {
        var zoom = 1f
        var pan = Offset.Zero
        var pastTouchSlop = false
        val touchSlop = viewConfiguration.touchSlop

        awaitFirstDown(requireUnconsumed = false)
        do {
            val event = awaitPointerEvent()
            val canceled = event.changes.fastAny { it.isConsumed }
            if (!canceled) {
                if (!canClaim(event.changes.count { it.pressed })) {
                    // Not ours — e.g. one finger while the page canvas is drawing
                    // or moving a path. Consume nothing, and reset the slop
                    // accumulation so a later two-finger pinch starts clean.
                    zoom = 1f
                    pan = Offset.Zero
                    pastTouchSlop = false
                } else {
                    val zoomChange = event.calculateZoom()
                    val panChange = event.calculatePan()

                    if (!pastTouchSlop) {
                        zoom *= zoomChange
                        pan += panChange
                        val centroidSize = event.calculateCentroidSize(useCurrent = false)
                        val zoomMotion = abs(1f - zoom) * centroidSize
                        val panMotion = pan.getDistance()
                        if (zoomMotion > touchSlop || panMotion > touchSlop) pastTouchSlop = true
                    }

                    if (pastTouchSlop) {
                        if (zoomChange != 1f || panChange != Offset.Zero) {
                            onGesture(panChange, zoomChange)
                        }
                        event.changes.fastForEach { if (it.positionChanged()) it.consume() }
                    }
                }
            }
        } while (!canceled && event.changes.fastAny { it.pressed })
    }
}
