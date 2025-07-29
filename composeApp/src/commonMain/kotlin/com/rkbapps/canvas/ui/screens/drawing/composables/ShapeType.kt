package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Immutable
@Stable
@Serializable
enum class ShapeType {
    NONE,
    LINE,
    RECTANGLE,
    SQUARE,
    CIRCLE,
    OVAL,
    TRIANGLE,
    ARROW_LEFT,
    ARROW_RIGHT,
    ARROW_UP,
    ARROW_DOWN,
    STAR,
    PENTAGON,
    HEXAGON
}