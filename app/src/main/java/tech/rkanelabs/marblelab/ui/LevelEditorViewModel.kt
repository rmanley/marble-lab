package tech.rkanelabs.marblelab.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import tech.rkanelabs.marblelab.data.Tile
import javax.inject.Inject

@HiltViewModel
class LevelEditorViewModel @Inject constructor() : ViewModel() {
    val uiState: StateFlow<LevelEditorUiState> = flow {
        emit(
            LevelEditorUiState(
                isLoading = false,
                tiles = List(ROWS) {
                    List(COLUMNS) {
                        TileUiState(
                            tile = Tile(),
                        )
                    }
                }
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LevelEditorUiState()
    )

    fun onTilePaint(row: Int, col: Int) {
        Log.d("test", "drag ($row, $col)")
    }
}