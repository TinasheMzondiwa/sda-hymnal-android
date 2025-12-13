package hymnal.services.content.impl.ext

import hymnal.libraries.model.Hymnal
import hymnal.services.content.impl.model.LyricType
import hymnal.services.content.impl.model.RemoteHymn
import hymnal.services.model.Hymn
import hymnal.services.model.HymnLyrics
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.json.Json
import timber.log.Timber

private const val BUCKET_ID = "hymnals"
private const val FILE_PATH = "sda-hymnal-db-v2.json"
private const val FILE_PATH_CHORUSES = "choruses.json"
private const val FILE_PATH_OLD_HYMN = "church-hymnal-db.json"

internal suspend fun Storage.downloadHymns(hymnal: Hymnal): List<Hymn>? {
    return try {
        val path = when (hymnal) {
            Hymnal.OldHymnal -> FILE_PATH_OLD_HYMN
            Hymnal.NewHymnal -> FILE_PATH
        }
        val bytes = from(BUCKET_ID)
            .downloadAuthenticated(path)

        val jsonString = bytes.decodeToString() // Convert ByteArray to String
        val json = Json { ignoreUnknownKeys = true }
        val hymns: List<RemoteHymn> = json.decodeFromString<List<RemoteHymn>>(jsonString)

        hymns.map { remoteHymn ->
            Hymn(
                index = remoteHymn.index,
                number = remoteHymn.number,
                title = remoteHymn.title,
                majorKey = remoteHymn.majorKey,
                author = remoteHymn.author,
                authorLink = remoteHymn.authorLink,
                year = hymnal.year,
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
                },
                revision = remoteHymn.revision ?: 1
            )
        }
    } catch (e: Exception) {
        Timber.e(e, "Failed to download hymnal content")
        null
    }
}