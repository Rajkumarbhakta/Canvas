package com.rkbapps.canvas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.rkbapps.canvas.model.PathData
import com.rkbapps.canvas.ui.composables.DrawingCanvas
import com.rkbapps.canvas.ui.composables.DrawingPageTestTag
import com.rkbapps.canvas.ui.composables.getBoundingBox
import com.rkbapps.canvas.ui.screens.drawing.DrawingAction
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The page must keep its own size and aspect ratio even when it is larger than
 * the area the canvas is given — it used to be clamped to that area, so changing
 * the page size had no visible effect.
 */
@OptIn(ExperimentalTestApi::class)
class DrawingCanvasPageSizeTest {

    @Test
    fun pageIsLaidOutAtItsOwnSizeAndFittedToTheDesk() = runComposeUiTest {
        var pageSize by mutableStateOf(794f to 1123f)
        setContent {
            // Deliberately smaller than every page preset.
            Box(Modifier.size(300.dp, 400.dp)) {
                DrawingCanvas(
                    paths = emptyList(),
                    currentPath = null,
                    backgroundColor = Color.White,
                    onAction = {},
                    pageWidth = pageSize.first,
                    pageHeight = pageSize.second,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        listOf(794f to 1123f, 1123f to 794f, 1587f to 1123f, 800f to 800f).forEach { (w, h) ->
            pageSize = w to h
            waitForIdle()

            val page = onNodeWithTag(DrawingPageTestTag).fetchSemanticsNode()
            assertEquals(IntSize(w.toInt(), h.toInt()), page.size, "page layout size")

            // On screen the whole page is scaled down to fit, keeping its aspect ratio.
            val (deskWidth, deskHeight) = with(density) { 300.dp.toPx() to 400.dp.toPx() }
            val shown = page.layoutInfo.coordinates.boundsInRoot()
            assertTrue(shown.width <= deskWidth && shown.height <= deskHeight, "page fits the desk: $shown")
            assertTrue(abs(shown.width / shown.height - w / h) < 0.01f, "aspect of $shown, want ${w / h}")
        }
    }

    @Test
    fun strokesAreRecordedInPageUnits() = runComposeUiTest {
        val drawn = mutableListOf<Offset>()
        setContent {
            Box(Modifier.size(300.dp, 400.dp)) {
                DrawingCanvas(
                    paths = emptyList(),
                    currentPath = null,
                    backgroundColor = Color.White,
                    onAction = { if (it is DrawingAction.OnDraw) drawn += it.offset },
                    pageWidth = 1123f,
                    pageHeight = 794f,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Drag across the page as it appears on screen, corner to corner.
        val shown = onNodeWithTag(DrawingPageTestTag).fetchSemanticsNode()
            .layoutInfo.coordinates.boundsInRoot()
        onRoot().performTouchInput {
            down(shown.topLeft + Offset(0.5f, 0.5f))
            moveTo(shown.center)
            moveTo(shown.bottomRight - Offset(0.5f, 0.5f))
            up()
        }

        // Allow a couple of screen px of slack, expressed in page units.
        val slack = 2f * 1123f / shown.width
        val first = drawn.first()
        val last = drawn.last()
        assertTrue(first.x < slack && first.y < slack, "stroke starts at the page origin, was $first")
        assertTrue(
            abs(last.x - 1123f) < slack && abs(last.y - 794f) < slack,
            "stroke ends at the page corner, was $last"
        )
    }

    @Test
    fun deleteHandleKeepsItsOnScreenHitAreaWhenThePageIsScaledDown() = runComposeUiTest {
        val actions = mutableListOf<DrawingAction>()
        val line = PathData(
            id = "line",
            color = Color.Black,
            path = listOf(Offset(300f, 300f), Offset(500f, 500f))
        )
        setContent {
            Box(Modifier.size(300.dp, 400.dp)) {
                DrawingCanvas(
                    paths = listOf(line),
                    currentPath = null,
                    backgroundColor = Color.White,
                    onAction = { actions += it },
                    isSelectionMode = true,
                    selectedPathId = line.id,
                    pageWidth = 794f,
                    pageHeight = 1123f,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        val shown = onNodeWithTag(DrawingPageTestTag).fetchSemanticsNode()
            .layoutInfo.coordinates.boundsInRoot()
        val scale = shown.width / 794f
        val box = line.getBoundingBox()!!
        val handleOnScreen = shown.topLeft + Offset(box.right, box.top) * scale

        // 20dp from the handle's centre on screen — far outside it in page units at this scale.
        onRoot().performTouchInput { click(handleOnScreen + Offset(20.dp.toPx(), 0f)) }

        assertEquals(listOf<DrawingAction>(DrawingAction.OnDeleteSelectedPath), actions)
    }
}
