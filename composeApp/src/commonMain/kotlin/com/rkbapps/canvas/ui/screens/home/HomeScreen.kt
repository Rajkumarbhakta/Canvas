package com.rkbapps.canvas.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import canvas.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.navigation.Draw
import com.rkbapps.canvas.navigation.Settings
import com.rkbapps.canvas.util.Platforms
import com.rkbapps.canvas.util.getPlatform
import com.rkbapps.canvas.util.getWindowSize
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = koinViewModel()
) {
    val allDesign by viewModel.allDesign.collectAsStateWithLifecycle()
    val migrationStatus by viewModel.migrationState.collectAsStateWithLifecycle()
    val currentDeletableProject by viewModel.selectedProjectForDelete.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val gridState = rememberLazyGridState()
    val isExpanded by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex == 0 && gridState.firstVisibleItemScrollOffset <= 0
        }
    }
    val windowSizeClass: WindowSizeClass = getWindowSize()
    val isLargeScreen = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

    val columns = GridCells.Adaptive(minSize = 160.dp)

    val contentPadding = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> PaddingValues(16.dp)
        WindowWidthSizeClass.Medium -> PaddingValues(horizontal = 24.dp, vertical = 16.dp)
        else -> PaddingValues(horizontal = 32.dp, vertical = 24.dp)
    }

    Row(modifier = Modifier.fillMaxSize()) {
        if (isLargeScreen) {
            NavigationRail(
                header = {
                    FloatingActionButton(
                        onClick = { navController.navigate(route = Draw()) },
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Brush,
                            contentDescription = stringResource(Res.string.new_drawing)
                        )
                    }
                },
                modifier = Modifier.fillMaxHeight()
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                NavigationRailItem(
                    selected = true,
                    onClick = { /* Already here */ },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.GridView,
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(Res.string.my_designs)) }
                )
                Spacer(modifier = Modifier.weight(1f))
                NavigationRailItem(
                    selected = false,
                    onClick = {
                        if (getPlatform() != Platforms.WEB) {
                            navController.navigate(route = Settings)
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(Res.string.settings)
                        )
                    },
                    label = { Text(stringResource(Res.string.settings)) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Scaffold(
            modifier = Modifier.weight(1f).nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(
                    title = {
                        Text(stringResource(Res.string.my_designs))
                    },
                    actions = {
                        if (!isLargeScreen) {
                            IconButton(
                                onClick = {
                                    if (getPlatform() != Platforms.WEB) {
                                        navController.navigate(route = Settings)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = stringResource(Res.string.settings)
                                )
                            }
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            floatingActionButton = {
                if (!isLargeScreen) {
                    ExtendedFloatingActionButton(
                        text = { Text(text = stringResource(Res.string.draw)) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Brush,
                                contentDescription = stringResource(Res.string.new_drawing)
                            )
                        },
                        onClick = {
                            navController.navigate(route = Draw())
                        },
                        expanded = isExpanded
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {

            if (migrationStatus.isLoading) {
                AlertDialog(
                    onDismissRequest = {},
                    title = {
                        Text(stringResource(Res.string.please_wait))
                    },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator()
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

            if (allDesign.designs.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = columns,
                    state = gridState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = contentPadding,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = allDesign.designs.reversed(), key = { it.id }) {
                        DesignListItem(it, onDelete = {
                            viewModel.selectDesignForDelete(it)
                        }) {
                            navController.navigate(route = Draw(it.id))
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .alpha(0.1f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        stringResource(Res.string.no_designs),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        stringResource(Res.string.no_designs_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
}

@Composable
fun DesignListItem(design: SavedDesign, onDelete: () -> Unit = {}, onClick: () -> Unit = {}) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Brush,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = design.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = design.time.toString().substringBefore("T"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(Res.string.delete),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(projectName: String, onCancel: () -> Unit, onDone: () -> Unit) {
    AlertDialog(
        onDismissRequest = {
            onCancel()
        },
        title = {
            Text(stringResource(Res.string.confirm_deletion))
        },
        text = {
            Text(stringResource(Res.string.delete_confirmation, projectName))
        },
        confirmButton = {
            OutlinedButton(onClick = onDone) {
                Text(stringResource(Res.string.delete))
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text(stringResource(Res.string.cancel))
            }
        }
    )
}
