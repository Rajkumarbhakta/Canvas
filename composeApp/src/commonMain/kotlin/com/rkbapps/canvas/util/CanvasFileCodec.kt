package com.rkbapps.canvas.util

import androidx.compose.ui.geometry.Offset
import com.rkbapps.canvas.model.CANVAS_FILE_FORMAT
import com.rkbapps.canvas.model.CANVAS_FILE_FORMAT_VERSION
import com.rkbapps.canvas.model.CanvasFileDrawing
import com.rkbapps.canvas.model.CanvasFileError
import com.rkbapps.canvas.model.CanvasFileException
import com.rkbapps.canvas.model.CanvasFileManifest
import com.rkbapps.canvas.model.CanvasFilePage
import com.rkbapps.canvas.model.CanvasPage
import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.model.MAX_CANVAS_FILE_UNPACKED_BYTES
import com.rkbapps.canvas.model.PathData
import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.util.serializers.compactJson

/**
 * Turns a drawing into the entries of a Canvas file and back.
 *
 * Works on plain name → bytes entries, so the format can be tested without a zip
 * or a file system; [toCanvasFile] and [readCanvasFile] add the zip around it.
 */
object CanvasFileCodec {
    const val MANIFEST = "manifest.json"
    const val DRAWING = "drawing.json"

    /** Page sizes outside this range are not something the app could have written. */
    private val pageSideRange = 1f..100_000f

    /**
     * Only what makes up the drawing is written: pages, paper colours and strokes.
     * Undo history, tool settings and the local database id stay behind.
     */
    fun encode(design: SavedDesign, appVersion: String = getAppVersion()): Map<String, ByteArray> {
        val state = design.state
        val manifest = CanvasFileManifest(
            format = CANVAS_FILE_FORMAT,
            formatVersion = CANVAS_FILE_FORMAT_VERSION,
            appVersion = appVersion,
            name = design.name,
            createdAt = design.time,
            pageWidth = state.pageWidth,
            pageHeight = state.pageHeight,
            pageSizeLabel = state.pageSizeLabel,
            pageCount = state.pages.size,
        )
        val drawing = CanvasFileDrawing(
            pages = state.pages.map { CanvasFilePage(it.backgroundColor, it.paths) }
        )
        return mapOf(
            MANIFEST to compactJson.encodeToString(CanvasFileManifest.serializer(), manifest).encodeToByteArray(),
            DRAWING to compactJson.encodeToString(CanvasFileDrawing.serializer(), drawing).encodeToByteArray(),
        )
    }

    /**
     * Rebuilds a drawing from file entries, as a **new** drawing: it gets its own id
     * and today's date, so importing never replaces a drawing that is already here.
     *
     * @throws CanvasFileException when the entries are not a Canvas file this app can read.
     */
    fun decode(entries: Map<String, ByteArray>): SavedDesign {
        val manifest = try {
            val bytes = entries[MANIFEST] ?: throw CanvasFileException(CanvasFileError.NOT_A_CANVAS_FILE)
            compactJson.decodeFromString(CanvasFileManifest.serializer(), bytes.decodeToString())
        } catch (e: CanvasFileException) {
            throw e
        } catch (e: Exception) {
            throw CanvasFileException(CanvasFileError.NOT_A_CANVAS_FILE, e)
        }
        if (manifest.format != CANVAS_FILE_FORMAT || manifest.formatVersion < 1) {
            throw CanvasFileException(CanvasFileError.NOT_A_CANVAS_FILE)
        }
        // Checked before the drawing is read: a newer file may hold shapes or styles
        // this version has no name for.
        if (manifest.formatVersion > CANVAS_FILE_FORMAT_VERSION) {
            throw CanvasFileException(CanvasFileError.NEWER_VERSION)
        }
        if (manifest.pageWidth !in pageSideRange || manifest.pageHeight !in pageSideRange) {
            throw CanvasFileException(CanvasFileError.DAMAGED)
        }

        val drawing = try {
            val bytes = entries[DRAWING] ?: throw CanvasFileException(CanvasFileError.DAMAGED)
            compactJson.decodeFromString(CanvasFileDrawing.serializer(), bytes.decodeToString())
        } catch (e: CanvasFileException) {
            throw e
        } catch (e: Exception) {
            throw CanvasFileException(CanvasFileError.DAMAGED, e)
        }

        // A drawing always has at least one page.
        val pages = drawing.pages.ifEmpty { listOf(CanvasFilePage()) }.mapIndexed { index, page ->
            CanvasPage(
                id = "page_$index",
                backgroundColor = page.backgroundColor,
                paths = page.paths.map { it.sanitized() },
            )
        }
        return SavedDesign(
            name = manifest.name,
            isLegacy = false,
            state = DrawingState(
                pageWidth = manifest.pageWidth,
                pageHeight = manifest.pageHeight,
                pageSizeLabel = manifest.pageSizeLabel,
                pages = pages,
                // The database keeps one paper colour per drawing.
                backgroundColor = pages.first().backgroundColor,
            ),
        )
    }

    /** Values the renderer cannot draw (NaN, infinite, a zero width) are removed rather than trusted. */
    private fun PathData.sanitized(): PathData = copy(
        thickness = thickness.takeIf { it.isFinite() && it > 0f } ?: 10f,
        path = path.filter { it.isDrawable() },
        shapePoints = shapePoints.filter { it.isDrawable() },
    )

    private fun Offset.isDrawable(): Boolean = x.isFinite() && y.isFinite()
}

/** Packs a paged drawing as a Canvas file. */
fun SavedDesign.toCanvasFile(): ByteArray = zipEntries(CanvasFileCodec.encode(this))

/**
 * Reads a Canvas file into a new drawing (see [CanvasFileCodec.decode]).
 *
 * @throws CanvasFileException when [bytes] are not a Canvas file this app can read.
 */
fun readCanvasFile(bytes: ByteArray): SavedDesign {
    val entries = try {
        unzipEntries(
            bytes = bytes,
            wanted = setOf(CanvasFileCodec.MANIFEST, CanvasFileCodec.DRAWING),
            maxBytes = MAX_CANVAS_FILE_UNPACKED_BYTES,
        )
    } catch (e: Exception) {
        throw CanvasFileException(CanvasFileError.NOT_A_CANVAS_FILE, e)
    }
    return CanvasFileCodec.decode(entries)
}
