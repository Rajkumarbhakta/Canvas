package com.rkbapps.canvas.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asComposeCanvas
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.scale
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

class ImageSharerJvm : ImageSharer {

    // ── Internal rendering ────────────────────────────────────────────────────

    /**
     * Renders [drawingState] to a [BufferedImage].
     *
     * **Paged mode** (`isLegacy = false`): renders only the active page's paths at exactly
     * [DrawingState.pageWidth] × [DrawingState.pageHeight] dp at 2× scale (retina quality).
     * Background is the page's background colour.
     *
     * **Legacy mode** (`isLegacy = true`): uses the original bounding-box approach, rendering
     * all strokes from [DrawingState.paths] into a 1080×1920 canvas. Unchanged from before.
     */
    private fun generateBufferedImage(
        drawingState: DrawingState,
        isLegacy: Boolean,
        currentPageIndex: Int,
    ): BufferedImage {
        return if (isLegacy) {
            generateLegacyBufferedImage(drawingState)
        } else {
            generatePagedBufferedImage(drawingState, currentPageIndex)
        }
    }

    /** Paged mode: render the active page at 2× the page dimensions. */
    private fun generatePagedBufferedImage(
        drawingState: DrawingState,
        currentPageIndex: Int,
    ): BufferedImage {
        val page = drawingState.pages.getOrNull(currentPageIndex)
            ?: return BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)

        val exportScale = 2f   // 2× for crisp output on HiDPI displays
        val width  = (drawingState.pageWidth  * exportScale).toInt()
        val height = (drawingState.pageHeight * exportScale).toInt()

        val skiaBitmap = Bitmap()
        skiaBitmap.allocPixels(ImageInfo(width, height, ColorType.RGBA_8888, ColorAlphaType.PREMUL))
        val skiaCanvas = org.jetbrains.skia.Canvas(skiaBitmap)

        val bg = page.backgroundColor
        skiaCanvas.clear(
            org.jetbrains.skia.Color.makeARGB(
                (bg.alpha * 255).toInt(),
                (bg.red   * 255).toInt(),
                (bg.green * 255).toInt(),
                (bg.blue  * 255).toInt(),
            )
        )

        val drawScope = CanvasDrawScope()
        drawScope.draw(
            density = Density(1f),
            layoutDirection = LayoutDirection.Ltr,
            canvas = skiaCanvas.asComposeCanvas(),
            size = androidx.compose.ui.geometry.Size(width.toFloat(), height.toFloat()),
        ) {
            // Scale up to the export resolution; paths are in page-coordinate dp
            scale(exportScale, exportScale, pivot = Offset.Zero) {
                page.paths.forEach { pathData ->
                    drawPath(
                        path        = pathData.path,
                        color       = pathData.color,
                        thickness   = pathData.thickness,
                        pathEffect  = pathData.pathEffect,
                        isEraser    = pathData.isEraser,
                        backgroundColor = page.backgroundColor,
                        shapeType   = pathData.shapeType,
                        shapePoints = pathData.shapePoints,
                    )
                }
            }
        }

