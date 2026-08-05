package com.rkbapps.canvas.util

import androidx.compose.ui.Modifier
import com.rkbapps.canvas.ui.screens.drawing.DrawingAction

/** Android actual: scroll-wheel zoom is not applicable on touch devices — no-op. */
actual fun Modifier.desktopScrollZoom(
    isLegacy: Boolean,
    onAction: (DrawingAction) -> Unit,
): Modifier = this
