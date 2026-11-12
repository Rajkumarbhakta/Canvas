package com.rkbapps.canvas.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.icons.outlined.SaveAlt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import canvas.composeapp.generated.resources.Res
import canvas.composeapp.generated.resources.cancel
import canvas.composeapp.generated.resources.clear_search
import canvas.composeapp.generated.resources.confirm_deletion
import canvas.composeapp.generated.resources.delete
import canvas.composeapp.generated.resources.delete_confirmation
import canvas.composeapp.generated.resources.draw
import canvas.composeapp.generated.resources.drawing_imported
import canvas.composeapp.generated.resources.export_as_file
import canvas.composeapp.generated.resources.file_export_failed
import canvas.composeapp.generated.resources.file_exported
import canvas.composeapp.generated.resources.import_damaged
import canvas.composeapp.generated.resources.import_drawing
import canvas.composeapp.generated.resources.import_newer_version
import canvas.composeapp.generated.resources.import_not_canvas_file
import canvas.composeapp.generated.resources.migrating_data
import canvas.composeapp.generated.resources.months_short
import canvas.composeapp.generated.resources.more_options
import canvas.composeapp.generated.resources.my_designs
import canvas.composeapp.generated.resources.no_designs
import canvas.composeapp.generated.resources.no_designs_desc
import canvas.composeapp.generated.resources.no_search_results
import canvas.composeapp.generated.resources.no_search_results_desc
import canvas.composeapp.generated.resources.open
import canvas.composeapp.generated.resources.pages_count
import canvas.composeapp.generated.resources.please_wait
import canvas.composeapp.generated.resources.search_here
import canvas.composeapp.generated.resources.settings
import canvas.composeapp.generated.resources.short_date
import canvas.composeapp.generated.resources.today
import canvas.composeapp.generated.resources.untitled_drawing
import canvas.composeapp.generated.resources.yesterday
import com.rkbapps.canvas.model.CanvasFileError
import com.rkbapps.canvas.model.PathData
import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.model.canvasFileName
import com.rkbapps.canvas.navigation.Draw
import com.rkbapps.canvas.navigation.Settings
import com.rkbapps.canvas.ui.composables.drawPath
import com.rkbapps.canvas.ui.composables.getBoundingBox
import com.rkbapps.canvas.ui.screens.drawing.composables.ChromeButton
import com.rkbapps.canvas.ui.screens.drawing.composables.ChromeShape
import com.rkbapps.canvas.ui.screens.drawing.composables.MenuButton
import com.rkbapps.canvas.ui.screens.drawing.composables.MenuItem
import com.rkbapps.canvas.ui.screens.drawing.utils.PaintingStyleType
import com.rkbapps.canvas.util.Platforms
import com.rkbapps.canvas.util.getPlatform
import com.rkbapps.canvas.util.getWindowSize
import com.rkbapps.canvas.util.isDrawingFileSupported
import com.rkbapps.canvas.util.rememberDrawingFileExporter
import com.rkbapps.canvas.util.rememberDrawingFileImporter
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.PI
import kotlin.math.sin
import kotlin.time.Clock
import kotlin.time.Instant

