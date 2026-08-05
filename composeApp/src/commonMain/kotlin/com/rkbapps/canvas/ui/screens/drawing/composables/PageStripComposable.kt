package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.foundation.Canvas
import com.rkbapps.canvas.ui.composables.drawPath
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.rkbapps.canvas.model.CanvasPage
import com.rkbapps.canvas.ui.screens.drawing.DrawingAction

// ─────────────────────────────────────────────────────────────────────────────
// Page Strip — horizontal (mobile bottom) and vertical (desktop/tablet right)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Horizontal strip for mobile, placed at the bottom of the canvas area.
 * Shows thumbnails of each page; tapping a thumbnail switches to it.
 * A [+] button adds a new blank page.
 */
@Composable
fun HorizontalPageStrip(
    pages: List<CanvasPage>,
    currentPageIndex: Int,
    pageWidth: Float,
    pageHeight: Float,
    onAction: (DrawingAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.95f),
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            pages.forEachIndexed { index, page ->
                PageThumbnail(
                    page = page,
                    index = index,
                    isSelected = index == currentPageIndex,
                    pageWidth = pageWidth,
                    pageHeight = pageHeight,
                    canDelete = pages.size > 1,
                    onSelect = { onAction(DrawingAction.OnSelectPage(index)) },
                    onDelete = { onAction(DrawingAction.OnDeletePage(index)) },
                    orientation = ThumbnailOrientation.VERTICAL   // tall thumbnail
                )
            }
            // Add page button
            AddPageButton { onAction(DrawingAction.OnAddPage) }
        }
    }
}

/**
 * Vertical strip for desktop/tablet, placed on the right side of the canvas area.
 */
@Composable
fun VerticalPageStrip(
    pages: List<CanvasPage>,
    currentPageIndex: Int,
    pageWidth: Float,
    pageHeight: Float,
    onAction: (DrawingAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .widthIn(min = 80.dp, max = 96.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            pages.forEachIndexed { index, page ->
                PageThumbnail(
                    page = page,
                    index = index,
                    isSelected = index == currentPageIndex,
                    pageWidth = pageWidth,
                    pageHeight = pageHeight,
                    canDelete = pages.size > 1,
                    onSelect = { onAction(DrawingAction.OnSelectPage(index)) },
                    onDelete = { onAction(DrawingAction.OnDeletePage(index)) },
                    orientation = ThumbnailOrientation.PORTRAIT   // portrait thumbnail
                )
            }
            AddPageButton { onAction(DrawingAction.OnAddPage) }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Internal helpers
// ─────────────────────────────────────────────────────────────────────────────

private enum class ThumbnailOrientation { PORTRAIT, VERTICAL }

@Composable
private fun PageThumbnail(
    page: CanvasPage,
    index: Int,
    isSelected: Boolean,
    pageWidth: Float,
    pageHeight: Float,
    canDelete: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit,
    orientation: ThumbnailOrientation,
) {
    // Thumbnail target size
    val thumbW = if (orientation == ThumbnailOrientation.PORTRAIT) 64.dp else 56.dp
    val thumbH = if (orientation == ThumbnailOrientation.PORTRAIT) {
        (thumbW.value * (pageHeight / pageWidth)).dp
    } else {
        72.dp
    }

    val scaleFactor = thumbW.value / pageWidth

    Box {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Mini canvas drawing
            Box(
                modifier = Modifier
                    .size(thumbW, thumbH)
                    .shadow(2.dp, RoundedCornerShape(4.dp))
                    .clip(RoundedCornerShape(4.dp))
                    .border(
                        width = if (isSelected) 2.dp else 0.5.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .background(page.backgroundColor)
                    .clickable { onSelect() }
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    scale(scaleFactor, pivot = androidx.compose.ui.geometry.Offset.Zero) {
                        page.paths.fastForEach { pathData ->
                            drawPath(
                                path = pathData.path,
                                color = pathData.color,
                                thickness = pathData.thickness,
                                pathEffect = pathData.pathEffect,
                                isEraser = pathData.isEraser,
                                backgroundColor = page.backgroundColor,
                                shapeType = pathData.shapeType,
                                shapePoints = pathData.shapePoints
                            )
                        }
                    }
                }
            }

            // Page number label
            Text(
                text = "${index + 1}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Delete (×) button at top-right — only shown when there's more than 1 page
        if (canDelete) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(16.dp)
                    .background(MaterialTheme.colorScheme.errorContainer, CircleShape)
                    .clickable { onDelete() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete page",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(10.dp)
                )
            }
        }
    }
}

@Composable
private fun AddPageButton(onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline,
                    RoundedCornerShape(8.dp)
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add page",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = "Add",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Mobile Page Navigation Bar — compact floating bar with page counter,
// prev/next navigation, and add button
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Compact navigation bar for mobile page management.
 * Shows: [←] [Page 2 / 5] [→] [+]
 * Always visible for paged (non-legacy) drawings so users can add pages.
 */
@Composable
fun MobilePageNavigationBar(
    pages: List<CanvasPage>,
    currentPageIndex: Int,
    pageWidth: Float,
    pageHeight: Float,
    onAction: (DrawingAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.95f),
        tonalElevation = 6.dp,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(50)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous page
            IconButton(
                onClick = { onAction(DrawingAction.OnSelectPage(currentPageIndex - 1)) },
                enabled = currentPageIndex > 0
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous page",
                    tint = if (currentPageIndex > 0)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }

            // Page counter
            Text(
                text = "${currentPageIndex + 1} / ${pages.size}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.width(56.dp),
                textAlign = TextAlign.Center
            )

            // Next page
            IconButton(
                onClick = { onAction(DrawingAction.OnSelectPage(currentPageIndex + 1)) },
                enabled = currentPageIndex < pages.size - 1
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next page",
                    tint = if (currentPageIndex < pages.size - 1)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }

            // Spacer between navigation and add
            Spacer(Modifier.width(4.dp))

            // Add page button
            IconButton(
                onClick = { onAction(DrawingAction.OnAddPage) }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add page",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Delete page button (only when more than 1 page)
            if (pages.size > 1) {
                IconButton(
                    onClick = { onAction(DrawingAction.OnDeletePage(currentPageIndex)) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Delete page",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tablet Page Navigation Header — sits above the VerticalPageStrip
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Navigation header for the tablet page strip.
 * Shows page counter with prev/next arrows for quick sequential page access.
 */
@Composable
fun TabletPageNavigationHeader(
    currentPageIndex: Int,
    totalPages: Int,
    onAction: (DrawingAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .widthIn(min = 80.dp, max = 96.dp)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Page label
        Text(
            text = "Pages",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )

        // Navigation row: [▲] [2/5] [▼]
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Previous page (up)
            IconButton(
                onClick = { onAction(DrawingAction.OnSelectPage(currentPageIndex - 1)) },
                enabled = currentPageIndex > 0,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Previous page",
                    modifier = Modifier.size(18.dp),
                    tint = if (currentPageIndex > 0)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }

            // Page counter
            Text(
                text = "${currentPageIndex + 1}/${totalPages}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            // Next page (down)
            IconButton(
                onClick = { onAction(DrawingAction.OnSelectPage(currentPageIndex + 1)) },
                enabled = currentPageIndex < totalPages - 1,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Next page",
                    modifier = Modifier.size(18.dp),
                    tint = if (currentPageIndex < totalPages - 1)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }
        }
    }
}
