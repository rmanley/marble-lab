package tech.rkanelabs.marblelab.ui

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import tech.rkanelabs.marblelab.R
import tech.rkanelabs.marblelab.data.TileType
import tech.rkanelabs.marblelab.data.WallMask
import tech.rkanelabs.marblelab.ui.theme.MarbleLabTheme

@Composable
fun LevelEditorScreen(
    // todo: put this at route level
    viewModel: LevelEditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.events.collect { event ->
                when (event) {
                    is LevelEditorEvent.FileSaveResult -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                    is LevelEditorEvent.FileLoadError -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MarbleLabTopAppBar(
                actions = {
                    LevelEditorMenuActions(
                        onCreateNewLevelTapped = viewModel::onCreateNewLevelTapped,
                        onSaveTapped = viewModel::onSaveTapped,
                        onLoadTapped = viewModel::onLoadTapped,
                        getFilename = viewModel::getFilename,
                        enabled = uiState.isLoading.not()
                    )
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp)
            ) {
                EditModeRadioGroupRow(
                    selected = uiState.editMode,
                    onSelected = viewModel::onEditModeSelected,
                    enabled = uiState.isLoading.not()
                )
                TilePaletteRow(
                    mode = uiState.editMode,
                    selectedTile = uiState.selectedTile,
                    onTileSelected = viewModel::onTileTypeSelected,
                    selectedWallMask = uiState.selectedWallMask,
                    onWallMaskSelected = viewModel::onWallMaskSelected,
                    enabled = uiState.isLoading.not()
                )
            }
        }
    ) { innerPadding ->
        LevelEditorGrid(
            modifier = Modifier
                .padding(innerPadding),
            uiState = uiState,
            onTilePaint = viewModel::onTilePaint
        )
    }
}

@Composable
fun LevelEditorGrid(
    modifier: Modifier = Modifier,
    uiState: LevelEditorUiState,
    onTilePaint: (row: Int, col: Int) -> Unit = { row, col ->
        Log.d("test", "drag ($row, $col)")
    },
) {
    val paintedDuringDrag = remember { mutableSetOf<Pair<Int, Int>>() }
    var cellSizePx by remember { mutableFloatStateOf(0f) }
    var gridOffset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        paintedDuringDrag.clear()
                        val local = offset - gridOffset
                        val cell = offsetToCell(local, cellSizePx)
                        if (cell != null && paintedDuringDrag.add(cell)) {
                            onTilePaint(cell.first, cell.second)
                        }
                    },
                    onDrag = { change, _ ->
                        val cell = offsetToCell(change.position - gridOffset, cellSizePx)
                        if (cell != null && paintedDuringDrag.add(cell)) {
                            onTilePaint(cell.first, cell.second)
                        }
                    },
                    onDragEnd = {
                        paintedDuringDrag.clear()
                        Log.d("test", "drag end")
                    },
                    onDragCancel = {
                        paintedDuringDrag.clear()
                        Log.d("test", "drag cancel")
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
                .onSizeChanged { layoutSize ->
                    cellSizePx = layoutSize.width / 8f
                }
                .onGloballyPositioned { coords ->
                    val pos = coords.positionInParent()
                    gridOffset = Offset(pos.x, pos.y)
                },
        ) {
            uiState.tiles.forEachIndexed { row, columns ->
                Row(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    columns.forEachIndexed { col, tileUiState ->
                        TileCell(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            uiState = tileUiState,
                            onTap = {
                                onTilePaint(row, col)
                            },
                            debugText = "${row * COLUMNS + col}"
                        )
                    }
                }
            }
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(64.dp)
                    .fillMaxHeight()
                    .aspectRatio(1f),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}

private fun offsetToCell(
    position: Offset,
    cellSizePx: Float
): Pair<Int, Int>? {
    if (cellSizePx <= 0f) return null
    val row = (position.y / cellSizePx).toInt()
    val col = (position.x / cellSizePx).toInt()
    if (row !in 0 until 8 || col !in 0 until 8) return null
    return row to col
}