// ─────────────────────────────────────────────────────────────────────────────
// HOME — the gallery of saved drawings.
//
// Every drawing is shown as its first page lying on the desk, so the grid is
// recognisable at a glance instead of being a wall of identical cards.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = koinViewModel()
) {
    val allDesign by viewModel.allDesign.collectAsStateWithLifecycle()
    val migrationStatus by viewModel.migrationState.collectAsStateWithLifecycle()
    val currentDeletableProject by viewModel.selectedProjectForDelete.collectAsStateWithLifecycle()

    if (migrationStatus.isLoading) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(stringResource(Res.string.please_wait)) },
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(Modifier.size(28.dp))
                    Text(stringResource(Res.string.migrating_data))
                }
            },
            confirmButton = {},
        )
    }

    currentDeletableProject?.let {
        DeleteConfirmationDialog(
            projectName = it.name,
            onCancel = { viewModel.selectDesignForDelete(null) }
        ) {
            viewModel.deleteDesign(it.pId)
        }
    }

    // ── Canvas files: import, export, and telling the user how it went ────────
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val importFile = rememberDrawingFileImporter(viewModel::importFile)
    val exportFile = rememberDrawingFileExporter(viewModel::onFileExported)
    val onExport: (SavedDesign) -> Unit = { design ->
        scope.launch {
            val file = viewModel.buildCanvasFile(design)
            if (file != null) exportFile(canvasFileName(design.name), file) else viewModel.onFileExported(false)
        }
    }

    val exported = stringResource(Res.string.file_exported)
    val exportFailed = stringResource(Res.string.file_export_failed)
    val imported = stringResource(Res.string.drawing_imported)
    val notCanvasFile = stringResource(Res.string.import_not_canvas_file)
    val newerVersion = stringResource(Res.string.import_newer_version)
    val damaged = stringResource(Res.string.import_damaged)
    val openLabel by rememberUpdatedState(stringResource(Res.string.open))
    val textOf by rememberUpdatedState { message: HomeMessage ->
        when (message) {
            HomeMessage.Exported -> exported
            HomeMessage.ExportFailed -> exportFailed
            is HomeMessage.Imported -> imported
            is HomeMessage.ImportFailed -> when (message.error) {
                CanvasFileError.NOT_A_CANVAS_FILE -> notCanvasFile
                CanvasFileError.NEWER_VERSION -> newerVersion
                CanvasFileError.DAMAGED -> damaged
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.messages.collect { message ->
            snackbarHostState.currentSnackbarData?.dismiss()
            launch {
                val importedDesign = (message as? HomeMessage.Imported)?.design
                val result = snackbarHostState.showSnackbar(
                    message = textOf(message),
                    // An imported drawing can be opened straight from the message.
                    actionLabel = openLabel.takeIf { importedDesign != null },
                    duration = if (message == HomeMessage.Exported) SnackbarDuration.Short else SnackbarDuration.Long
                )
                if (result == SnackbarResult.ActionPerformed && importedDesign != null) {
                    navController.navigate(route = Draw(importedDesign.id))
                }
            }
        }
    }

    HomeContent(
        designs = allDesign.designs,
        isWide = getWindowSize().widthSizeClass != WindowWidthSizeClass.Compact,
        snackbarHostState = snackbarHostState,
        onImport = importFile.takeIf { isDrawingFileSupported },
        onExport = onExport.takeIf { isDrawingFileSupported },
        cachedPreview = viewModel::cachedPreview,
        loadPreview = viewModel::loadPreview,
        onNewDrawing = { navController.navigate(route = Draw()) },
        onOpen = { navController.navigate(route = Draw(it.id)) },
        onDelete = viewModel::selectDesignForDelete,
        onSettings = {
            if (getPlatform() != Platforms.WEB) navController.navigate(route = Settings)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    designs: List<SavedDesign>,
    isWide: Boolean,
    cachedPreview: (Long) -> List<PathData>?,
    loadPreview: suspend (Long) -> List<PathData>,
    onNewDrawing: () -> Unit,
    onOpen: (SavedDesign) -> Unit,
    onDelete: (SavedDesign) -> Unit,
    onSettings: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    /** Null where Canvas files are not supported; the controls are then left out. */
    onImport: (() -> Unit)? = null,
    onExport: ((SavedDesign) -> Unit)? = null,
) {
    var query by rememberSaveable { mutableStateOf("") }
    // Newest first; the search matches anywhere in the name.
    val shown = remember(designs, query) {
        designs
            .filter { it.name.contains(query.trim(), ignoreCase = true) }
            .sortedByDescending { it.time }
    }
    val hasDesigns = designs.isNotEmpty()

    val gridState = rememberLazyGridState()
    val isAtTop by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex == 0 && gridState.firstVisibleItemScrollOffset == 0
        }
    }
    val edge = if (isWide) 32.dp else 16.dp

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.my_designs),
                        // Line the title up with the grid's left edge.
                        modifier = Modifier.padding(start = edge - 16.dp),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    if (isWide && hasDesigns) {
                        SearchField(query, onQueryChange = { query = it }, Modifier.width(280.dp))
                    }
                    if (onImport != null) {
                        ChromeButton(
                            label = stringResource(Res.string.import_drawing),
                            onClick = onImport,
                            modifier = Modifier.padding(start = 8.dp)
                        ) { Icon(Icons.Outlined.FileOpen, null) }
                    }
                    ChromeButton(
                        label = stringResource(Res.string.settings),
                        onClick = onSettings,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) { Icon(Icons.Outlined.Settings, null) }
                    if (isWide) {
                        Button(
                            onClick = onNewDrawing,
                            modifier = Modifier.padding(end = edge),
                            shape = ChromeShape
                        ) {
                            Icon(Icons.Rounded.Brush, null, Modifier.size(18.dp))
                            Text(stringResource(Res.string.draw), Modifier.padding(start = 8.dp))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            // On phones the primary action lives under the thumb.
            if (!isWide && hasDesigns) {
                ExtendedFloatingActionButton(
                    text = { Text(stringResource(Res.string.draw)) },
                    icon = { Icon(Icons.Rounded.Brush, null) },
                    onClick = onNewDrawing,
                    expanded = isAtTop
                )
            }
        }
    ) { innerPadding ->
        if (!hasDesigns) {
            EmptyGallery(onNewDrawing, onImport, Modifier.fillMaxSize().padding(innerPadding))
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = if (isWide) 208.dp else 152.dp),
                state = gridState,
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                // Extra room at the bottom so the last row clears the floating button.
                contentPadding = PaddingValues(start = edge, end = edge, top = 8.dp, bottom = 96.dp),
                horizontalArrangement = Arrangement.spacedBy(if (isWide) 24.dp else 16.dp),
                verticalArrangement = Arrangement.spacedBy(if (isWide) 24.dp else 20.dp)
            ) {
                if (!isWide) {
                    item(span = { GridItemSpan(maxLineSpan) }, key = "search") {
                        SearchField(
                            query, onQueryChange = { query = it },
                            Modifier.fillMaxWidth().padding(bottom = 4.dp)
                        )
                    }
                }
                if (shown.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }, key = "no-results") {
                        NoResults(query.trim(), Modifier.fillMaxWidth().padding(top = 64.dp))
                    }
                }
                items(items = shown, key = { it.id }) { design ->
                    DesignCard(
                        design = design,
                        cachedPreview = cachedPreview,
                        loadPreview = loadPreview,
                        onClick = { onOpen(design) },
                        onDelete = { onDelete(design) },
                        // Legacy drawings are in screen pixels and cannot be exported.
                        onExport = onExport?.takeIf { !design.isLegacy }?.let { export -> { export(design) } },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }
}

