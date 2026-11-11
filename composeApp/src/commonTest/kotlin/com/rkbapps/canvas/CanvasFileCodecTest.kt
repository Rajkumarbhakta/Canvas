package com.rkbapps.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.rkbapps.canvas.model.CANVAS_FILE_FORMAT_VERSION
import com.rkbapps.canvas.model.CanvasFileError
import com.rkbapps.canvas.model.CanvasFileException
import com.rkbapps.canvas.model.CanvasPage
import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.model.PathData
import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.model.canvasFileName
import com.rkbapps.canvas.ui.screens.drawing.utils.PaintingStyleType
import com.rkbapps.canvas.ui.screens.drawing.utils.ShapeType
import com.rkbapps.canvas.util.CanvasFileCodec
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class CanvasFileCodecTest {

    private val stroke = PathData(
        id = "stroke-1",
        color = Color(0xFF1E88E5),
        thickness = 5f,
        pathEffect = PaintingStyleType.PENCIL,
        path = listOf(Offset(10.5f, 20.25f), Offset(300.125f, 1100f), Offset(-4f, 0.1f)),
    )
    private val shape = PathData(
        id = "shape-1",
        color = Color(0x80FF0000),
        thickness = 2f,
        shapeType = ShapeType.STAR,
        shapePoints = listOf(Offset(100f, 100f), Offset(400f, 380f)),
    )
    private val eraser = PathData(
        id = "eraser-1",
        color = Color.White,
        thickness = 40f,
        path = listOf(Offset(0f, 0f), Offset(50f, 50f)),
        isEraser = true,
    )

    private val design = SavedDesign(
        pId = 42L,
        id = "original-id",
        name = "Field notes",
        state = DrawingState(
            pageWidth = 1123f,
            pageHeight = 794f,
            pageSizeLabel = "A4 Landscape",
            pages = listOf(
                CanvasPage(id = "a", backgroundColor = Color(0xFFFFF8E1), paths = listOf(stroke, shape)),
                CanvasPage(id = "b", backgroundColor = Color(0xFFFFF8E1), paths = listOf(eraser)),
                // A blank last page has to survive too.
                CanvasPage(id = "c", backgroundColor = Color(0xFFFFF8E1)),
            ),
        ),
    )

    private fun encode(design: SavedDesign = this.design) = CanvasFileCodec.encode(design, appVersion = "test")

    private fun Map<String, ByteArray>.with(name: String, edit: (String) -> String) =
        this + (name to edit(getValue(name).decodeToString()).encodeToByteArray())

    private fun errorOf(entries: Map<String, ByteArray>): CanvasFileError =
        assertFailsWith<CanvasFileException> { CanvasFileCodec.decode(entries) }.error

    @Test
    fun aDrawingComesBackWithTheSamePagesPaperAndStrokes() {
        val read = CanvasFileCodec.decode(encode())

        assertEquals("Field notes", read.name)
        assertEquals(1123f, read.state.pageWidth)
        assertEquals(794f, read.state.pageHeight)
        assertEquals("A4 Landscape", read.state.pageSizeLabel)
        assertFalse(read.isLegacy)
        assertEquals(design.state.pages.map { it.paths }, read.state.pages.map { it.paths })
        assertEquals(design.state.pages.map { it.backgroundColor }, read.state.pages.map { it.backgroundColor })
        assertEquals(Color(0xFFFFF8E1), read.state.backgroundColor)
    }

    @Test
    fun anImportedDrawingIsANewDrawing() {
        val read = CanvasFileCodec.decode(encode())

        // Not the exported drawing's row or id, so importing can never overwrite it.
        assertEquals(0L, read.pId)
        assertNotEquals("original-id", read.id)
    }

    @Test
    fun historyAndToolSettingsStayOutOfTheFile() {
        val busy = design.copy(
            state = design.state.copy(
                selectedThickness = 33f,
                pages = design.state.pages.map {
                    it.copy(undoStack = listOf(listOf(stroke)), redoStack = listOf(listOf(shape)))
                },
            )
        )
        val text = encode(busy).values.joinToString("\n") { it.decodeToString() }

        assertFalse("undoStack" in text)
        assertFalse("redoStack" in text)
        assertFalse("selectedThickness" in text)
        assertTrue(CanvasFileCodec.decode(encode(busy)).state.pages.all { it.undoStack.isEmpty() })
    }

    @Test
    fun aFileFromANewerFormatIsRefusedBeforeItsDrawingIsRead() {
        val newer = encode()
            .with(CanvasFileCodec.MANIFEST) {
                it.replace("\"formatVersion\":$CANVAS_FILE_FORMAT_VERSION", "\"formatVersion\":${CANVAS_FILE_FORMAT_VERSION + 1}")
            }
            // What a newer version might write; must not be reported as damage.
            .with(CanvasFileCodec.DRAWING) { it.replace("STAR", "SPIRAL") }

        assertEquals(CanvasFileError.NEWER_VERSION, errorOf(newer))
    }

    @Test
    fun contentThatIsNotOursIsRefused() {
        assertEquals(CanvasFileError.NOT_A_CANVAS_FILE, errorOf(emptyMap()))
        assertEquals(
            CanvasFileError.NOT_A_CANVAS_FILE,
            errorOf(mapOf(CanvasFileCodec.MANIFEST to """{"name":"someone else's zip"}""".encodeToByteArray()))
        )
        assertEquals(
            CanvasFileError.NOT_A_CANVAS_FILE,
            errorOf(encode().with(CanvasFileCodec.MANIFEST) { it.replace("\"format\":\"rkcanvas\"", "\"format\":\"other\"") })
        )
    }

    @Test
    fun aCanvasFileWithUnreadableContentIsReportedAsDamaged() {
        assertEquals(CanvasFileError.DAMAGED, errorOf(encode() - CanvasFileCodec.DRAWING))
        assertEquals(CanvasFileError.DAMAGED, errorOf(encode().with(CanvasFileCodec.DRAWING) { it.dropLast(20) }))
        assertEquals(CanvasFileError.DAMAGED, errorOf(encode().with(CanvasFileCodec.DRAWING) { it.replace("STAR", "SPIRAL") }))
        assertEquals(
            CanvasFileError.DAMAGED,
            errorOf(encode().with(CanvasFileCodec.MANIFEST) { it.replace("\"pageWidth\":1123.0", "\"pageWidth\":0.0") })
        )
    }

    @Test
    fun valuesTheRendererCannotDrawAreDropped() {
        val odd = encode().with(CanvasFileCodec.DRAWING) {
            it.replace("10.5,20.25;", "NaN,20.25;Infinity,1.0;").replace("\"thickness\":5.0", "\"thickness\":-1.0")
        }
        val path = CanvasFileCodec.decode(odd).state.pages.first().paths.first()

        assertEquals(listOf(Offset(300.125f, 1100f), Offset(-4f, 0.1f)), path.path)
        assertEquals(10f, path.thickness)
    }

    @Test
    fun aFileWithNoPagesOpensAsOneBlankPage() {
        val empty = encode().with(CanvasFileCodec.DRAWING) { """{"pages":[]}""" }

        assertEquals(1, CanvasFileCodec.decode(empty).state.pages.size)
    }

    @Test
    fun fileNamesAreSafeOnEveryFileSystem() {
        assertEquals("Field notes.rkcanvas", canvasFileName("Field notes"))
        assertEquals("a_b_c.rkcanvas", canvasFileName("a/b:c"))
        assertEquals("Untitled drawing.rkcanvas", canvasFileName("  "))
    }
}
