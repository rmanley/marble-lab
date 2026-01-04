package tech.rkanelabs.marblelab.ui

import tech.rkanelabs.marblelab.data.Tile
import tech.rkanelabs.marblelab.data.TileType
import tech.rkanelabs.marblelab.data.WallMask

const val ROWS = 8
const val COLUMNS = 8

enum class EditMode {
    Floor,
    Walls,
    Objects,
    Erase
}

data class LevelEditorUiState(
    val isLoading: Boolean = false,
    val tiles: List<List<TileUiState>> = DEFAULT_TILES,
    val editMode: EditMode = EditMode.Floor,
    val selectedTile: TileType = TileType.Floor,
    val selectedWallMask: WallMask = WallMask.None
) {
    companion object {
        val DEFAULT_TILES = List(ROWS) {
            List(COLUMNS) {
                TileUiState(
                    tile = Tile(),
                )
            }
        }
    }
}

data class TileUiState(
    val tile: Tile,
)