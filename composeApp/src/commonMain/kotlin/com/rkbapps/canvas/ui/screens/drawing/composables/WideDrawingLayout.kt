package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.SaveAlt
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import canvas.composeapp.generated.resources.Res
import canvas.composeapp.generated.resources.back
import canvas.composeapp.generated.resources.clear_page
import canvas.composeapp.generated.resources.export_as_file
import canvas.composeapp.generated.resources.focus_mode
import canvas.composeapp.generated.resources.more_options
import canvas.composeapp.generated.resources.page_size
import canvas.composeapp.generated.resources.paper
import canvas.composeapp.generated.resources.rename
import canvas.composeapp.generated.resources.save
import canvas.composeapp.generated.resources.save_as_image
import canvas.composeapp.generated.resources.share
import canvas.composeapp.generated.resources.tool_options
import com.rkbapps.canvas.model.CanvasPage
import com.rkbapps.canvas.model.DrawingState
import com.rkbapps.canvas.ui.screens.drawing.DrawingAction
import com.rkbapps.canvas.ui.screens.drawing.DrawingScreenState
import com.rkbapps.canvas.ui.screens.drawing.utils.DrawingTool
import com.rkbapps.canvas.ui.screens.drawing.utils.PaintingStyleType
import com.rkbapps.canvas.ui.screens.drawing.utils.ShapeType
import com.rkbapps.canvas.ui.screens.drawing.utils.paperPalette
import org.jetbrains.compose.resources.stringResource

// ─────────────────────────────────────────────────────────────────────────────
// WIDE LAYOUT  (Medium and Expanded — tablets, desktop)
//
//   top bar      the document: name, history, save, export
//   left rail    the tools
//   canvas       the page, with page size and zoom in its bottom corners
//   side panel   options of the active tool, paper, pages — can be hidden
// ─────────────────────────────────────────────────────────────────────────────

