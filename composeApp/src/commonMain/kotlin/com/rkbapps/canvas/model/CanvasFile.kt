package com.rkbapps.canvas.model

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.time.Instant

// ─────────────────────────────────────────────────────────────────────────────
// CANVAS FILE — one drawing as a file that can leave the app.
//
// A zip with its own extension, holding:
//
//   manifest.json   what the file is; checked before anything else is read
//   drawing.json    the pages and their strokes
//
// Only paged drawings are written. Legacy drawings are recorded in screen
// pixels and would not look the same on another device.
// ─────────────────────────────────────────────────────────────────────────────

const val CANVAS_FILE_EXTENSION = "rkcanvas"
const val CANVAS_FILE_MIME_TYPE = "application/x-rkcanvas"
const val CANVAS_FILE_FORMAT = "rkcanvas"

/** Raise this whenever a file written by the new code can no longer be read correctly by older apps. */
const val CANVAS_FILE_FORMAT_VERSION = 1

/** Largest file the app will try to open; anything bigger is not a drawing. */
const val MAX_CANVAS_FILE_BYTES = 32 * 1024 * 1024

/** Limit on the unpacked size, so a small file cannot expand into all of the memory. */
const val MAX_CANVAS_FILE_UNPACKED_BYTES = 128 * 1024 * 1024

@Serializable
data class CanvasFileManifest(
    // No defaults for these two: a manifest without them is not ours.
    val format: String,
    val formatVersion: Int,
    val appVersion: String = "",
    val name: String = "",
    val createdAt: Instant? = null,
    val pageWidth: Float = 794f,
    val pageHeight: Float = 1123f,
    val pageSizeLabel: String = "A4 Portrait",
    val pageCount: Int = 1,
)

@Serializable
data class CanvasFileDrawing(
    val pages: List<CanvasFilePage> = emptyList(),
)

@Serializable
data class CanvasFilePage(
    @Contextual
    val backgroundColor: Color = Color.White,
    val paths: List<PathData> = emptyList(),
)

/** Why a file could not be opened. */
enum class CanvasFileError {
    /** Not a zip, or a zip that was not written by this app. */
    NOT_A_CANVAS_FILE,
    /** Written by a newer version of the app, in a format this one does not know. */
    NEWER_VERSION,
    /** A Canvas file whose content cannot be read. */
    DAMAGED,
}

class CanvasFileException(
    val error: CanvasFileError,
    cause: Throwable? = null,
) : Exception(error.name, cause)

/** File name for a drawing called [name], safe on every file system. */
fun canvasFileName(name: String): String {
    val safe = name.replace(Regex("""[\\/:*?"<>|]"""), "_").trim().ifBlank { "Untitled drawing" }
    return "$safe.$CANVAS_FILE_EXTENSION"
}
