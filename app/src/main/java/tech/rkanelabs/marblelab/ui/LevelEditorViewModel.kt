package tech.rkanelabs.marblelab.ui

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.rkanelabs.marblelab.data.LevelExporterRepository
import tech.rkanelabs.marblelab.data.TileType
import tech.rkanelabs.marblelab.data.WallMask
import javax.inject.Inject

@HiltViewModel
class LevelEditorViewModel @Inject constructor(
    private val levelExporterRepository: LevelExporterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LevelEditorUiState())
    val uiState: StateFlow<LevelEditorUiState> = _uiState.asStateFlow()

    private val _events = Channel<LevelEditorEvent>()
    val events = _events.receiveAsFlow()

    fun onTilePaint(row: Int, col: Int) = viewModelScope.launch {
        if (_uiState.value.isLoading) return@launch

        Log.d("test", "drag ($row, $col)")
        _uiState.update { state ->
            state.copy(
                tiles = state.tiles.mapIndexed { rowIndex, columns ->
                    if (row != rowIndex) {
                        columns
                    } else {
                        columns.mapIndexed { columnIndex, cell ->
                            if (columnIndex != col) cell else cell.copy(
                                tile = cell.tile.copy(
                                    type = if (state.editMode != EditMode.Walls) {
                                        state.selectedTile
                                    } else {
                                        cell.tile.type
                                    },
                                    walls = when (state.editMode) {
                                        EditMode.Walls -> {
                                            if (state.selectedWallMask == WallMask.None) {
                                                WallMask.None
                                            } else {
                                                cell.tile.walls.with(state.selectedWallMask)
                                            }
                                        }
                                        EditMode.Erase -> WallMask.None
                                        else -> cell.tile.walls
                                    }
                                )
                            )
                        }
                    }
                }
            )
        }
    }

    fun onEditModeSelected(mode: EditMode) = viewModelScope.launch {
        Log.d("test", "edit mode = $mode")
        _uiState.update {
            it.copy(
                editMode = mode,
                selectedTile = if (mode == EditMode.Erase) TileType.Empty else it.selectedTile
            )
        }
    }

    fun onTileTypeSelected(tileType: TileType) = viewModelScope.launch {
        Log.d("test", "tile type = $tileType")
        _uiState.update {
            it.copy(
                selectedTile = tileType,
                selectedWallMask = WallMask.None
            )
        }
    }

    fun onWallMaskSelected(wallMask: WallMask) = viewModelScope.launch {
        Log.d("test", "wall mask = $wallMask")
        _uiState.update {
            it.copy(
                selectedWallMask = wallMask
            )
        }
    }

    fun onSaveTapped(uri: Uri) = viewModelScope.launch {
        Log.d("test", "save tapped")
        val tiles = _uiState.value.tiles
        _uiState.update { it.copy(isLoading = true) }
        levelExporterRepository.saveTiles(
            uri = uri,
            tiles = tiles.flatten().map { it.tile }
        ).onSuccess {
            Log.d("test", "saved as: $it")
            _events.send(LevelEditorEvent.FileSaveResult("Level saved to: $it"))
        }.onFailure {
            Log.e("test", "save failed", it)
            _events.send(LevelEditorEvent.FileSaveResult("Failed to save level!"))
        }
        _uiState.update { it.copy(isLoading = false) }
    }
}
