package com.rkbapps.canvas.util

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import com.rkbapps.canvas.model.MAX_CANVAS_FILE_BYTES
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

/** Android actual: the system "save to" screen, which also reaches Drive and other providers. */
@Composable
actual fun rememberDrawingFileExporter(
    onResult: (saved: Boolean) -> Unit,
): (fileName: String, bytes: ByteArray) -> Unit {
    val context = LocalContext.current.applicationContext
    val scope = rememberCoroutineScope()
    val currentOnResult by rememberUpdatedState(onResult)
    // The file waits here while the system screen is open.
    val pending = remember { mutableStateOf<ByteArray?>(null) }

    // Android knows no type for the extension, so the file is saved as plain binary data.
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        val bytes = pending.value
        pending.value = null
        if (uri != null && bytes != null) {
            scope.launch {
                val saved = withContext(Dispatchers.IO) {
                    try {
                        context.contentResolver.openOutputStream(uri)?.use { it.write(bytes) } != null
                    } catch (e: Exception) {
                        e.printStackTrace()
                        false
                    }
                }
                currentOnResult(saved)
            }
        }
    }

    return remember(launcher) {
        { fileName, bytes ->
            pending.value = bytes
            try {
                launcher.launch(fileName)
            } catch (e: Exception) {
                // No app on the device can show the "save to" screen.
                e.printStackTrace()
                pending.value = null
                currentOnResult(false)
            }
        }
    }
}

/** Android actual: the system file picker. */
@Composable
actual fun rememberDrawingFileImporter(
    onPicked: (bytes: ByteArray?) -> Unit,
): () -> Unit {
    val context = LocalContext.current.applicationContext
    val scope = rememberCoroutineScope()
    val currentOnPicked by rememberUpdatedState(onPicked)

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            scope.launch {
                currentOnPicked(withContext(Dispatchers.IO) { context.readDrawingFile(uri) })
            }
        }
    }

    return remember(launcher) {
        {
            try {
                // The picker cannot filter by extension; what was picked is checked when it is read.
                launcher.launch(arrayOf("*/*"))
            } catch (e: Exception) {
                e.printStackTrace()
                currentOnPicked(null)
            }
        }
    }
}

/**
 * The content behind [uri], or `null` when it cannot be read or is larger than a
 * drawing can be. Blocking — call it off the main thread.
 */
fun Context.readDrawingFile(uri: Uri): ByteArray? {
    return try {
        contentResolver.openInputStream(uri)?.use { input ->
            val content = ByteArrayOutputStream()
            val buffer = ByteArray(8 * 1024)
            while (true) {
                val read = input.read(buffer)
                if (read < 0) break
                if (content.size() + read > MAX_CANVAS_FILE_BYTES) return null
                content.write(buffer, 0, read)
            }
            content.toByteArray()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
