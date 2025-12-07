package tech.rkanelabs.marblelab.ui

import tech.rkanelabs.marblelab.data.Tile

const val ROWS = 8
const val COLUMNS = 8

data class LevelEditorUiState(
    val isLoading: Boolean = true,
    val tiles: List<List<TileUiState>> = List(ROWS) {
        List(COLUMNS) {
            TileUiState(
                tile = Tile(),
            )
        }
    }
)

data class TileUiState(
    val tile: Tile,
)