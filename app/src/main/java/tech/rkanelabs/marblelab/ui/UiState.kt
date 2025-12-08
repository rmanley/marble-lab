package tech.rkanelabs.marblelab.ui

import tech.rkanelabs.marblelab.data.Tile
import tech.rkanelabs.marblelab.data.TileType

const val ROWS = 8
const val COLUMNS = 8

enum class EditMode {
    Floor,
    Walls,
    Objects,
    Erase
}

data class LevelEditorUiState(
    val isLoading: Boolean = true,
    val tiles: List<List<TileUiState>> = List(ROWS) {
        List(COLUMNS) {
            TileUiState(
                tile = Tile(),
            )
        }
    },
    val editMode: EditMode = EditMode.Floor,
    val selectedTile: TileType = TileType.Floor
)

data class TileUiState(
    val tile: Tile,
)