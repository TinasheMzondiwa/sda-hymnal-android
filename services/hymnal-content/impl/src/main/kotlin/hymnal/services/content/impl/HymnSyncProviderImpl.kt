// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.content.impl

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import hymnal.libraries.coroutines.DispatcherProvider
import hymnal.libraries.coroutines.Scopable
import hymnal.libraries.coroutines.ioScopable
import hymnal.services.content.HymnSyncProvider
import hymnal.services.content.impl.model.ApiHymnRevision
import hymnal.services.content.impl.model.LyricType
import hymnal.services.content.impl.model.RemoteHymn
import hymnal.storage.db.dao.HymnsDao
import hymnal.storage.db.entity.DbLyricType
import hymnal.storage.db.entity.HymnEntity
import hymnal.storage.db.entity.LyricPartEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import timber.log.Timber

@ContributesBinding(scope = AppScope::class, binding<HymnSyncProvider>())
@Inject
class HymnSyncProviderImpl(
    private val hymnsDao: HymnsDao,
    private val dispatcherProvider: DispatcherProvider,
    private val supabase: SupabaseClient,
) : HymnSyncProvider, Scopable by ioScopable(dispatcherProvider) {

    private val exceptionLogger = CoroutineExceptionHandler { _, e -> Timber.e(e) }
    private val json: Json by lazy { Json { ignoreUnknownKeys = true } }

    override fun invoke(index: String) {
        scope.launch(exceptionLogger) {
            // Caller guarantees existence, so this safe-guard is fine
            val hymn = hymnsDao.get(index) ?: return@launch

            val model = supabase
                .from(REVISIONS_TABLE)
                .select {
                    filter { ApiHymnRevision::index eq index }
                }
                .decodeSingleOrNull<ApiHymnRevision>() ?: return@launch

            if (model.revision > hymn.hymn.revision) {
                val apiHymn = json.decodeFromJsonElement<RemoteHymn>(model.payload)

                saveHymnToDatabase(
                    hymn = apiHymn,
                    year = hymn.hymn.year,
                    revision = model.revision,
                )
            } else {
                Timber.i("Hymn $index is up to date.")
            }
        }
    }

    private suspend fun saveHymnToDatabase(hymn: RemoteHymn, year: String, revision: Int) {
        val entity = HymnEntity(
            hymnId = hymn.index,
            number = hymn.number,
            title = hymn.title,
            majorKey = hymn.majorKey,
            author = hymn.author,
            authorLink = hymn.authorLink,
            year = year,
            revision = revision
        )

        val lyricParts = hymn.lyrics.mapNotNull { section ->
            val type = when (section.type) {
                LyricType.VERSE -> DbLyricType.VERSE
                LyricType.REFRAIN -> DbLyricType.CHORUS
                else -> return@mapNotNull null // Skip unknown types
            }

            LyricPartEntity(
                hymnOwnerId = entity.hymnId,
                type = type,
                itemIndex = section.index,
                lines = section.lines,
            )
        }

        hymnsDao.saveHymnAndLyrics(
            hymn = entity,
            lyricParts = lyricParts,
        )
    }

    private companion object {
        const val REVISIONS_TABLE = "hymn_revisions"
    }
}