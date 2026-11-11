package com.rkbapps.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.rkbapps.canvas.model.CanvasFileError
import com.rkbapps.canvas.model.CanvasFileException
import com.rkbapps.canvas.model.CanvasPage
import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.model.PathData
import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.util.CanvasFileCodec
import com.rkbapps.canvas.util.readCanvasFile
import com.rkbapps.canvas.util.toCanvasFile
import com.rkbapps.canvas.util.unzipEntries
import com.rkbapps.canvas.util.zipEntries
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class CanvasFileZipTest {

    private val design = SavedDesign(
        name = "Sketch",
        state = DrawingState(
            pages = listOf(
                CanvasPage(
                    paths = listOf(
                        PathData(
                            id = "s",
                            color = Color.Black,
                            path = List(500) { Offset(it * 1.5f, it * 0.25f) },
                        )
                    )
                ),
                CanvasPage(),
            )
        ),
    )

    @Test
    fun aDrawingSurvivesBeingWrittenToAFileAndReadBack() {
        val file = design.toCanvasFile()
        val read = readCanvasFile(file)

        // A zip starts with "PK".
        assertEquals("PK", file.decodeToString(0, 2))
        assertEquals("Sketch", read.name)
        assertEquals(design.state.pages.map { it.paths }, read.state.pages.map { it.paths })
    }

    @Test
    fun entriesTheAppDoesNotKnowAreIgnored() {
        val file = zipEntries(
            CanvasFileCodec.encode(design, appVersion = "test") + ("thumbnail.png" to ByteArray(2048) { 7 })
        )

        assertEquals(2, readCanvasFile(file).state.pages.size)
    }

    @Test
    fun filesThatAreNotCanvasFilesAreRefused() {
        val notAZip = "just some text, long enough to look like a file".encodeToByteArray()
        val someoneElsesZip = zipEntries(mapOf("readme.txt" to "hello".encodeToByteArray()))
        val cutShort = design.toCanvasFile().let { it.copyOf(it.size / 2) }

        listOf(ByteArray(0), notAZip, someoneElsesZip, cutShort).forEach { bytes ->
            val error = assertFailsWith<CanvasFileException> { readCanvasFile(bytes) }.error
            assertEquals(CanvasFileError.NOT_A_CANVAS_FILE, error)
        }
    }

    @Test
    fun unpackingStopsAtTheSizeLimit() {
        // 4 MB of zeros packs into a few kilobytes.
        val bomb = zipEntries(mapOf("other.bin" to ByteArray(4 * 1024 * 1024)))
        assertTrue(bomb.size < 64 * 1024)

        assertFailsWith<IOException> { unzipEntries(bomb, wanted = setOf("manifest.json"), maxBytes = 1024 * 1024) }
    }

    @Test
    fun onlyTheWantedEntriesAreReturned() {
        val zip = zipEntries(mapOf("a" to byteArrayOf(1, 2), "b" to byteArrayOf(3)))
        val read = unzipEntries(zip, wanted = setOf("b"), maxBytes = 1024)

        assertEquals(setOf("b"), read.keys)
        assertContentEquals(byteArrayOf(3), read["b"])
    }
}
