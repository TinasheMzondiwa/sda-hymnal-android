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
import hymnal.storage.db.entity.CollectionHymnCrossRef
import hymnal.storage.db.entity.CollectionWithHymns
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import timber.log.Timber

@ContributesBinding(AppScope::class)
@Inject
class CollectionsRepositoryImpl(
    private val collectionsDao: CollectionDao,
    private val dispatcherProvider: DispatcherProvider,
    private val firebaseSync: FirebaseSync,
) : CollectionsRepository {

    init {
        firebaseSync.attachCollectionListener()
    }

    override fun listAll(): Flow<List<HymnsCollection>> {
        return collectionsDao.getAllCollectionsWithHymns()
            .map { it.map { collectionWithHymns -> collectionWithHymns.toHymnsCollection() } }
            .flowOn(dispatcherProvider.io)
            .onStart { refresh() }
            .catch {
                Timber.e(it)
                emit(emptyList())
            }
    }

    override suspend fun create(
        title: String,
        description: String?,
        color: String,
    ): Result<Unit> {
        return withContext(dispatcherProvider.io) {
            try {
                firebaseSync.createCollection(
                    title = title,
                    description = description,
                    color = color,
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
                        hymnId = hymnId,
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

    override fun getHymnCollections(hymnId: String): Flow<List<HymnsCollection>> {
        return collectionsDao.getCollectionsForHymn(hymnId)
            .map { collections ->
                collections?.collections?.map { collection ->
                    HymnsCollection(
                        collectionId = collection.collectionId,
                        title = collection.title,
                        description = collection.description,
                        hymns = emptyList(), // Purposely avoid loading hymns in this flow
                        created = collection.created,
                        color = collection.color,
                    )
                } ?: emptyList()
            }
            .flowOn(dispatcherProvider.io)
            .catch {
                Timber.e(it)
                emit(emptyList())
            }
    }

    override fun getCollectionById(collectionId: String): Flow<HymnsCollection?> {
        return collectionsDao.getHymnsForCollection(collectionId)
            .map { it?.toHymnsCollection() }
            .flowOn(dispatcherProvider.io)
            .catch { Timber.e(it) }
    }

    override suspend fun deleteCollection(collectionId: String): Result<Unit> {
        return withContext(dispatcherProvider.io) {
            try {
                collectionsDao.deleteCollection(collectionId)
                Result.success(Unit)
            } catch (ex: Exception) {
                Timber.e(ex)
                Result.failure(ex)
            }
        }
    }

    override fun refresh() {
        firebaseSync.attachCollectionListener()
    }
    override fun unsubscribe() {
        firebaseSync.detachCollectionListener()
    }

    private fun CollectionWithHymns.toHymnsCollection(): HymnsCollection {
        return HymnsCollection(
            collectionId = collection.collectionId,
            title = collection.title,
            description = collection.description,
            hymns = hymns.map { hymn ->
                Hymn(
                    index = hymn.hymnId,
                    title = hymn.title,
                    number = hymn.number,
                    majorKey = hymn.majorKey,
                    author = hymn.author,
                    lyrics = emptyList(), // Purposely avoid loading lyrics in collections
                )
            },
            created = collection.created,
            color = collection.color,
        )
    }
}