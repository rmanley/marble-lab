package tech.rkanelabs.marblelab.data

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import tech.rkanelabs.marblelab.util.CoroutineDispatcherProvider
import javax.inject.Inject

class LevelsRepository @Inject constructor(
    private val contentResolver: ContentResolver,
    private val json: Json,
    private val coroutines: CoroutineDispatcherProvider
) {
    suspend fun saveTiles(uri: Uri, tiles: List<Tile>): Result<String> = withContext(coroutines.io) {
        runCatching {
            val jsonString = json.encodeToString(tiles)
            contentResolver.openOutputStream(uri)!!.use { outputStream ->
                outputStream.write(jsonString.toByteArray())
            }
            uri.displayName
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun loadTiles(uri: Uri): Result<Pair<String, List<Tile>>> = withContext(coroutines.io) {
        runCatching {
            contentResolver.openInputStream(uri)!!.use { inputStream ->
                Pair(
                    uri.displayName,
                json.decodeFromStream(inputStream)
                )
            }
        }
    }

    private val Uri.displayName: String
        get() = contentResolver.query(this, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME).takeUnless { it < 0 }?.let { nameIndex ->
                    cursor.getString(nameIndex)
                }
            } else {
                ""
            }
        }.orEmpty()
}