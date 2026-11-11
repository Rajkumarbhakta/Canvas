package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import canvas.composeapp.generated.resources.Res
import canvas.composeapp.generated.resources.add_page
import canvas.composeapp.generated.resources.delete_page
import canvas.composeapp.generated.resources.next_page
import canvas.composeapp.generated.resources.page_number
import canvas.composeapp.generated.resources.pages
import canvas.composeapp.generated.resources.previous_page
import com.rkbapps.canvas.model.CanvasPage
import com.rkbapps.canvas.ui.composables.drawPath
import com.rkbapps.canvas.ui.screens.drawing.DrawingAction
import org.jetbrains.compose.resources.stringResource

// ─────────────────────────────────────────────────────────────────────────────
// Pages — thumbnail panel for wide screens, compact stepper for phones.
// Deleting always goes through [onRequestDelete] so the caller can confirm it.
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Thumbnails of every page, with add and delete in the header. Delete acts on
 * the current page rather than sitting on each thumbnail, where it is easy to
 * hit by accident.
 */
@Composable
fun PagesPanel(
    pages: List<CanvasPage>,
    currentPageIndex: Int,
    pageWidth: Float,
    pageHeight: Float,
    onAction: (DrawingAction) -> Unit,
    onRequestDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val gridState = rememberLazyGridState()
    // Keep the current page in view when it changes (new page, keyboard, delete).
    LaunchedEffect(currentPageIndex, pages.size) {
        gridState.animateScrollToItem(currentPageIndex.coerceIn(0, (pages.size - 1).coerceAtLeast(0)))
    }

    Section(
        title = "${stringResource(Res.string.pages)}  ${currentPageIndex + 1} / ${pages.size}",
        modifier = modifier,
        trailing = {
            Row {
                ChromeButton(
                    label = stringResource(Res.string.delete_page),
                    enabled = pages.size > 1,
                    size = 32.dp,
                    onClick = onRequestDelete
                ) { Icon(Icons.Outlined.Delete, null, Modifier.size(18.dp)) }
                ChromeButton(
                    label = stringResource(Res.string.add_page),
                    size = 32.dp,
                    onClick = { onAction(DrawingAction.OnAddPage) }
                ) { Icon(Icons.Rounded.Add, null, Modifier.size(20.dp)) }
            }
        }
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            state = gridState,
            modifier = Modifier.fillMaxWidth().heightIn(max = 232.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            itemsIndexed(pages) { index, page ->
                PageThumbnail(
                    page = page,
                    number = index + 1,
                    isSelected = index == currentPageIndex,
                    pageWidth = pageWidth,
                    pageHeight = pageHeight,
                    onSelect = { onAction(DrawingAction.OnSelectPage(index)) }
                )
            }
        }
    }
}

@Composable
private fun PageThumbnail(
    page: CanvasPage,
    number: Int,
    isSelected: Boolean,
    pageWidth: Float,
    pageHeight: Float,
    onSelect: () -> Unit,
) {
    val label = stringResource(Res.string.page_number, number)
    val shape = RoundedCornerShape(4.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Canvas(
            Modifier
                .fillMaxWidth()
                .aspectRatio(pageWidth / pageHeight)
                .clip(shape)
                .background(page.backgroundColor)
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant,
                    shape = shape
                )
                .clickable(role = Role.Tab, onClick = onSelect)
                .semantics {
                    contentDescription = label
                    selected = isSelected
                }
        ) {
            // Strokes are in page units; map the page width onto the thumbnail's px width.
            scale(size.width / pageWidth, pivot = Offset.Zero) {
                page.paths.fastForEach { pathData ->
                    drawPath(
                        path = pathData.path,
                        color = pathData.color,
                        thickness = pathData.thickness,
                        pathEffect = pathData.pathEffect,
                        isEraser = pathData.isEraser,
                        backgroundColor = page.backgroundColor,
                        shapeType = pathData.shapeType,
                        shapePoints = pathData.shapePoints,
                        prebuiltPath = pathData.renderPath()
                    )
                }
            }
        }
        Text(
            text = "$number",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Previous, "2 / 5", next, add — the whole of page navigation in one small bar. */
@Composable
fun PageStepper(
    currentPageIndex: Int,
    pageCount: Int,
    onAction: (DrawingAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    FloatingBar(modifier) {
        Row(Modifier.padding(2.dp), verticalAlignment = Alignment.CenterVertically) {
            ChromeButton(
                label = stringResource(Res.string.previous_page),
                enabled = currentPageIndex > 0,
                size = 36.dp,
                onClick = { onAction(DrawingAction.OnSelectPage(currentPageIndex - 1)) }
            ) { Icon(Icons.AutoMirrored.Rounded.KeyboardArrowLeft, null) }
            Text(
                text = "${currentPageIndex + 1} / $pageCount",
                modifier = Modifier.widthIn(min = 44.dp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            ChromeButton(
                label = stringResource(Res.string.next_page),
                enabled = currentPageIndex < pageCount - 1,
                size = 36.dp,
                onClick = { onAction(DrawingAction.OnSelectPage(currentPageIndex + 1)) }
            ) { Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null) }
            BarDivider()
            ChromeButton(
                label = stringResource(Res.string.add_page),
                size = 36.dp,
                onClick = { onAction(DrawingAction.OnAddPage) }
            ) { Icon(Icons.Rounded.Add, null, Modifier.size(20.dp)) }
        }
    }
}
