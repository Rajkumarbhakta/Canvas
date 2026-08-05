package com.rkbapps.canvas.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toColorInt
import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.ui.composables.drawPath
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class ImageSharerAndroid(private val context: Context) : ImageSharer {

    // ── Internal rendering ────────────────────────────────────────────────────

    /**
     * Renders [drawingState] to an Android [Bitmap].
     *
     * **Paged mode** (`isLegacy = false`): renders only the active page's paths at exactly
     * [DrawingState.pageWidth] × [DrawingState.pageHeight] dp at 2× scale (retina quality).
     *
     * **Legacy mode** (`isLegacy = true`): original bounding-box approach using flat
     * [DrawingState.paths] in a 1080×1920 canvas — unchanged from before.
     */
    private fun generateBitmap(
        drawingState: DrawingState,
        isLegacy: Boolean,
        currentPageIndex: Int,
    ): Bitmap {
        return if (isLegacy) {
            generateLegacyBitmap(drawingState)
        } else {
            generatePagedBitmap(drawingState, currentPageIndex)
        }
    }

    /** Paged mode: render the active page at 2× the page dimensions. */
    private fun generatePagedBitmap(
        drawingState: DrawingState,
        currentPageIndex: Int,
    ): Bitmap {
        val page = drawingState.pages.getOrNull(currentPageIndex)
            ?: return createBitmap(1, 1)

        val exportScale = 2f   // 2× for crisp output on HiDPI/retina displays
        val width  = (drawingState.pageWidth  * exportScale).toInt()
        val height = (drawingState.pageHeight * exportScale).toInt()

        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)

        val bg = page.backgroundColor
        canvas.drawColor(
            android.graphics.Color.argb(
                (bg.alpha * 255).toInt(),
                (bg.red   * 255).toInt(),
                (bg.green * 255).toInt(),
                (bg.blue  * 255).toInt(),
            )
        )

        val drawScope = CanvasDrawScope()
        drawScope.draw(
            density = Density(context),
            layoutDirection = LayoutDirection.Ltr,
            canvas = androidx.compose.ui.graphics.Canvas(canvas),
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

        return bitmap
    }

    /** Legacy mode: original bounding-box behaviour, unchanged. */
    private fun generateLegacyBitmap(drawingState: DrawingState): Bitmap {
        val width  = 1080
        val height = 1920

        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)

        canvas.drawColor(
            android.graphics.Color.argb(
                (drawingState.backgroundColor.alpha * 255).toInt(),
                (drawingState.backgroundColor.red   * 255).toInt(),
                (drawingState.backgroundColor.green * 255).toInt(),
                (drawingState.backgroundColor.blue  * 255).toInt(),
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
            density = Density(context),
            layoutDirection = LayoutDirection.Ltr,
            canvas = androidx.compose.ui.graphics.Canvas(canvas),
            size = androidx.compose.ui.geometry.Size(width.toFloat(), height.toFloat()),
        ) {
            if (hasContent) {
                val padding       = 50f
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

        return bitmap
    }

    // ── Public API ────────────────────────────────────────────────────────────

    override fun shareDrawing(
        drawingState: DrawingState,
        fileName: String,
        isLegacy: Boolean,
        currentPageIndex: Int,
    ) {
        val bitmap = generateBitmap(drawingState, isLegacy, currentPageIndex)
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val stream = FileOutputStream("$cachePath/drawing.png")
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            val imageFile  = File(cachePath, "drawing.png")
            val contentUri = FileProvider.getUriForFile(
                context, "${context.packageName}.fileprovider", imageFile
            )

            if (contentUri != null) {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    setDataAndType(contentUri, context.contentResolver.getType(contentUri))
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    type = "image/png"
                }
                val chooser = Intent.createChooser(shareIntent, "Share Drawing").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(chooser)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun saveDrawing(
        drawingState: DrawingState,
        fileName: String,
        isLegacy: Boolean,
        currentPageIndex: Int,
    ) {
        val bitmap        = generateBitmap(drawingState, isLegacy, currentPageIndex)
        val imageFileName = "$fileName.png"
        var fos: OutputStream? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver      = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME,  imageFileName)
                    put(MediaStore.MediaColumns.MIME_TYPE,     "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + File.separator + "Canvas")
                }
                val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (imageUri != null) fos = resolver.openOutputStream(imageUri)
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
                val imageFile = File(imagesDir, imageFileName)
                fos = FileOutputStream(imageFile)
            }

            fos?.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                Toast.makeText(context, "Saved to Pictures/Canvas", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    }
}
