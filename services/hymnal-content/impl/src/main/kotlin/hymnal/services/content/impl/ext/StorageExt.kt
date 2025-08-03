package hymnal.services.content.impl.ext

import hymnal.services.content.impl.model.LyricType
import hymnal.services.content.impl.model.RemoteHymn
import hymnal.services.model.Hymn
import hymnal.services.model.HymnLyrics
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.json.Json
import timber.log.Timber

private const val BUCKET_ID = "hymnals"
private const val FILE_PATH = "sda-hymnal-db.json"

internal suspend fun Storage.downloadHymns(): List<Hymn>? {
    return try {
        val bytes = from(BUCKET_ID)
            .downloadAuthenticated(FILE_PATH)

        val jsonString = bytes.decodeToString() // Convert ByteArray to String
        val hymns: List<RemoteHymn> = Json.decodeFromString<List<RemoteHymn>>(jsonString)

        hymns.map { remoteHymn ->
            Hymn(
                index = remoteHymn.index,
                number = remoteHymn.number,
                title = remoteHymn.title,
                majorKey = null,
                lyrics = remoteHymn.lyrics.mapNotNull { section ->
                    when (section.type) {
                        LyricType.VERSE -> HymnLyrics.Verse(
                            index = section.index,
                            lines = section.lines
                        )
                        LyricType.REFRAIN -> HymnLyrics.Chorus(
                            index = section.index,
                            lines = section.lines
                        )
                        else -> null
                    }
                }
            )
        }
    } catch (e: Exception) {
        Timber.e(e, "Failed to download hymnal content")
        null
    }
}