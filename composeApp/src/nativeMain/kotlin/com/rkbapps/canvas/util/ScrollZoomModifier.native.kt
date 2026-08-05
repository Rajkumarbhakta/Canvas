package com.rkbapps.canvas.util

import androidx.compose.ui.Modifier
import com.rkbapps.canvas.ui.screens.drawing.DrawingAction

/** Native (iOS) actual: scroll-wheel zoom is not applicable on iOS — no-op. */
actual fun Modifier.desktopScrollZoom(
    isLegacy: Boolean,
    onAction: (DrawingAction) -> Unit,
): Modifier = this
