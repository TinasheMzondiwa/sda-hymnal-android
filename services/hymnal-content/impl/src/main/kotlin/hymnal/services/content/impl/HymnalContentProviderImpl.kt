package hymnal.services.content.impl

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import hymnal.libraries.coroutines.DispatcherProvider
import hymnal.libraries.model.SabbathResource
import hymnal.services.content.HymnalContentProvider
import hymnal.services.content.impl.model.ApiSabbathResource
import hymnal.services.content.impl.model.toDomain
import hymnal.services.model.Hymn
import hymnal.services.model.HymnCategory
import hymnal.storage.db.dao.HymnsDao
import hymnal.storage.db.entity.HymnWithLyrics
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDate
import java.time.temporal.WeekFields

@ContributesBinding(AppScope::class)
@Inject
class HymnalContentProviderImpl(
    private val hymnsDao: HymnsDao,
    private val dispatcherProvider: DispatcherProvider,
    private val supabase: SupabaseClient,
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

    override fun search(query: String): Flow<List<Hymn>> {
        return hymnsDao.searchLyrics(query)
            .map { it.map(HymnWithLyrics::toDomainHymn) }
            .flowOn(dispatcherProvider.io)
            .catch {
                Timber.e(it)
                emit(emptyList())
            }
    }

    override fun hymn(index: String): Flow<Hymn?> {
        return hymnsDao.getHymnWithLyricsById(index)
            .map { it?.toDomainHymn() }
            .flowOn(dispatcherProvider.io)
            .catch {
                Timber.e(it)
                emit(null)
            }
    }

    override suspend fun hymn(number: Int): Hymn? {
        return withContext(dispatcherProvider.io) {
            hymnsDao.getHymnWithLyricsByNumber(number)
                ?.toDomainHymn()
        }
    }

    override fun sabbathResources(): Flow<List<SabbathResource>> {
        return flow {
            val week = LocalDate.now().get(WeekFields.ISO.weekOfWeekBasedYear())

            val model = supabase
                .from("sabbath_resource")
                .select(
                    request = { filter { ApiSabbathResource::week eq week } }
                )
                .decodeSingle<ApiSabbathResource>()

            emit(model.toDomain())
        }.flowOn(dispatcherProvider.io)
            .catch { Timber.e(it) }
    }
}