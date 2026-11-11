package com.rkbapps.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.rkbapps.canvas.db.AppDatabase
import com.rkbapps.canvas.db.utils.toDomain
import com.rkbapps.canvas.db.utils.toEntity
import com.rkbapps.canvas.db.utils.toPathEntities
import com.rkbapps.canvas.model.CanvasFileError
import com.rkbapps.canvas.model.CanvasPage
import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.model.PathData
import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.ui.screens.drawing.utils.ShapeType
import com.rkbapps.canvas.ui.screens.home.HomeMessage
import com.rkbapps.canvas.ui.screens.home.HomeRepository
import com.rkbapps.canvas.util.IncomingFiles
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/** Export and import through the home repository, against a real (in-memory) database. */
class CanvasFileImportTest {

    private val database = Room.inMemoryDatabaseBuilder<AppDatabase>()
        .setDriver(BundledSQLiteDriver())
        .build()
    private val dao = database.drawingDao()
    private val repository = HomeRepository(dao, IncomingFiles())

    @AfterTest
    fun closeDatabase() = database.close()

    private val cream = Color(0xFFFFF8E1)
    private val stroke = PathData(
        id = "stroke",
        color = Color(0xFF1E88E5),
        thickness = 5f,
        path = listOf(Offset(12.5f, 40f), Offset(600f, 720.25f)),
    )
    private val shape = PathData(
        id = "shape",
        color = Color.Red,
        shapeType = ShapeType.HEXAGON,
        shapePoints = listOf(Offset(100f, 100f), Offset(300f, 300f)),
    )

    private suspend fun save(design: SavedDesign) {
        dao.upsertDesignWithPaths(design.toEntity(), design.toPathEntities())
    }

    private suspend fun load(id: String): SavedDesign =
        assertNotNull(dao.getDesignWithPathsByStringId(id)).toDomain()

    @Test
    fun anExportedDrawingImportsAsACopyNextToTheOriginal() = runTest {
        val original = SavedDesign(
            id = "original",
            name = "Floor plan",
            state = DrawingState(
                pageWidth = 1123f,
                pageHeight = 794f,
                pageSizeLabel = "A4 Landscape",
                backgroundColor = cream,
                pages = listOf(
                    CanvasPage(id = "1", backgroundColor = cream, paths = listOf(stroke, shape)),
                    CanvasPage(id = "2", backgroundColor = cream),
                    CanvasPage(id = "3", backgroundColor = cream, paths = listOf(shape)),
                    CanvasPage(id = "4", backgroundColor = cream),
                ),
            ),
        )
        save(original)

        val file = assertNotNull(repository.buildCanvasFile("original"))
        val message = assertIs<HomeMessage.Imported>(repository.importDesign(file))

        assertEquals(2, dao.getAllDesigns().first().size)
        assertNotEquals("original", message.design.id)

        val saved = load("original")
        val copy = load(message.design.id)
        assertEquals("Floor plan", copy.name)
        assertEquals(1123f to 794f, copy.state.pageWidth to copy.state.pageHeight)
        assertEquals("A4 Landscape", copy.state.pageSizeLabel)
        assertEquals(cream, copy.state.backgroundColor)
        // Blank pages in the middle and at the end are still there.
        assertEquals(4, copy.state.pages.size)
        assertEquals(saved.state.pages.map { it.paths }, copy.state.pages.map { it.paths })
        // The original is untouched.
        assertEquals(original.state.pages.map { it.paths }, saved.state.pages.map { it.paths })
    }

    @Test
    fun importingTheSameFileTwiceGivesTwoDrawings() = runTest {
        save(SavedDesign(id = "original", name = "Twice", state = DrawingState()))
        val file = assertNotNull(repository.buildCanvasFile("original"))

        repository.importDesign(file)
        repository.importDesign(file)

        assertEquals(3, dao.getAllDesigns().first().size)
    }

    @Test
    fun legacyAndMissingDrawingsAreNotExported() = runTest {
        save(SavedDesign(id = "legacy", name = "Old", isLegacy = true, state = DrawingState(paths = listOf(stroke))))

        assertNull(repository.buildCanvasFile("legacy"))
        assertNull(repository.buildCanvasFile("no-such-drawing"))
    }

    @Test
    fun aFileThatIsNotADrawingAddsNothing() = runTest {
        val unreadable = assertIs<HomeMessage.ImportFailed>(repository.importDesign(null))
        val foreign = assertIs<HomeMessage.ImportFailed>(repository.importDesign("not a drawing".encodeToByteArray()))

        assertEquals(CanvasFileError.NOT_A_CANVAS_FILE, unreadable.error)
        assertEquals(CanvasFileError.NOT_A_CANVAS_FILE, foreign.error)
        assertEquals(0, dao.getAllDesigns().first().size)
    }
}
