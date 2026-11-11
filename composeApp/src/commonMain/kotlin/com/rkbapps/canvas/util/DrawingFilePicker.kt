package com.rkbapps.canvas.util

import androidx.compose.runtime.Composable

/** Whether drawings can be exported to and imported from files on this platform. */
val isDrawingFileSupported: Boolean
    get() = getPlatform() != Platforms.IOS

/**
 * Returns a function that asks where to save a Canvas file and writes the bytes there.
 *
 * On **Android**: the system "save to" screen (no storage permission needed).
 * On **JVM/Desktop**: the operating system's save dialog.
 * On **iOS/Native**: not supported yet — a no-op.
 *
 * @param onResult Called with `true` once the file is written, `false` when it could not
 *                 be written. Not called when the user cancels.
 */
@Composable
expect fun rememberDrawingFileExporter(
    onResult: (saved: Boolean) -> Unit,
): (fileName: String, bytes: ByteArray) -> Unit

/**
 * Returns a function that lets the user pick a Canvas file to import.
 *
 * Platforms as for [rememberDrawingFileExporter].
 *
 * @param onPicked Called with the content of the picked file, or `null` when it could not
 *                 be read or is too large to be a drawing. Not called when the user cancels.
 */
@Composable
expect fun rememberDrawingFileImporter(
    onPicked: (bytes: ByteArray?) -> Unit,
): () -> Unit
