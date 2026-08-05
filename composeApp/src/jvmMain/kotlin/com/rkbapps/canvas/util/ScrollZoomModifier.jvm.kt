package com.rkbapps.canvas.util

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerKeyboardModifiers
import androidx.compose.ui.input.pointer.isCtrlPressed
import androidx.compose.ui.input.pointer.isMetaPressed
import androidx.compose.ui.input.pointer.onPointerEvent
import com.rkbapps.canvas.ui.screens.drawing.DrawingAction

/**
 * JVM/Desktop actual: Ctrl/Cmd + scroll-wheel = zoom, plain scroll = pan.
 */
@OptIn(ExperimentalComposeUiApi::class)
actual fun Modifier.desktopScrollZoom(
    isLegacy: Boolean,
    onAction: (DrawingAction) -> Unit,
): Modifier {
    if (isLegacy) return this   // no-op for legacy full-screen drawings

    return this.onPointerEvent(PointerEventType.Scroll) { event ->
        val scrollDelta = event.changes.firstOrNull()?.scrollDelta ?: return@onPointerEvent
        val mods: PointerKeyboardModifiers = event.keyboardModifiers
        val ctrlHeld = mods.isCtrlPressed || mods.isMetaPressed
        if (ctrlHeld) {
            // Ctrl/Cmd + scroll up = zoom in (factor > 1), scroll down = zoom out
            val zoomFactor = (1f - scrollDelta.y * 0.1f).coerceIn(0.5f, 2f)
            onAction(DrawingAction.OnZoomPan(zoomFactor, Offset.Zero))
        } else {
            // Plain scroll = pan in scroll direction
            val panDelta = Offset(
                x = -scrollDelta.x * 50f,
                y = -scrollDelta.y * 50f,
            )
            onAction(DrawingAction.OnZoomPan(1f, panDelta))
        }
    }
}
