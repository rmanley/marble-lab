package tech.rkanelabs.marblelab.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.rkanelabs.marblelab.data.TileType
import javax.inject.Inject

@HiltViewModel
class LevelEditorViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(LevelEditorUiState())
    val uiState: StateFlow<LevelEditorUiState> = _uiState.asStateFlow()

    fun onTilePaint(row: Int, col: Int) = viewModelScope.launch {
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
                                    type = state.selectedTile
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
                editMode = mode
            )
        }
    }

    fun onTileTypeSelected(tileType: TileType) = viewModelScope.launch {
        Log.d("test", "tile type = $tileType")
        _uiState.update {
            it.copy(
                selectedTile = tileType
            )
        }
    }
}