package com.rkbapps.canvas.util

import androidx.compose.ui.Modifier
import com.rkbapps.canvas.ui.screens.drawing.DrawingAction

/**
 * Applies a scroll-wheel zoom/pan gesture handler to this [Modifier].
 *
 * On **JVM/Desktop**: Ctrl/Cmd + scroll = zoom, plain scroll = pan.
 * On **Android** and **iOS/Native**: this is a no-op (returns [this] unchanged).
 *
 * Using expect/actual lets [DesktopDrawingLayout] remain in `commonMain` while
 * the JVM-specific `onPointerEvent` API stays in `jvmMain`.
 *
 * @param isLegacy  When true the handler does nothing (legacy drawings are full-screen
 *                  and have no zoom/pan state to modify).
 * @param onAction  Callback to dispatch [DrawingAction.OnZoomPan] events.
 */
expect fun Modifier.desktopScrollZoom(
    isLegacy: Boolean,
    onAction: (DrawingAction) -> Unit,
): Modifier
