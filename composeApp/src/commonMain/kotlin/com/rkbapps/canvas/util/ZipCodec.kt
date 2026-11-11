package com.rkbapps.canvas.util

/**
 * Packs [entries] (name → content) into a zip.
 *
 * Kotlin has no zip in common code, so this is expect/actual: `java.util.zip` on
 * **Android** and **JVM/Desktop**; **iOS/Native** does not support it yet and throws.
 */
expect fun zipEntries(entries: Map<String, ByteArray>): ByteArray

/**
 * Reads the [wanted] entries of a zip into memory; anything else in it is skipped.
 *
 * The zip comes from outside the app, so nothing is written to disk and the
 * unpacked size is limited to [maxBytes] in total — more than that throws.
 * Bytes that are not a zip give an empty map.
 */
expect fun unzipEntries(bytes: ByteArray, wanted: Set<String>, maxBytes: Int): Map<String, ByteArray>
