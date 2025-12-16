package tech.rkanelabs.marblelab.ui

sealed interface LevelEditorEvent {
    data class FileSaveResult(val message: String) : LevelEditorEvent
}