package hymnal.services.content.impl

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import hymnal.libraries.coroutines.DispatcherProvider
import hymnal.libraries.coroutines.Scopable
import hymnal.libraries.coroutines.ioScopable
import hymnal.libraries.model.Hymnal
import hymnal.services.content.HymnalContentSyncProvider
import hymnal.services.content.impl.ext.downloadHymns
import hymnal.services.model.Hymn
import hymnal.services.model.HymnLyrics
import hymnal.storage.db.dao.HymnsDao
import hymnal.storage.db.entity.DbLyricType
import hymnal.storage.db.entity.HymnEntity
import hymnal.storage.db.entity.LyricPartEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber

@ContributesBinding(scope = AppScope::class, binding<HymnalContentSyncProvider>())
@Inject
class HymnalContentSyncProviderImpl(
    private val hymnsDao: HymnsDao,
    private val dispatcherProvider: DispatcherProvider,
    private val supabase: SupabaseClient,
) : HymnalContentSyncProvider, Scopable by ioScopable(dispatcherProvider) {

    private val exceptionLogger = CoroutineExceptionHandler { _, e -> Timber.e(e) }

    override fun invoke() {
        scope.launch(exceptionLogger) {
            Hymnal.entries.forEach { syncHymnal(it) }
        }
    }

    private suspend fun syncHymnal(hymnal: Hymnal) {
        val hymns = hymnsDao.getAllHymns(hymnal.year)

        if (hymns.isEmpty()) {
            val downloadedHymns = supabase.storage.downloadHymns(hymnal)
            if (downloadedHymns != null) {
                saveHymnsToDatabase(downloadedHymns)
            } else {
                Timber.e("Failed to download hymns for ${hymnal.title}.")
                // We'll retry on next launch.
            }
        } else {
            Timber.i("Found ${hymns.size} hymns for ${hymnal.title} in the database, no sync needed.")
        }
    }

    private suspend fun saveHymnsToDatabase(hymns: List<Hymn>) {
        hymns.forEach { hymn ->
            val entity = HymnEntity(
                hymnId = hymn.index,
                number = hymn.number,
                title = hymn.title,
                majorKey = hymn.majorKey,
                author = hymn.author,
                authorLink = hymn.authorLink,
                year = hymn.year,
            )

            val lyricParts = hymn.lyrics.map { lyrics ->
                val type = when (lyrics) {
                    is HymnLyrics.Chorus -> DbLyricType.CHORUS
                    is HymnLyrics.Verse -> DbLyricType.VERSE
                }

                LyricPartEntity(
                    hymnOwnerId = entity.hymnId,
                    type = type,
                    itemIndex = lyrics.index,
                    lines = lyrics.lines,
                )
            }

            hymnsDao.saveHymnAndLyrics(
                hymn = entity,
                lyricParts = lyricParts,
            )
        }
    }
}