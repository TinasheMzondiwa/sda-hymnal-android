// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.content.repository

import hymnal.services.model.HymnsCollection
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing hymn collections.
 */
interface CollectionsRepository {

    /**
     * Retrieves a flow of all hymn collections.
     *
     * @return A [Flow] emitting a list of [HymnsCollection] objects.
     */
    fun listAll(): Flow<List<HymnsCollection>>

    /**
     * Creates a new hymn collection.
     *
     * @param title The title of the collection.
     * @param description An optional description for the collection.
     * @param color The color associated with the collection.
     * @return A [Result] indicating success or failure.
     */
    suspend fun create(
        title: String,
        description: String?,
        color: String,
    ): Result<Unit>

    /**
     * Adds a hymn to a specific collection.
     *
     * @param collectionId The ID of the collection.
     * @param hymnId The ID of the hymn to add.
     * @return A [Result] indicating success or failure.
     */
    suspend fun addHymnToCollection(collectionId: String, hymnId: String): Result<Unit>

    /**
     * Removes a hymn from a specific collection.
     *
     * @param collectionId The ID of the collection.
     * @param hymnId The ID of the hymn to remove.
     * @return A [Result] indicating success or failure.
     */
    suspend fun removeHymnFromCollection(collectionId: String, hymnId: String): Result<Unit>
}