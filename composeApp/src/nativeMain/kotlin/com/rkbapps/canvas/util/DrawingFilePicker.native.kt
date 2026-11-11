package com.rkbapps.canvas.util

import androidx.compose.runtime.Composable

/** Native (iOS) actual: Canvas files are not supported on iOS yet — no-op. */
@Composable
actual fun rememberDrawingFileExporter(
    onResult: (saved: Boolean) -> Unit,
): (fileName: String, bytes: ByteArray) -> Unit = { _, _ -> }

/** Native (iOS) actual: Canvas files are not supported on iOS yet — no-op. */
@Composable
actual fun rememberDrawingFileImporter(
    onPicked: (bytes: ByteArray?) -> Unit,
): () -> Unit = {}
