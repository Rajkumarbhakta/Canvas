package com.rkbapps.canvas.util

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/** Android actual: `java.util.zip`. */
actual fun zipEntries(entries: Map<String, ByteArray>): ByteArray {
    val out = ByteArrayOutputStream()
    ZipOutputStream(out).use { zip ->
        entries.forEach { (name, content) ->
            zip.putNextEntry(ZipEntry(name))
            zip.write(content)
            zip.closeEntry()
        }
    }
    return out.toByteArray()
}

actual fun unzipEntries(bytes: ByteArray, wanted: Set<String>, maxBytes: Int): Map<String, ByteArray> {
    val found = mutableMapOf<String, ByteArray>()
    val buffer = ByteArray(8 * 1024)
    var total = 0L
    ZipInputStream(ByteArrayInputStream(bytes)).use { zip ->
        while (true) {
            val entry = zip.nextEntry ?: break
            // Skipped entries are still read and counted, so their unpacked size is limited too.
            val keep = !entry.isDirectory && entry.name in wanted && entry.name !in found
            val content = ByteArrayOutputStream()
            while (true) {
                val read = zip.read(buffer)
                if (read < 0) break
                total += read
                if (total > maxBytes) throw IOException("Zip content is larger than $maxBytes bytes")
                if (keep) content.write(buffer, 0, read)
            }
            if (keep) found[entry.name] = content.toByteArray()
        }
    }
    return found
}
