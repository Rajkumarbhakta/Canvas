package com.rkbapps.canvas

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.ui.screens.home.HomeContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

/** Where Import and Export appear on the home screen. */
@OptIn(ExperimentalTestApi::class)
class HomeFileActionsTest {

    // Newest first on screen: the paged drawing's card comes before the legacy one.
    private val paged = SavedDesign(
        id = "paged", name = "Paged", time = Instant.parse("2026-11-10T10:00:00Z"), state = DrawingState()
    )
    private val legacy = SavedDesign(
        id = "legacy", name = "Legacy", time = Instant.parse("2026-01-01T10:00:00Z"), state = DrawingState(), isLegacy = true
    )

    @Test
    fun importIsOfferedAndOnlyPagedDrawingsCanBeExported() = runComposeUiTest {
        var imports = 0
        val exported = mutableListOf<String>()
        setContent {
            MaterialTheme {
                HomeContent(
                    designs = listOf(paged, legacy),
                    isWide = true,
                    cachedPreview = { null },
                    loadPreview = { emptyList() },
                    onNewDrawing = {},
                    onOpen = {},
                    onDelete = {},
                    onSettings = {},
                    onImport = { imports++ },
                    onExport = { exported += it.id },
                )
            }
        }

        onNodeWithContentDescription("Import").performClick()
        assertEquals(1, imports)

        onAllNodesWithContentDescription("More options")[0].performClick()
        onNodeWithText("Export as file").performClick()
        assertEquals(listOf("paged"), exported)

        // The legacy card's menu has Delete but no Export.
        onAllNodesWithContentDescription("More options")[1].performClick()
        onNodeWithText("Delete").assertExists()
        onAllNodesWithText("Export as file").assertCountEquals(0)
    }

    @Test
    fun anEmptyGalleryOffersImport() = runComposeUiTest {
        var imports = 0
        setContent {
            MaterialTheme {
                HomeContent(
                    designs = emptyList(),
                    isWide = false,
                    cachedPreview = { null },
                    loadPreview = { emptyList() },
                    onNewDrawing = {},
                    onOpen = {},
                    onDelete = {},
                    onSettings = {},
                    onImport = { imports++ },
                )
            }
        }

        onNodeWithText("Import").performClick()
        assertEquals(1, imports)
    }

    @Test
    fun fileControlsAreLeftOutWhereFilesAreNotSupported() = runComposeUiTest {
        setContent {
            MaterialTheme {
                HomeContent(
                    designs = listOf(paged),
                    isWide = true,
                    cachedPreview = { null },
                    loadPreview = { emptyList() },
                    onNewDrawing = {},
                    onOpen = {},
                    onDelete = {},
                    onSettings = {},
                )
            }
        }

        onAllNodesWithContentDescription("Import").assertCountEquals(0)
        onAllNodesWithContentDescription("More options")[0].performClick()
        onAllNodesWithText("Export as file").assertCountEquals(0)
    }
}
