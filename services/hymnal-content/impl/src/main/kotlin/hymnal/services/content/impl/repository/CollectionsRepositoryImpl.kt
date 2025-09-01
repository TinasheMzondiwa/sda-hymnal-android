// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.content.impl.repository

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import hymnal.libraries.coroutines.DispatcherProvider
import hymnal.services.content.repository.CollectionsRepository
import hymnal.services.model.Hymn
import hymnal.services.model.HymnsCollection
import hymnal.storage.db.dao.CollectionDao
import hymnal.storage.db.entity.CollectionEntity
import hymnal.storage.db.entity.CollectionHymnCrossRef
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID

@ContributesBinding(AppScope::class)
@Inject
class CollectionsRepositoryImpl(
    private val collectionsDao: CollectionDao,
    private val dispatcherProvider: DispatcherProvider,
) : CollectionsRepository {

    override fun listAll(): Flow<List<HymnsCollection>> {
        return collectionsDao.getAllCollectionsWithHymns()
            .map {
                it.map { (collection, hymns) ->
                    HymnsCollection(
                        collectionId = collection.collectionId,
                        title = collection.title,
                        description = collection.description,
                        hymns = hymns.map { hymn ->
                            Hymn(
                                index = hymn.hymnId,
                                title = hymn.title,
                                number = hymn.number,
                                majorKey = hymn.majorKey,
                                lyrics = emptyList(), // Purposely avoid loading lyrics in collections
                            )
                        },
                        created = collection.created,
                        color = collection.color,
                    )
                }
            }
            .flowOn(dispatcherProvider.io)
            .catch {
                Timber.e(it)
                emit(emptyList())
            }
    }

    override suspend fun create(
        title: String,
        description: String?,
        color: String
    ): Result<Unit> {
        return withContext(dispatcherProvider.io) {
            try {
                collectionsDao.insert(
                    CollectionEntity(
                        collectionId = UUID.randomUUID().toString(),
                        title = title,
                        description = description,
                        created = System.currentTimeMillis(),
                        color = color,
                    )
                )
                Result.success(Unit)
            } catch (ex: Exception) {
                Timber.e(ex)
                Result.failure(ex)
            }
        }
    }

    override suspend fun addHymnToCollection(
        collectionId: String,
        hymnId: String
    ): Result<Unit> {
        return withContext(dispatcherProvider.io) {
            try {
                collectionsDao.addHymnToCollection(
                    CollectionHymnCrossRef(
                        collectionId = collectionId,
                        hymnId = hymnId
                    )
                )
                Result.success(Unit)
            } catch (ex: Exception) {
                Timber.e(ex)
                Result.failure(ex)
            }
        }
    }

    override suspend fun removeHymnFromCollection(
        collectionId: String,
        hymnId: String
    ): Result<Unit> {
        return withContext(dispatcherProvider.io) {
            try {
                collectionsDao.removeHymnFromCollection(collectionId, hymnId)
                Result.success(Unit)
            } catch (ex: Exception) {
                Timber.e(ex)
                Result.failure(ex)
            }
        }
    }
}