@Composable
fun TileCell(
    modifier: Modifier = Modifier,
    uiState: TileUiState,
    onTap: () -> Unit,
    debugText: String
) {
    val painter = when (uiState.tile.type) {
        TileType.Empty -> null
        TileType.Floor -> painterResource(R.drawable.tile_floor)
        TileType.Marble -> painterResource(R.drawable.tile_marble)
        TileType.Goal -> painterResource(R.drawable.tile_goal)
        TileType.Hole -> painterResource(R.drawable.tile_hole)
    }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        Log.d("test", "tap $debugText")
                        onTap()
                    },
                    onLongPress = {
                        Log.d("test", "tap $debugText")
                    }
                )
            }
            .border(
                color = Color.Black,
                sides = uiState.tile.walls
            )
            .padding(0.dp)
            .background(color = Color.Transparent),
        contentAlignment = Alignment.Center,
    ) {
        painter?.let {
            Image(
                painter = it,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        Text(debugText)
    }
}

@Preview(showBackground = true)
@Composable
fun LevelEditorGridPreview() {
    MarbleLabTheme {
        LevelEditorGrid(
            modifier = Modifier.fillMaxSize(),
            uiState = LevelEditorUiState(isLoading = false)
        )
    }
}

@Composable
fun EditModeRadioGroupRow(
    selected: EditMode,
    onSelected: (EditMode) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        EditMode.entries.forEach { mode ->
            FilterChip(
                selected = selected == mode,
                onClick = {
                    onSelected(mode)
                },
                label = {
                    Text(mode.name.lowercase().replaceFirstChar { it.uppercase() })
                },
                enabled = enabled
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditModeRadioGroupPreview() {
    MarbleLabTheme {
        EditModeRadioGroupRow(
            selected = EditMode.Floor,
            onSelected = {
                print("$it")
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TilePaletteRow(
    mode: EditMode,
    selectedTile: TileType,
    onTileSelected: (TileType) -> Unit,
    selectedWallMask: WallMask,
    onWallMaskSelected: (WallMask) -> Unit,
    enabled: Boolean = true
) {
    val tilesForMode = when (mode) {
        EditMode.Floor -> listOf(TileType.Floor, TileType.Hole)
        EditMode.Walls -> listOf()
        EditMode.Objects -> listOf(TileType.Marble, TileType.Goal)
        EditMode.Erase -> listOf()
    }

    LazyHorizontalStaggeredGrid(
        rows = StaggeredGridCells.Adaptive(minSize = 52.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp, max = 200.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalItemSpacing = 8.dp,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 4.dp)
    ) {
        when (mode) {
            EditMode.Erase -> {
                item {
                    // Empty, keep to prevent UI jump
                }
            }
            EditMode.Walls -> {
                items(
                    listOf(
                        WallMask.Up,
                        WallMask.Right,
                        WallMask.Down,
                        WallMask.Left,
                        WallMask.All,
                        WallMask.None
                    )
                ) { wallMask ->
                    FilterChip(
                        onClick = { onWallMaskSelected(wallMask) },
                        label = {
                            Text(
                                when (wallMask) {
                                    WallMask.Up -> "Up"
                                    WallMask.Right -> "Right"
                                    WallMask.Down -> "Down"
                                    WallMask.Left -> "Left"
                                    WallMask.All -> "All"
                                    else -> "None"
                                }
                            )
                        },
                        selected = wallMask == selectedWallMask,
                        enabled = enabled
                    )
                }
            }
            else -> {
                items(tilesForMode) { tileType ->
                    FilterChip(
                        onClick = { onTileSelected(tileType) },
                        label = { Text(tileType.name) },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .background(
                                        tileType = tileType,
                                        shape = CircleShape
                                    )
                                    .padding(6.dp)
                            )
                        },
                        selected = tileType == selectedTile,
                        enabled = enabled
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TilePaletteRowPreview() {
    MarbleLabTheme {
        TilePaletteRow(
            mode = EditMode.Floor,
            selectedTile = TileType.Floor,
            onTileSelected = { print("$it") },
            selectedWallMask = WallMask.None,
            onWallMaskSelected = { print("$it") }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WallsTilePaletteRowPreview() {
    MarbleLabTheme {
        TilePaletteRow(
            mode = EditMode.Walls,
            selectedTile = TileType.Floor,
            onTileSelected = { print("$it") },
            selectedWallMask = WallMask.None,
            onWallMaskSelected = { print("$it") }
        )
    }
}

@Composable
fun RowScope.LevelEditorMenuActions(
    onCreateNewLevelTapped: () -> Unit = {
        Log.d("test", "create new level tapped!")
    },
    onSaveTapped: (Uri) -> Unit = { uri ->
        Log.d("test", "save tapped: $uri!")
    },
    onLoadTapped: (Uri) -> Unit = { uri ->
        Log.d("test", "load tapped: $uri")
    },
    getFilename: () -> String = {
        "mbl_${System.currentTimeMillis()}.json"
    },
    enabled: Boolean = true
) {
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let(onSaveTapped) ?: Log.e("test", "Failed to create document!")
    }

    val chooseDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let(onLoadTapped) ?: Log.e("test", "Failed to load document!")
    }

    IconButton(
        onClick = onCreateNewLevelTapped,
        enabled = enabled
    ) {
        Icon(
            imageVector = Icons.Filled.AddCircleOutline,
            contentDescription = "Create new level"
        )
    }

    IconButton(
        onClick = {
            chooseDocumentLauncher.launch("application/json")
        },
        enabled = enabled
    ) {
        Icon(
            imageVector = Icons.Filled.FolderOpen,
            contentDescription = "Load level"
        )
    }

    IconButton(
        onClick = {
            // save as new file every time for simplicity
            createDocumentLauncher.launch(getFilename())
        },
        enabled = enabled
    ) {
        Icon(
            imageVector = Icons.Filled.Save,
            contentDescription = "Save level"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LevelEditorMenuActionsPreview() {
    MarbleLabTheme {
        Row {
            LevelEditorMenuActions()
        }
    }
}
