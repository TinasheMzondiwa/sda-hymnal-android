// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.content.impl

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import hymnal.libraries.coroutines.DispatcherProvider
import hymnal.libraries.model.Hymnal
import hymnal.libraries.model.HymnalAppConfig
import hymnal.libraries.model.SabbathResource
import hymnal.services.content.HymnSyncProvider
import hymnal.services.content.HymnalContentProvider
import hymnal.services.content.impl.model.ApiSabbathResource
import hymnal.services.content.impl.model.toEntity
import hymnal.services.model.Hymn
import hymnal.services.model.HymnCategory
import hymnal.storage.db.dao.HymnsDao
import hymnal.storage.db.dao.SabbathResourceDao
import hymnal.storage.db.entity.HymnWithLyrics
import hymnal.storage.db.entity.RecentHymnEntity
import hymnal.storage.db.entity.SabbathResourceEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.WeekFields

@ContributesBinding(AppScope::class)
@Inject
class HymnalContentProviderImpl(
    private val appConfig: HymnalAppConfig,
    private val hymnsDao: HymnsDao,
    private val dispatcherProvider: DispatcherProvider,
    private val supabase: SupabaseClient,
    private val sabbathResourceDao: SabbathResourceDao,
    private val hymnSyncProvider: HymnSyncProvider,
) : HymnalContentProvider {

    private val scope = CoroutineScope(dispatcherProvider.io + SupervisorJob())

    override fun hymns(year: String): Flow<List<Hymn>> {
        return hymnsDao.getAllHymnsWithLyrics(year)
            .map { it.map(HymnWithLyrics::toDomainHymn) }
            .flowOn(dispatcherProvider.io)
            .catch {
                Timber.e(it)
                emit(emptyList())
            }
    }

    override fun categories(year: String): Flow<List<HymnCategory>> {
        val hymnal = Hymnal.fromYear(year) ?: return flow { emit(emptyList()) }
        return flow {
            val categories = buildList {
                when (hymnal) {
                    Hymnal.OldHymnal -> {
                        // add(HymnCategory("1-703", "All", 1, 703))
                        // Figure out how to categorise these
                    }
                    Hymnal.NewHymnal -> {
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

                    Hymnal.Choruses -> {
                        // No categories for choruses
                    }
                }
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
            .onEach {
                if (it != null) {
                    syncHymn(index)
                }
            }
            .map { it?.toDomainHymn() }
            .flowOn(dispatcherProvider.io)
            .catch {
                Timber.e(it)
                emit(null)
            }
    }

    private fun syncHymn(index: String) = scope.launch {
        hymnsDao.insertRecentHymn(RecentHymnEntity(hymnId = index))
        hymnsDao.trimRecentHistory()

        if (appConfig.syncHymnsEnabled) {
            hymnSyncProvider(index)
        }
    }

    override suspend fun hymn(number: Int, year: String): Hymn? {
        return withContext(dispatcherProvider.io) {
            try {
                hymnsDao.getHymnWithLyricsByNumber(number, year)?.toDomainHymn()
            } catch (e: Exception) {
                Timber.e(e)
                null
            }
        }
    }

    override fun recentHymns(limit: Int): Flow<List<Hymn>> {
        return hymnsDao.getRecentHymns(limit)
            .map { it.map(HymnWithLyrics::toDomainHymn) }
            .flowOn(dispatcherProvider.io)
            .catch {
                Timber.e(it)
                emit(emptyList())
            }
    }

    override fun sabbathHymns(): Flow<List<Hymn>> {
        val numbers = buildList {
            add(40)
            addAll(380..395)
            addAll(listOf(668, 677))
        }
        return hymnsDao.getHymnsWithLyricsInRange(numbers, Hymnal.NewHymnal.year)
            .map { it.map(HymnWithLyrics::toDomainHymn) }
            .flowOn(dispatcherProvider.io)
            .catch {
                Timber.e(it)
                emit(emptyList())
            }
    }

    override fun sabbathResources(): Flow<List<SabbathResource>> {
        // Defined with Sunday as the first day of the week
        val weekFields = WeekFields.of(DayOfWeek.SUNDAY, 1)
        val week = LocalDate.now().get(weekFields.weekOfWeekBasedYear())

        return sabbathResourceDao
            .get(week)
            .onEach { if (it.isEmpty()) fetchResource(week) }
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(dispatcherProvider.io)
            .catch { Timber.e(it) }
    }

    private suspend fun fetchResource(week: Int) = withContext(dispatcherProvider.io) {
        try {
            val model = supabase
                .from("sabbath_resource")
                .select(
                    request = { filter { ApiSabbathResource::week eq week } }
                )
                .decodeSingleOrNull<ApiSabbathResource>() ?: return@withContext

            sabbathResourceDao.insertAll(model.toEntity())
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun SabbathResourceEntity.toDomain(): SabbathResource = when (type) {
        SabbathResourceEntity.Type.SCRIPTURE -> {
            SabbathResource.Scripture(
                id = id,
                reference = reference,
                text = text,
                section = section
            )
        }

        SabbathResourceEntity.Type.QUOTE -> {
            SabbathResource.Quote(
                id = id,
                reference = reference,
                text = text,
            )
        }
    }

}