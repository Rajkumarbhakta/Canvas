package com.rkbapps.canvas.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asComposeCanvas
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.ui.composables.drawPath
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.ColorType
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLAnchorElement
import org.khronos.webgl.Uint8ClampedArray
import org.w3c.dom.ImageData
import org.khronos.webgl.set

class ImageSharerWeb : ImageSharer {
    
    @OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
    private fun generateCanvas(drawingState: DrawingState): HTMLCanvasElement {
        val width = 720
        val height = 1280
        
        val skiaBitmap = Bitmap()
        val imageInfo = ImageInfo(width, height, ColorType.RGBA_8888, ColorAlphaType.PREMUL)
        skiaBitmap.allocPixels(imageInfo)
        val skiaCanvas = org.jetbrains.skia.Canvas(skiaBitmap)
        
        val bg = drawingState.backgroundColor
        skiaCanvas.clear(org.jetbrains.skia.Color.makeARGB(
            (bg.alpha * 255).toInt(),
            (bg.red * 255).toInt(),
            (bg.green * 255).toInt(),
            (bg.blue * 255).toInt()
        ))

        // Calculate bounding box of all paths
        var minX = Float.MAX_VALUE
        var minY = Float.MAX_VALUE
        var maxX = Float.MIN_VALUE
        var maxY = Float.MIN_VALUE
        var hasContent = false

        val allPaths = drawingState.paths + listOfNotNull(drawingState.currentPath)
        
        allPaths.forEach { pathData ->
            val points = if (pathData.shapePoints.isNotEmpty()) pathData.shapePoints else pathData.path
            points.forEach { offset ->
                minX = minOf(minX, offset.x)
                minY = minOf(minY, offset.y)
                maxX = maxOf(maxX, offset.x)
                maxY = maxOf(maxY, offset.y)
                hasContent = true
            }
        }
        
        val drawScope = CanvasDrawScope()
        drawScope.draw(
            density = Density(1f), 
            layoutDirection = LayoutDirection.Ltr,
            canvas = skiaCanvas.asComposeCanvas(),
            size = androidx.compose.ui.geometry.Size(width.toFloat(), height.toFloat())
        ) {
            if (hasContent) {
                val padding = 50f
                val contentWidth = maxX - minX
                val contentHeight = maxY - minY
                
                val scaleX = (width - 2 * padding) / maxOf(contentWidth, 1f)
                val scaleY = (height - 2 * padding) / maxOf(contentHeight, 1f)
                val scale = minOf(scaleX, scaleY)
                
                val offsetX = (width - contentWidth * scale) / 2f - minX * scale
                val offsetY = (height - contentHeight * scale) / 2f - minY * scale

                withTransform({
                    translate(offsetX, offsetY)
                    scale(scale, scale, Offset.Zero)
                }) {
                    allPaths.forEach {
                        drawPath(
                            it.path,
                            it.color,
                            it.thickness,
                            it.pathEffect,
                            isEraser = it.isEraser,
                            drawingState.backgroundColor,
                            it.shapeType,
                            it.shapePoints
                        )
                    }
                }
            }
        }
        
        val htmlCanvas = document.createElement("canvas") as HTMLCanvasElement
        htmlCanvas.width = width
        htmlCanvas.height = height
        val ctx = htmlCanvas.getContext("2d") as CanvasRenderingContext2D
        
        val pixels = skiaBitmap.readPixels(skiaBitmap.imageInfo, (width * 4), 0, 0)
        if (pixels != null) {
            val array = Uint8ClampedArray(pixels.size)
            
            for (i in 0 until pixels.size) {
                val byteValue = pixels[i].toInt()
                val uintValue = if (byteValue < 0) byteValue + 256 else byteValue
                array.set(i, uintValue.toByte())
            }
            
            try {
                val imageData = ImageData(array, width, height)
                ctx.putImageData(imageData, 0.0, 0.0)
            } catch (e: Exception) {
                val imageData = ctx.createImageData(width.toDouble(), height.toDouble())
                val targetData = imageData.data
                for (i in 0 until pixels.size) {
                    val byteValue = pixels[i].toInt()
                    val uintValue = if (byteValue < 0) byteValue + 256 else byteValue
                    targetData.set(i, uintValue.toByte())
                }
                ctx.putImageData(imageData, 0.0, 0.0)
            }
        }
        
        return htmlCanvas
    }

    override fun shareDrawing(drawingState: DrawingState, fileName: String) {
        saveDrawing(drawingState, fileName)
    }

    @OptIn(ExperimentalWasmJsInterop::class)
    override fun saveDrawing(drawingState: DrawingState, fileName: String) {
        try {
            val canvas = generateCanvas(drawingState)
            val dataUrl = canvas.toDataURL("image/png")
            
            val link = document.createElement("a") as HTMLAnchorElement
            link.download = "${fileName.replace(" ", "_")}.png"
            link.href = dataUrl
            document.body?.appendChild(link)
            link.click()
            document.body?.removeChild(link)
        } catch (e: Exception) {
            println("Error saving drawing: ${e.message}")
        }
    }
}