private val RailWidth = 64.dp
private val PanelWidth = 280.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WideDrawingLayout(
    state: DrawingState,
    uiState: DrawingScreenState,
    drawingName: String,
    tool: DrawingTool,
    snackbarHostState: SnackbarHostState,
    /** Open by default where there is room for it (Expanded), closed on Medium. */
    panelInitiallyOpen: Boolean,
    onSelectTool: (DrawingTool) -> Unit,
    onSave: () -> Unit,
    /** Null when the drawing cannot be exported as a file (legacy drawings, iOS). */
    onExportFile: (() -> Unit)?,
    onAction: (DrawingAction) -> Unit,
    navigateBack: () -> Unit,
) {
    val currentPage = state.pages.getOrElse(uiState.currentPageIndex) { CanvasPage() }
    val paperColor = if (uiState.isLegacy) state.backgroundColor else currentPage.backgroundColor
    val canUndo = if (uiState.isLegacy) state.undoStack.isNotEmpty() else currentPage.undoStack.isNotEmpty()
    val canRedo = if (uiState.isLegacy) state.redoStack.isNotEmpty() else currentPage.redoStack.isNotEmpty()
    val focus = uiState.isFullScreen

    var panelOpen by rememberSaveable(panelInitiallyOpen) { mutableStateOf(panelInitiallyOpen) }
    var dialog by rememberSaveable { mutableStateOf<DrawingDialog?>(null) }

    DrawingDialogs(dialog, state, uiState, paperColor, onAction, onDismiss = { dialog = null })

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AnimatedVisibility(visible = !focus) {
                Column {
                    TopAppBar(
                        title = {
                            DrawingTitle(drawingName, onRename = { onAction(DrawingAction.OnOpenNameEditDialog) })
                        },
                        navigationIcon = {
                            ChromeButton(
                                label = stringResource(Res.string.back),
                                onClick = navigateBack,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            ) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, null) }
                        },
                        actions = {
                            UndoRedoButtons(canUndo, canRedo, onAction)
                            BarDivider(Modifier.padding(horizontal = 4.dp))
                            FilledTonalButton(onClick = onSave, shape = ChromeShape) {
                                Icon(Icons.Outlined.Save, null, Modifier.size(18.dp))
                                Text(stringResource(Res.string.save), Modifier.padding(start = 8.dp))
                            }
                            Spacer(Modifier.width(4.dp))
                            MenuButton(stringResource(Res.string.share), Icons.Outlined.FileDownload) { close ->
                                MenuItem(stringResource(Res.string.save_as_image), Icons.Outlined.Image, onClick = {
                                    close(); onAction(DrawingAction.OnSaveAsImage)
                                })
                                MenuItem(stringResource(Res.string.share), Icons.Outlined.Share, onClick = {
                                    close(); onAction(DrawingAction.OnShareDrawing)
                                })
                                if (onExportFile != null) {
                                    MenuItem(stringResource(Res.string.export_as_file), Icons.Outlined.SaveAlt, onClick = {
                                        close(); onExportFile()
                                    })
                                }
                            }
                            MenuButton(stringResource(Res.string.more_options), Icons.Rounded.MoreVert) { close ->
                                MenuItem(stringResource(Res.string.rename), Icons.Outlined.Edit, onClick = {
                                    close(); onAction(DrawingAction.OnOpenNameEditDialog)
                                })
                                if (!uiState.isLegacy) {
                                    MenuItem(stringResource(Res.string.page_size), Icons.Outlined.Description, onClick = {
                                        close(); onAction(DrawingAction.OnOpenPageSizePicker)
                                    })
                                }
                                MenuItem(stringResource(Res.string.focus_mode), Icons.Rounded.Fullscreen, onClick = {
                                    close(); onAction(DrawingAction.OnEnterFullScreen)
                                })
                                HorizontalDivider()
                                MenuItem(
                                    stringResource(Res.string.clear_page), Icons.Outlined.DeleteSweep,
                                    destructive = true,
                                    onClick = { close(); dialog = DrawingDialog.CLEAR_PAGE }
                                )
                            }
                            ChromeButton(
                                label = stringResource(Res.string.tool_options),
                                selected = panelOpen,
                                subtle = true,
                                onClick = { panelOpen = !panelOpen },
                                modifier = Modifier.padding(start = 4.dp, end = 8.dp)
                            ) { Icon(Icons.Outlined.Tune, null, Modifier.size(22.dp)) }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        )
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    ) { padding ->
        Row(Modifier.fillMaxSize().padding(padding)) {

            // ── Tool rail ────────────────────────────────────────────────────
            AnimatedVisibility(visible = !focus) {
                Row {
                    // Tools at the top, view controls at the bottom; scrolls if the window is too short.
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(RailWidth)
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            ToolButtons(tool, onSelectTool)
                        }
                        Column(
                            modifier = Modifier.padding(top = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // The nib doubles as a quick way to reach the tool options.
                            ChromeButton(
                                label = stringResource(Res.string.tool_options),
                                onClick = { panelOpen = !panelOpen }
                            ) {
                                NibBadge(state.selectedColor, state.selectedThickness, tool == DrawingTool.ERASER)
                            }
                            ChromeButton(
                                label = stringResource(Res.string.focus_mode),
                                shortcut = "Ctrl+F",
                                onClick = { onAction(DrawingAction.OnEnterFullScreen) }
                            ) { Icon(Icons.Rounded.Fullscreen, null) }
                        }
                    }
                    VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }

            // ── Canvas ───────────────────────────────────────────────────────
            BoxWithConstraints(Modifier.weight(1f).fillMaxHeight()) {
                val showViewControls = !uiState.isLegacy && !focus
                // Lift the page above the corner controls only when it would sit under them.
                val clearance = if (
                    showViewControls &&
                    fittedPageReachesBottomCorners(maxWidth, maxHeight, state.pageWidth / state.pageHeight)
                ) ViewControlsHeight else 0.dp
                DrawingSurface(
                    state, uiState, currentPage, onAction, Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = clearance)
                )

                if (showViewControls) {
                    PageSizeChip(
                        label = state.pageSizeLabel,
                        onClick = { onAction(DrawingAction.OnOpenPageSizePicker) },
                        modifier = Modifier.align(Alignment.BottomStart).padding(12.dp)
                    )
                    ZoomControl(
                        zoom = uiState.zoom,
                        onAction = onAction,
                        modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp)
                    )
                }
                if (focus) {
                    FocusDock(
                        activeTool = tool,
                        canUndo = canUndo,
                        canRedo = canRedo,
                        onSelectTool = onSelectTool,
                        onAction = onAction,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
                    )
                }
            }

            // ── Side panel ───────────────────────────────────────────────────
            AnimatedVisibility(visible = !focus && panelOpen) {
                Row {
                    VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    SidePanel(
                        tool = tool,
                        color = state.selectedColor,
                        thickness = state.selectedThickness,
                        style = state.selectedPathEffect,
                        shape = state.selectedShapeType,
                        paperColor = paperColor,
                        hasSelection = uiState.selectedPathId != null,
                        // Pages only exist for paged drawings.
                        pages = if (uiState.isLegacy) null else state.pages,
                        currentPageIndex = uiState.currentPageIndex,
                        pageWidth = state.pageWidth,
                        pageHeight = state.pageHeight,
                        onAction = onAction,
                        onRequestDeletePage = { dialog = DrawingDialog.DELETE_PAGE }
                    )
                }
            }
        }
    }
}

