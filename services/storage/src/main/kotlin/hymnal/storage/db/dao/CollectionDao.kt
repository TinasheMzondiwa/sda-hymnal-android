// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.storage.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import hymnal.storage.db.entity.CollectionEntity
import hymnal.storage.db.entity.CollectionHymnCrossRef
import hymnal.storage.db.entity.CollectionWithHymns
import hymnal.storage.db.entity.HymnWithCollections
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao : BaseDao<CollectionEntity> {

    // --- Collection Methods ---
    @Query("SELECT * FROM collections WHERE collectionId = :id")
    fun getCollection(id: String): Flow<CollectionEntity?>

    @Query("SELECT * FROM collections ORDER BY title ASC")
    fun getAllCollections(): Flow<List<CollectionEntity>>

    @Query("SELECT * FROM collections ORDER BY title ASC")
    suspend fun getAll(): List<CollectionEntity>

    // --- Relationship Methods ---

    /**
     * Adds a hymn to a collection.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addHymnToCollection(join: CollectionHymnCrossRef)

    /**
     * Removes a hymn from a specific collection.
     */
    @Query("DELETE FROM collection_hymn_join WHERE collectionId = :collectionId AND hymnId = :hymnId")
    suspend fun removeHymnFromCollection(collectionId: String, hymnId: String)

    /**
     * Gets all hymns for a given collection.
     */
    @Transaction
    @Query("SELECT * FROM collections WHERE collectionId = :collectionId")
    fun getHymnsForCollection(collectionId: String): Flow<CollectionWithHymns?>

    @Transaction
    @Query("SELECT * FROM collections WHERE collectionId = :collectionId")
    suspend fun getAllHymnsForCollection(collectionId: String): CollectionWithHymns?

    /**
     * Gets all collections that a specific hymn belongs to.
     */
    @Transaction
    @Query("SELECT * FROM hymns WHERE hymnId = :hymnId")
    fun getCollectionsForHymn(hymnId: String): Flow<HymnWithCollections?>

    /**
     * Gets all collections with their hymns.
     */
    @Transaction
    @Query("SELECT * FROM collections ORDER BY created DESC")
    fun getAllCollectionsWithHymns(): Flow<List<CollectionWithHymns>>

    /**
     * Deletes a collection and its relationships.
     */
    @Transaction
    suspend fun deleteCollection(collectionId: String) {
        // First, remove all hymns associated with this collection from the join table
        deleteHymnsFromCollection(collectionId)
        // Then, delete the collection itself
        deleteCollectionById(collectionId)
    }

    @Query("DELETE FROM collection_hymn_join WHERE collectionId = :collectionId")
    suspend fun deleteHymnsFromCollection(collectionId: String)

    @Query("DELETE FROM collections WHERE collectionId = :collectionId")
    suspend fun deleteCollectionById(collectionId: String)
}