        return skiaToBufferedImage(skiaBitmap, width, height)
    }

    /** Legacy mode: original bounding-box behaviour, unchanged. */
    private fun generateLegacyBufferedImage(drawingState: DrawingState): BufferedImage {
        val width  = 1080
        val height = 1920

        val skiaBitmap = Bitmap()
        skiaBitmap.allocPixels(ImageInfo(width, height, ColorType.RGBA_8888, ColorAlphaType.PREMUL))
        val skiaCanvas = org.jetbrains.skia.Canvas(skiaBitmap)

        val bg = drawingState.backgroundColor
        skiaCanvas.clear(
            org.jetbrains.skia.Color.makeARGB(
                (bg.alpha * 255).toInt(),
                (bg.red   * 255).toInt(),
                (bg.green * 255).toInt(),
                (bg.blue  * 255).toInt(),
            )
        )

        var minX = Float.MAX_VALUE; var minY = Float.MAX_VALUE
        var maxX = Float.MIN_VALUE; var maxY = Float.MIN_VALUE
        var hasContent = false

        val allPaths = drawingState.paths + listOfNotNull(drawingState.currentPath)
        allPaths.forEach { pathData ->
            val points = if (pathData.shapePoints.isNotEmpty()) pathData.shapePoints else pathData.path
            points.forEach { offset ->
                minX = minOf(minX, offset.x); minY = minOf(minY, offset.y)
                maxX = maxOf(maxX, offset.x); maxY = maxOf(maxY, offset.y)
                hasContent = true
            }
        }

        val drawScope = CanvasDrawScope()
        drawScope.draw(
            density = Density(1f),
            layoutDirection = LayoutDirection.Ltr,
            canvas = skiaCanvas.asComposeCanvas(),
            size = androidx.compose.ui.geometry.Size(width.toFloat(), height.toFloat()),
        ) {
            if (hasContent) {
                val padding      = 50f
                val contentWidth  = maxX - minX
                val contentHeight = maxY - minY
                val scaleX = (width  - 2 * padding) / maxOf(contentWidth,  1f)
                val scaleY = (height - 2 * padding) / maxOf(contentHeight, 1f)
                val s = minOf(scaleX, scaleY)
                val offsetX = (width  - contentWidth  * s) / 2f - minX * s
                val offsetY = (height - contentHeight * s) / 2f - minY * s
                withTransform({ translate(offsetX, offsetY); scale(s, s, Offset.Zero) }) {
                    allPaths.forEach {
                        drawPath(it.path, it.color, it.thickness, it.pathEffect,
                            isEraser = it.isEraser, drawingState.backgroundColor,
                            it.shapeType, it.shapePoints)
                    }
                }
            }
        }

        return skiaToBufferedImage(skiaBitmap, width, height)
    }

    /** Converts a Skia RGBA bitmap to a Java [BufferedImage] ARGB. */
    private fun skiaToBufferedImage(skiaBitmap: Bitmap, width: Int, height: Int): BufferedImage {
        val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val bytes = skiaBitmap.readPixels(skiaBitmap.imageInfo, (width * 4), 0, 0)
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

    // ── Public API ────────────────────────────────────────────────────────────

    override fun shareDrawing(
        drawingState: DrawingState,
        fileName: String,
        isLegacy: Boolean,
        currentPageIndex: Int,
    ) {
        val file = saveToPictures(drawingState, fileName, isLegacy, currentPageIndex, showDialog = false)
        if (file != null && Desktop.isDesktopSupported()) {
            try { Desktop.getDesktop().open(file) } catch (e: Exception) { e.printStackTrace() }
        }
    }

    override fun saveDrawing(
        drawingState: DrawingState,
        fileName: String,
        isLegacy: Boolean,
        currentPageIndex: Int,
    ) {
        saveToPictures(drawingState, fileName, isLegacy, currentPageIndex, showDialog = true)
    }

    private fun saveToPictures(
        drawingState: DrawingState,
        fileName: String,
        isLegacy: Boolean,
        currentPageIndex: Int,
        showDialog: Boolean,
    ): File? {
        return try {
            val bufferedImage = generateBufferedImage(drawingState, isLegacy, currentPageIndex)

            val picturesDir = File(System.getProperty("user.home"), "Pictures${File.separator}Canvas")
            if (!picturesDir.exists()) picturesDir.mkdirs()

            val safeFileName = fileName.replace(Regex("""[\\/:*?"<>|]"""), "_")
            val file = File(picturesDir, "$safeFileName.png")
            ImageIO.write(bufferedImage, "png", file)

            if (showDialog) {
                JOptionPane.showMessageDialog(
                    null, "Image saved to: ${file.absolutePath}", "Saved", JOptionPane.INFORMATION_MESSAGE
                )
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            if (showDialog) {
                JOptionPane.showMessageDialog(
                    null, "Failed to save image: ${e.message}", "Error", JOptionPane.ERROR_MESSAGE
                )
            }
            null
        }
    }
}
