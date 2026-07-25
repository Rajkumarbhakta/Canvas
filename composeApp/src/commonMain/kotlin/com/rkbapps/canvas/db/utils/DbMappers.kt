package com.rkbapps.canvas.db.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.rkbapps.canvas.db.entities.DesignEntity
import com.rkbapps.canvas.db.entities.DesignWithPaths
import com.rkbapps.canvas.db.entities.PathEntity
import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.model.PathData
import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.ui.screens.drawing.utils.PaintingStyleType
import com.rkbapps.canvas.ui.screens.drawing.utils.ShapeType

fun SavedDesign.toEntity(): DesignEntity {
    return DesignEntity(
        id = pId,
        stringId = id,
        name = name,
        time = time,
        backgroundColor = state.backgroundColor.toArgb(),
        selectedColor = state.selectedColor.toArgb(),
        selectedThickness = state.selectedThickness,
        selectedPathEffect = state.selectedPathEffect.name,
        selectedShapeType = state.selectedShapeType.name,
        isEraserMode = false
    )
}

fun PathData.toEntity(designId: Long, index: Int): PathEntity {
    return PathEntity(
        designId = designId,
        strokeId = id,
        color = color.toArgb(),
        thickness = thickness,
        pathEffect = pathEffect.name,
        isEraser = isEraser,
        shapeType = shapeType.name,
        pathPoints = path.toBinary(),
        shapePoints = shapePoints.toBinary(),
        orderIndex = index
    )
}

fun DesignWithPaths.toDomain(): SavedDesign {
    return SavedDesign(
        pId = design.id,
        id = design.stringId,
        name = design.name,
        time = design.time,
        state = DrawingState(
            selectedColor = Color(design.selectedColor),
            selectedThickness = design.selectedThickness,
            selectedPathEffect = PaintingStyleType.valueOf(design.selectedPathEffect),
            selectedShapeType = ShapeType.valueOf(design.selectedShapeType),
            backgroundColor = Color(design.backgroundColor),
            paths = paths.sortedBy { it.orderIndex }.map { it.toDomain() }
        )
    )
}


fun PathEntity.toDomain(): PathData {
    return PathData(
        id = strokeId,
        color = Color(color),
        thickness = thickness,
        pathEffect = PaintingStyleType.valueOf(pathEffect),
        isEraser = isEraser,
        shapeType = ShapeType.valueOf(shapeType),
        path = pathPoints.toOffsets(),
        shapePoints = shapePoints.toOffsets()
    )
}

// Binary conversion helpers
fun List<Offset>.toBinary(): ByteArray {
    val bytes = ByteArray(size * 8)
    forEachIndexed { i, offset ->
        val xBits = offset.x.toRawBits()
        val yBits = offset.y.toRawBits()
        bytes[i * 8 + 0] = (xBits shr 24).toByte()
        bytes[i * 8 + 1] = (xBits shr 16).toByte()
        bytes[i * 8 + 2] = (xBits shr 8).toByte()
        bytes[i * 8 + 3] = (xBits shr 0).toByte()
        bytes[i * 8 + 4] = (yBits shr 24).toByte()
        bytes[i * 8 + 5] = (yBits shr 16).toByte()
        bytes[i * 8 + 6] = (yBits shr 8).toByte()
        bytes[i * 8 + 7] = (yBits shr 0).toByte()
    }
    return bytes
}

fun ByteArray.toOffsets(): List<Offset> {
    val size = size / 8
    return List(size) { i ->
        val xBits = (this[i * 8 + 0].toInt() and 0xFF shl 24) or
                    (this[i * 8 + 1].toInt() and 0xFF shl 16) or
                    (this[i * 8 + 2].toInt() and 0xFF shl 8) or
                    (this[i * 8 + 3].toInt() and 0xFF)
        val yBits = (this[i * 8 + 4].toInt() and 0xFF shl 24) or
                    (this[i * 8 + 5].toInt() and 0xFF shl 16) or
                    (this[i * 8 + 6].toInt() and 0xFF shl 8) or
                    (this[i * 8 + 7].toInt() and 0xFF)
        Offset(Float.fromBits(xBits), Float.fromBits(yBits))
    }
}
