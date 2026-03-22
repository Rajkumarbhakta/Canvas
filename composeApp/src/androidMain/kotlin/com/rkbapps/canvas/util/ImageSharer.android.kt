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
import androidx.core.content.FileProvider
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.ui.composables.drawPath
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import androidx.core.graphics.createBitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.withTransform

class ImageSharerAndroid(private val context: Context): ImageSharer {

    private fun generateBitmap(drawingState: DrawingState): Bitmap {
        val width = 1080
        val height = 1920
        
        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)
        
        // Draw background
        canvas.drawColor(android.graphics.Color.argb(
            (drawingState.backgroundColor.alpha * 255).toInt(),
            (drawingState.backgroundColor.red * 255).toInt(),
            (drawingState.backgroundColor.green * 255).toInt(),
            (drawingState.backgroundColor.blue * 255).toInt()
        ))
        
        val drawScope = CanvasDrawScope()
        val targetSize = androidx.compose.ui.geometry.Size(width.toFloat(), height.toFloat())
        
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

        drawScope.draw(
            density = Density(context),
            layoutDirection = LayoutDirection.Ltr,
            canvas = androidx.compose.ui.graphics.Canvas(canvas),
            size = targetSize
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
        return bitmap
    }

    override fun shareDrawing(drawingState: DrawingState, fileName: String) {
        val bitmap = generateBitmap(drawingState)
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val stream = FileOutputStream("$cachePath/drawing.png")
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            val imageFile = File(cachePath, "drawing.png")
            val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)

            if (contentUri != null) {
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                shareIntent.setDataAndType(contentUri, context.contentResolver.getType(contentUri))
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
                shareIntent.type = "image/png"
                
                val chooser = Intent.createChooser(shareIntent, "Share Drawing")
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooser)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun saveDrawing(drawingState: DrawingState, fileName: String) {
        val bitmap = generateBitmap(drawingState)
        val imageFileName = "$fileName.png"
        var fos: OutputStream? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "Canvas")
                }
                val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (imageUri != null) {
                    fos = resolver.openOutputStream(imageUri)
                }
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
