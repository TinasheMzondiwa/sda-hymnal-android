package hymnal.services.content.impl

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import hymnal.libraries.coroutines.DispatcherProvider
import hymnal.services.content.HymnalContentProvider
import hymnal.services.model.Hymn
import hymnal.services.model.HymnCategory
import hymnal.storage.db.dao.HymnsDao
import hymnal.storage.db.entity.HymnWithLyrics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
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

    override fun categories(): Flow<List<HymnCategory>> {
        return flow {
            val categories = buildList {
                add(HymnCategory("1-695", "All", 1, 695))
                add(HymnCategory("1–69", "Worship", 1, 69))
                add(HymnCategory("70–73", "Trinity", 70, 73))
                add(HymnCategory("74–114", "God The Father", 74, 114))
                add(HymnCategory("115–256", "Jesus Christ", 115, 256))
                add(HymnCategory("257–270", "Holy Spirit", 257, 270))
                add(HymnCategory("271–278", "Holy Scripture", 271, 278))
                add(HymnCategory("279–343", "Gospel", 279, 343))
                add(HymnCategory("344–379", "Christian Church", 344, 379))
                add(HymnCategory("380–437", "Doctrines", 380, 437))
                add(HymnCategory("438–454", "Early Advent", 438, 454))
                add(HymnCategory("455–649", "Christian Life", 455, 649))
                add(HymnCategory("650–659", "Christian Home", 650, 659))
                add(HymnCategory("660–695", "Sentences and Responses", 660, 695))
            }

            emit(categories)
        }
    }
}