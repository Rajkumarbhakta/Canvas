package com.rkbapps.canvas.util

/** Native (iOS) actual: Canvas files are not supported on iOS yet. */
actual fun zipEntries(entries: Map<String, ByteArray>): ByteArray =
    throw UnsupportedOperationException("Canvas files are not supported on iOS yet")

actual fun unzipEntries(bytes: ByteArray, wanted: Set<String>, maxBytes: Int): Map<String, ByteArray> =
    throw UnsupportedOperationException("Canvas files are not supported on iOS yet")
