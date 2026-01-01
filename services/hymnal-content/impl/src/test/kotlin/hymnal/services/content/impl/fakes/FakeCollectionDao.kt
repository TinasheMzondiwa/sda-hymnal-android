// Copyright (C) 2026 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.content.impl.fakes

import hymnal.storage.db.dao.CollectionDao
import hymnal.storage.db.entity.CollectionEntity
import hymnal.storage.db.entity.CollectionHymnCrossRef
import hymnal.storage.db.entity.CollectionWithHymns
import hymnal.storage.db.entity.HymnWithCollections
import kotlinx.coroutines.flow.Flow

class FakeCollectionDao : FakeBaseDao<CollectionEntity>(), CollectionDao {
    override fun getCollection(id: String): Flow<CollectionEntity?> {
        TODO("Not yet implemented")
    }

    override fun get(id: String): CollectionEntity? {
        TODO("Not yet implemented")
    }

    override fun getAllCollections(): Flow<List<CollectionEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAll(): List<CollectionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun addHymnToCollection(join: CollectionHymnCrossRef) {
        TODO("Not yet implemented")
    }

    override suspend fun removeHymnFromCollection(
        collectionId: String,
        hymnId: String
    ) {
        TODO("Not yet implemented")
    }

    override fun getHymnsForCollection(collectionId: String): Flow<CollectionWithHymns?> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllHymnsForCollection(collectionId: String): CollectionWithHymns? {
        TODO("Not yet implemented")
    }

    override fun getCollectionsForHymn(hymnId: String): Flow<HymnWithCollections?> {
        TODO("Not yet implemented")
    }

    override fun getAllCollectionsWithHymns(): Flow<List<CollectionWithHymns>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteHymnsFromCollection(collectionId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCollectionById(collectionId: String) {
        TODO("Not yet implemented")
    }
}