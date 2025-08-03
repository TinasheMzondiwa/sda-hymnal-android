package hymnal.services.content.impl

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import hymnal.libraries.coroutines.DispatcherProvider
import hymnal.services.content.HymnalContentProvider
import hymnal.services.model.Hymn
import hymnal.storage.db.dao.HymnsDao
import hymnal.storage.db.entity.HymnWithLyrics
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import timber.log.Timber

@ContributesBinding(AppScope::class)
@Inject
class HymnalContentProviderImpl(
    private val hymnsDao: HymnsDao,
    private val dispatcherProvider: DispatcherProvider,
) : HymnalContentProvider {

    override fun hymns(): Flow<List<Hymn>> {
        return hymnsDao.getAllHymnsWithLyrics()
            .map { it.map(HymnWithLyrics::toDomainHymn) }
            .flowOn(dispatcherProvider.io)
            .catch {
                Timber.e(it)
                emit(emptyList())
            }
    }
}