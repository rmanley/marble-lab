@file:OptIn(ExperimentalMaterial3Api::class)

package tech.rkanelabs.marblelab

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tech.rkanelabs.marblelab.data.Tile
import tech.rkanelabs.marblelab.data.TileType
import tech.rkanelabs.marblelab.ui.theme.MarbleLabTheme

const val ROWS = 8
const val COLUMNS = 8

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MarbleLabTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { MarbleLabTopAppBar() },
                ) { innerPadding ->
                    LevelEditorGrid(
                        modifier = Modifier
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MarbleLabTopAppBar(
    modifier: Modifier = Modifier
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text("Marble Lab")
        },
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
fun MarbleLabTopAppBarPreview() {
    MarbleLabTheme {
        MarbleLabTopAppBar()
    }
}

@Composable
fun LevelEditorGrid(
    modifier: Modifier = Modifier,
    grid: List<List<TileUiState>> = List(ROWS) {
        List(COLUMNS) {
            TileUiState(
                tile = Tile(),
            )
        }
    },
    onCellPaint: (row: Int, col: Int) -> Unit = { row, col ->
        Log.d("test", "drag ($row, $col)")
    },
) {
    val paintedDuringDrag = remember { mutableSetOf<Pair<Int, Int>>() }
    var cellSizePx by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .pointerInput(grid) {
                detectDragGestures(
                    onDragStart = { offset ->
                        paintedDuringDrag.clear()
                        val cell = offsetToCell(offset, cellSizePx)
                        if (cell != null && paintedDuringDrag.add(cell)) {
                            onCellPaint(cell.first, cell.second)
                        }
                    },
                    onDrag = { change, _ ->
                        val cell = offsetToCell(change.position, cellSizePx)
                        if (cell != null && paintedDuringDrag.add(cell)) {
                            onCellPaint(cell.first, cell.second)
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
            grid.forEachIndexed { row, columns ->
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
        LevelEditorGrid(modifier = Modifier.fillMaxSize())
    }
}