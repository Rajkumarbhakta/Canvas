package com.rkbapps.canvas.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import com.rkbapps.canvas.model.CANVAS_FILE_EXTENSION
import com.rkbapps.canvas.model.MAX_CANVAS_FILE_BYTES
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.io.FilenameFilter

private const val DOT_EXTENSION = ".$CANVAS_FILE_EXTENSION"

/** JVM actual: the operating system's save dialog. */
@Composable
actual fun rememberDrawingFileExporter(
    onResult: (saved: Boolean) -> Unit,
): (fileName: String, bytes: ByteArray) -> Unit {
    val scope = rememberCoroutineScope()
    val currentOnResult by rememberUpdatedState(onResult)

    return remember {
        { fileName, bytes ->
            val dialog = FileDialog(null as Frame?, "", FileDialog.SAVE).apply {
                file = fileName
                isVisible = true   // returns when the dialog is closed
            }
            val directory = dialog.directory
            val chosen = dialog.file
            if (directory != null && chosen != null) {
                val target = withCanvasExtension(File(directory, chosen))
                scope.launch {
                    val saved = withContext(Dispatchers.IO) {
                        try {
                            target.writeBytes(bytes)
                            true
                        } catch (e: Exception) {
                            e.printStackTrace()
                            false
                        }
                    }
                    currentOnResult(saved)
                }
            }
        }
    }
}

/**
 * [file] with the Canvas extension, so the app can find and open it again.
 *
 * The dialog only confirmed replacing the name as typed. When adding the extension
 * lands on another file that exists, a free name is used instead of replacing it.
 */
private fun withCanvasExtension(file: File): File {
    if (file.name.endsWith(DOT_EXTENSION, ignoreCase = true)) return file
    var target = File(file.parentFile, file.name + DOT_EXTENSION)
    var copy = 1
    while (target.exists()) {
        target = File(file.parentFile, "${file.name} (${copy++})$DOT_EXTENSION")
    }
    return target
}

/** JVM actual: the operating system's open dialog, showing Canvas files. */
@Composable
actual fun rememberDrawingFileImporter(
    onPicked: (bytes: ByteArray?) -> Unit,
): () -> Unit {
    val scope = rememberCoroutineScope()
    val currentOnPicked by rememberUpdatedState(onPicked)

    return remember {
        {
            val dialog = FileDialog(null as Frame?, "", FileDialog.LOAD).apply {
                // macOS and Linux use the filter; Windows ignores it and takes a name pattern.
                filenameFilter = FilenameFilter { _, name -> name.endsWith(DOT_EXTENSION, ignoreCase = true) }
                if (System.getProperty("os.name").orEmpty().startsWith("Windows")) file = "*$DOT_EXTENSION"
                isVisible = true   // returns when the dialog is closed
            }
            val directory = dialog.directory
            val chosen = dialog.file
            if (directory != null && chosen != null) {
                scope.launch {
                    currentOnPicked(withContext(Dispatchers.IO) { readDrawingFile(File(directory, chosen)) })
                }
            }
        }
    }
}

/**
 * The content of [file], or `null` when it cannot be read or is larger than a
 * drawing can be. Blocking — call it off the main thread.
 */
fun readDrawingFile(file: File): ByteArray? {
    return try {
        if (file.length() > MAX_CANVAS_FILE_BYTES) null else file.readBytes()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