/** Space the page-size chip and zoom control take up along the bottom of the canvas. */
private val ViewControlsHeight = 56.dp
private val ViewControlsWidth = 170.dp

/**
 * Whether a page of [aspect] (width ÷ height), fitted to a [width]×[height] desk,
 * would extend under the controls in the bottom corners.
 */
private fun fittedPageReachesBottomCorners(width: Dp, height: Dp, aspect: Float): Boolean {
    val margin = 32.dp
    val availableWidth = width - margin
    val availableHeight = height - margin
    if (availableWidth <= 0.dp || availableHeight <= 0.dp) return false
    val heightLimited = availableWidth / availableHeight > aspect
    val pageWidth = if (heightLimited) availableHeight * aspect else availableWidth
    val pageHeight = if (heightLimited) availableHeight else availableWidth / aspect
    return (width - pageWidth) / 2 < ViewControlsWidth && (height - pageHeight) / 2 < ViewControlsHeight
}

@Composable
private fun SidePanel(
    tool: DrawingTool,
    color: Color,
    thickness: Float,
    style: PaintingStyleType,
    shape: ShapeType,
    paperColor: Color,
    hasSelection: Boolean,
    pages: List<CanvasPage>?,
    currentPageIndex: Int,
    pageWidth: Float,
    pageHeight: Float,
    onAction: (DrawingAction) -> Unit,
    onRequestDeletePage: () -> Unit,
) {
    val pagesPanel: @Composable () -> Unit = {
        if (pages != null) {
            PagesPanel(
                pages = pages,
                currentPageIndex = currentPageIndex,
                pageWidth = pageWidth,
                pageHeight = pageHeight,
                onAction = onAction,
                onRequestDelete = onRequestDeletePage,
                modifier = Modifier.padding(16.dp)
            )
        }
    }

    BoxWithConstraints(
        Modifier.fillMaxHeight().width(PanelWidth).background(MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        // With enough height the pages stay pinned at the bottom, so they do not jump
        // around as the tool options above change; on short windows everything scrolls.
        val pinPages = maxHeight >= 620.dp
        Column(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = toolLabel(tool),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    ToolInspector(
                        tool = tool,
                        color = color,
                        thickness = thickness,
                        style = style,
                        shape = shape,
                        paperColor = paperColor,
                        hasSelection = hasSelection,
                        onAction = onAction
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Section(stringResource(Res.string.paper), Modifier.fillMaxWidth().padding(16.dp)) {
                    ColorSwatches(
                        palette = paperPalette,
                        selected = paperColor,
                        onSelected = { onAction(DrawingAction.OnBackgroundColorChange(it)) }
                    )
                }
                if (!pinPages && pages != null) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    pagesPanel()
                }
            }
            if (pinPages && pages != null) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                pagesPanel()
            }
        }
    }
}