// ── Search ───────────────────────────────────────────────────────────────────

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorationBox = { field ->
            Row(
                modifier = Modifier
                    .height(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .padding(start = 14.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Rounded.Search, null,
                    Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                    if (query.isEmpty()) {
                        Text(
                            text = stringResource(Res.string.search_here),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    field()
                }
                if (query.isNotEmpty()) {
                    ChromeButton(
                        label = stringResource(Res.string.clear_search),
                        size = 36.dp,
                        onClick = { onQueryChange("") }
                    ) { Icon(Icons.Rounded.Close, null, Modifier.size(18.dp)) }
                }
            }
        }
    )
}

// ── Card ─────────────────────────────────────────────────────────────────────

private val TileShape = RoundedCornerShape(16.dp)
// Slightly wider than tall: portrait and landscape pages both get a fair share of the tile.
private const val TILE_ASPECT = 1.15f

@Composable
private fun DesignCard(
    design: SavedDesign,
    cachedPreview: (Long) -> List<PathData>?,
    loadPreview: suspend (Long) -> List<PathData>,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onExport: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    // Show what was loaded last time straight away, then refresh from the database.
    val paths by produceState(cachedPreview(design.pId), design.pId) {
        value = loadPreview(design.pId)
    }

    Column(
        modifier
            .clip(TileShape)
            .clickable(role = Role.Button, onClick = onClick)
    ) {
        DesignThumbnail(design, paths.orEmpty())
        Row(
            modifier = Modifier.padding(start = 6.dp, top = 6.dp, bottom = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = design.name.ifBlank { stringResource(Res.string.untitled_drawing) },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = dateLabel(design.time),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            // Delete sits behind a menu so it cannot be hit while scrolling the gallery.
            MenuButton(stringResource(Res.string.more_options), Icons.Rounded.MoreVert) { close ->
                if (onExport != null) {
                    MenuItem(
                        stringResource(Res.string.export_as_file), Icons.Outlined.SaveAlt,
                        onClick = { close(); onExport() }
                    )
                }
                MenuItem(
                    stringResource(Res.string.delete), Icons.Outlined.Delete,
                    destructive = true,
                    onClick = { close(); onDelete() }
                )
            }
        }
    }
}

/** The drawing's first page as a sheet of paper on the desk; a second sheet peeks out when there are more pages. */
@Composable
private fun DesignThumbnail(design: SavedDesign, paths: List<PathData>) {
    val paper = design.state.backgroundColor
    val pageCount = design.state.pages.size
    val pageWidth = design.state.pageWidth
    val pageHeight = design.state.pageHeight
    // Render to a texture once, so scrolling the gallery does not redraw every stroke per frame.
    val cached = Modifier.graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(TILE_ASPECT)
            .clip(TileShape)
            // The desk. Unlike surfaceDim this tone stands apart from the screen in dark theme too.
            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
        contentAlignment = Alignment.Center
    ) {
        if (design.isLegacy) {
            // Older drawings have no page: the strokes are fitted to the whole tile.
            val bounds = remember(paths) { paths.boundsOrNull() }
            Canvas(
                Modifier
                    .fillMaxSize()
                    .then(cached)
                    .background(paper)
                    // The paper fills the tile here, so it needs its own edge against the screen.
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, TileShape)
            ) {
                if (bounds != null) drawFitted(paths, bounds, paper)
            }
        } else {
            Box(Modifier.padding(16.dp).aspectRatio(pageWidth / pageHeight)) {
                if (pageCount > 1) {
                    Box(
                        Modifier
                            .matchParentSize()
                            .offset(5.dp, 5.dp)
                            .background(paper)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    )
                }
                Canvas(Modifier.matchParentSize().shadow(3.dp).then(cached).background(paper)) {
                    // Strokes are in page units; map the page width onto the sheet's px width.
                    scale(size.width / pageWidth, pivot = Offset.Zero) { drawStrokes(paths, paper) }
                }
            }
            if (pageCount > 1) {
                Text(
                    text = stringResource(Res.string.pages_count, pageCount),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest, CircleShape)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun DrawScope.drawStrokes(paths: List<PathData>, paper: Color) {
    paths.fastForEach { pathData ->
        drawPath(
            path = pathData.path,
            color = pathData.color,
            thickness = pathData.thickness,
            pathEffect = pathData.pathEffect,
            isEraser = pathData.isEraser,
            backgroundColor = paper,
            shapeType = pathData.shapeType,
            shapePoints = pathData.shapePoints,
            prebuiltPath = pathData.renderPath()
        )
    }
}

/** Draws [paths] scaled and centred so that [bounds] fills most of the canvas. */
private fun DrawScope.drawFitted(paths: List<PathData>, bounds: Rect, paper: Color) {
    if (bounds.width <= 0f || bounds.height <= 0f) return
    val fit = minOf(size.width / bounds.width, size.height / bounds.height) * 0.85f
    translate(
        left = size.width / 2 - bounds.center.x * fit,
        top = size.height / 2 - bounds.center.y * fit
    ) {
        scale(fit, pivot = Offset.Zero) { drawStrokes(paths, paper) }
    }
}

/** The smallest rectangle containing every stroke, or null when there is nothing drawn. */
private fun List<PathData>.boundsOrNull(): Rect? =
    mapNotNull { it.getBoundingBox() }.reduceOrNull { a, b ->
        Rect(minOf(a.left, b.left), minOf(a.top, b.top), maxOf(a.right, b.right), maxOf(a.bottom, b.bottom))
    }

/** "Today", "Yesterday", or a short date. */
@Composable
private fun dateLabel(time: Instant): String {
    val zone = TimeZone.currentSystemDefault()
    val date = time.toLocalDateTime(zone).date
    return when (date.daysUntil(Clock.System.now().toLocalDateTime(zone).date)) {
        0 -> stringResource(Res.string.today)
        1 -> stringResource(Res.string.yesterday)
        else -> {
            val month = stringArrayResource(Res.array.months_short)[date.month.number - 1]
            stringResource(Res.string.short_date, date.day, month, date.year)
        }
    }
}

// ── Empty states ─────────────────────────────────────────────────────────────

@Composable
private fun EmptyGallery(onNewDrawing: () -> Unit, onImport: (() -> Unit)?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BlankSheets(Modifier.size(width = 168.dp, height = 132.dp))
        Spacer(Modifier.height(28.dp))
        Text(
            text = stringResource(Res.string.no_designs),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.no_designs_desc),
            modifier = Modifier.widthIn(max = 360.dp),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onNewDrawing, shape = ChromeShape) {
            Icon(Icons.Rounded.Brush, null, Modifier.size(18.dp))
            Text(stringResource(Res.string.draw), Modifier.padding(start = 8.dp))
        }
        // Bringing drawings over from another device starts from an empty gallery.
        if (onImport != null) {
            Spacer(Modifier.height(4.dp))
            TextButton(onClick = onImport) {
                Icon(Icons.Outlined.FileOpen, null, Modifier.size(18.dp))
                Text(stringResource(Res.string.import_drawing), Modifier.padding(start = 8.dp))
            }
        }
    }
}

/** Two sheets of paper with a first stroke on the top one — drawn with the app's own stroke renderer. */
@Composable
private fun BlankSheets(modifier: Modifier = Modifier) {
    val desk = MaterialTheme.colorScheme.surfaceContainerHighest
    val paper = MaterialTheme.colorScheme.surfaceContainerLowest
    val edge = MaterialTheme.colorScheme.outlineVariant
    val ink = MaterialTheme.colorScheme.primary
    Canvas(modifier.clip(RoundedCornerShape(24.dp)).background(desk)) {
        val sheet = Size(size.width * 0.5f, size.height * 0.72f)
        val topLeft = Offset((size.width - sheet.width) / 2, (size.height - sheet.height) / 2)
        rotate(-8f) {
            drawRect(paper, topLeft, sheet)
            drawRect(edge, topLeft, sheet, style = Stroke(1.dp.toPx()))
        }
        rotate(4f) {
            drawRect(paper, topLeft, sheet)
            drawRect(edge, topLeft, sheet, style = Stroke(1.dp.toPx()))
            val inset = sheet.width * 0.18f
            val span = sheet.width - inset * 2
            drawPath(
                path = List(33) { i ->
                    val t = i / 32f
                    Offset(
                        x = topLeft.x + inset + span * t,
                        y = center.y - sin(t * 2 * PI).toFloat() * sheet.height * 0.14f
                    )
                },
                color = ink,
                thickness = 4.dp.toPx(),
                pathEffect = PaintingStyleType.STROKE
            )
        }
    }
}

@Composable
private fun NoResults(query: String, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(Res.string.no_search_results, query),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(Res.string.no_search_results_desc),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DeleteConfirmationDialog(projectName: String, onCancel: () -> Unit, onDone: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(stringResource(Res.string.confirm_deletion)) },
        text = { Text(stringResource(Res.string.delete_confirmation, projectName)) },
        confirmButton = {
            TextButton(onClick = onDone) {
                Text(stringResource(Res.string.delete), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text(stringResource(Res.string.cancel)) }
        }
    )
}
