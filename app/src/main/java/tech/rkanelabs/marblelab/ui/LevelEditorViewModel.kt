package tech.rkanelabs.marblelab.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.rkanelabs.marblelab.data.Tile
import tech.rkanelabs.marblelab.data.TileType
import javax.inject.Inject

@HiltViewModel
class LevelEditorViewModel @Inject constructor() : ViewModel() {
    private val tiles = MutableList(ROWS) {
        MutableList(COLUMNS) {
            Tile()
        }
    }
    private val _uiState = MutableStateFlow(LevelEditorUiState())
    val uiState: StateFlow<LevelEditorUiState> = _uiState.asStateFlow()

    fun onTilePaint(row: Int, col: Int) = viewModelScope.launch {
        Log.d("test", "drag ($row, $col)")
        tiles[row][col] = tiles[row][col].copy(type = TileType.Hole)
        _uiState.update { state ->
            state.copy(
                tiles = List(ROWS) { row ->
                    tiles[row].map {
                        TileUiState(it)
                    }
                }
            )
        }
    }
}