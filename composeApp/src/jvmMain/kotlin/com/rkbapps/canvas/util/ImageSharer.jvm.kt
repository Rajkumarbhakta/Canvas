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
import java.awt.Desktop
import java.io.File
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.swing.JOptionPane

class ImageSharerJvm: ImageSharer {
    
    private fun generateBufferedImage(drawingState: DrawingState): BufferedImage {
        val width = 1080
        val height = 1920
        
        // Setup Skia bitmap
        val skiaBitmap = Bitmap()
        val imageInfo = ImageInfo(width, height, ColorType.RGBA_8888, ColorAlphaType.PREMUL)
        skiaBitmap.allocPixels(imageInfo)
        val skiaCanvas = org.jetbrains.skia.Canvas(skiaBitmap)
        
        // Clear background
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
        
        // Use Compose DrawScope to draw on Skia canvas
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
        
        // Convert Skia Bitmap (RGBA) to BufferedImage (ARGB)
        val bytes = skiaBitmap.readPixels(skiaBitmap.imageInfo, (width * 4), 0, 0)
        val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        
        if (bytes != null) {
            val pixels = IntArray(width * height)
            val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
            
            for (i in 0 until width * height) {
                val r = buffer.get().toInt() and 0xFF
                val g = buffer.get().toInt() and 0xFF
                val b = buffer.get().toInt() and 0xFF
                val a = buffer.get().toInt() and 0xFF
                pixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
            }
            bufferedImage.setRGB(0, 0, width, height, pixels, 0, width)
        }
        
        return bufferedImage
    }

    override fun shareDrawing(drawingState: DrawingState, fileName: String) {
        val file = saveToPictures(drawingState, fileName, showDialog = false)
        if (file != null && Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(file)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun saveDrawing(drawingState: DrawingState, fileName: String) {
        saveToPictures(drawingState, fileName, showDialog = true)
    }
    
    private fun saveToPictures(drawingState: DrawingState, fileName: String, showDialog: Boolean): File? {
        try {
            val bufferedImage = generateBufferedImage(drawingState)
            
            val userHome = System.getProperty("user.home")
            val picturesDir = File(userHome, "Pictures" + File.separator + "Canvas")
            if (!picturesDir.exists()) {
                picturesDir.mkdirs()
            }
            
            val safeFileName = fileName.replace(Regex("[\\\\/:*?\"<>|]"), "_")
            val file = File(picturesDir, "$safeFileName.png")
            ImageIO.write(bufferedImage, "png", file)
            
            if (showDialog) {
                JOptionPane.showMessageDialog(null, "Image saved to: ${file.absolutePath}", "Success", JOptionPane.INFORMATION_MESSAGE)
            }
            
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            if (showDialog) {
                JOptionPane.showMessageDialog(null, "Failed to save image: ${e.message}", "Error", JOptionPane.ERROR_MESSAGE)
            }
            return null
        }
    }
}
