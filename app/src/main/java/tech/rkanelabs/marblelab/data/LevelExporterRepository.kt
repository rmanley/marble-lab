package tech.rkanelabs.marblelab.data

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tech.rkanelabs.marblelab.util.CoroutineDispatcherProvider
import javax.inject.Inject

class LevelExporterRepository @Inject constructor(
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