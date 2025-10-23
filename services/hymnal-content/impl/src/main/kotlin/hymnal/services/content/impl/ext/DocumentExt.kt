// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.content.impl.ext

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import hymnal.storage.db.entity.CollectionEntity
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

/**
 * Extension function to safely convert a Firebase DocumentSnapshot into a local Room CollectionEntity.
 */
fun DocumentSnapshot.toCollectionEntity(): CollectionEntity? {
    val collectionId = this.id

    return try {
        CollectionEntity(
            collectionId = collectionId,
            title = getString("title") ?: "",
            description = getString("description"),
            created = getLong("created") ?: System.currentTimeMillis(),
            color = getString("color") ?: "#4FC3F7",
        )
    } catch (e: Exception) {
        Timber.e(e, "Error converting Firestore document $collectionId to CollectionEntity")
        null
    }
}

sealed interface SnapShotEvent<T> {
    data class Added<T>(val item: T) : SnapShotEvent<T>
    data class Modified<T>(val item: T) : SnapShotEvent<T>
    data class Removed<T>(val item: T) : SnapShotEvent<T>
}

fun <T> CollectionReference.subscribe(): Flow<SnapShotEvent<T>> {

}
