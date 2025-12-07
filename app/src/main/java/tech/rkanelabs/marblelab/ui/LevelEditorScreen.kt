package tech.rkanelabs.marblelab.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import tech.rkanelabs.marblelab.data.TileType
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

    Box(
        modifier = modifier
            .pointerInput(uiState.tiles) {
                detectDragGestures(
                    onDragStart = { offset ->
                        paintedDuringDrag.clear()
                        val cell = offsetToCell(offset, cellSizePx)
                        if (cell != null && paintedDuringDrag.add(cell)) {
                            onTilePaint(cell.first, cell.second)
                        }
                    },
                    onDrag = { change, _ ->
                        val cell = offsetToCell(change.position, cellSizePx)
                        if (cell != null && paintedDuringDrag.add(cell)) {
                            onTilePaint(cell.first, cell.second)
                        }
                    },
                    onDragEnd = {
                        paintedDuringDrag.clear()
                    },
                    onDragCancel = {
                        paintedDuringDrag.clear()
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
                .onSizeChanged { layoutSize ->
                    cellSizePx = layoutSize.width / 8f
                },
        ) {
            uiState.tiles.forEachIndexed { row, columns ->
                Row(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    columns.forEachIndexed { index, tileUiState ->
                        TileCell(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            uiState = tileUiState,
                            debugText = "${row * COLUMNS + index}"
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
    debugText: String
) {
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        Log.d("test", "tap $debugText")
                    },
                    onLongPress = {
                        Log.d("test", "tap $debugText")
                    }
                )
            }
            .border(
                width = 2.dp,
                color = Color.Black,
            )
            .padding(1.dp)
            .background(
                color = when (uiState.tile.type) {
                    TileType.Empty -> Color.White
                    TileType.Marble -> Color.Magenta
                    TileType.Goal -> Color.Green
                    TileType.Hole -> Color.Red
                }
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