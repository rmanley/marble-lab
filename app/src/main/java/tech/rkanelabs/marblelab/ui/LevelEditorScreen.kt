package tech.rkanelabs.marblelab.ui

import android.util.Log
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import tech.rkanelabs.marblelab.data.TileType
import tech.rkanelabs.marblelab.data.WallMask
import tech.rkanelabs.marblelab.ui.theme.MarbleLabTheme

@Composable
fun LevelEditorScreen(
    // todo: put this at route level
    viewModel: LevelEditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { MarbleLabTopAppBar() },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 64.dp)
            ) {
                EditModeRadioGroupRow(
                    selected = uiState.editMode,
                    onSelected = viewModel::onEditModeSelected
                )
                TilePaletteRow(
                    mode = uiState.editMode,
                    selectedTile = uiState.selectedTile,
                    onTileSelected = viewModel::onTileTypeSelected,
                    selectedWallMask = uiState.selectedWallMask,
                    onWallMaskSelected = viewModel::onWallMaskSelected
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
            .background(
                tileType = uiState.tile.type
            ),
        contentAlignment = Alignment.Center,
    ) {
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
    onSelected: (EditMode) -> Unit
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
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditModeRadioGroupPreview() {
    MarbleLabTheme {
        EditModeRadioGroupRow(
            selected = EditMode.Floor
        ) {
            print("$it")
        }
    }
}

@Composable
fun TilePaletteRow(
    mode: EditMode,
    selectedTile: TileType,
    onTileSelected: (TileType) -> Unit,
    selectedWallMask: WallMask,
    onWallMaskSelected: (WallMask) -> Unit
) {
    val tilesForMode = when (mode) {
        EditMode.Floor -> listOf(TileType.Floor, TileType.Hole)
        EditMode.Walls -> listOf()
        EditMode.Objects -> listOf(TileType.Marble, TileType.Goal)
        EditMode.Erase -> listOf()
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (mode == EditMode.Walls) {
            items(listOf(WallMask.Up, WallMask.Right, WallMask.Down, WallMask.Left, WallMask.All)) { wallMask ->
                FilterChip(
                    onClick = { onWallMaskSelected(wallMask) },
                    label = {
                        Text(
                            when (wallMask) {
                                WallMask.Up -> "Up"
                                WallMask.Right -> "Right"
                                WallMask.Down -> "Down"
                                WallMask.Left -> "Left"
                                else -> "All"
                            }
                        )
                    },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .padding(6.dp)
                        )
                    },
                    selected = wallMask == selectedWallMask
                )
            }
        } else {
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
                    selected = tileType == selectedTile
                )
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