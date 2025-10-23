// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.content.impl.ext

import com.google.firebase.firestore.DocumentSnapshot
import hymnal.storage.db.entity.CollectionEntity
import hymnal.storage.db.entity.CollectionHymnCrossRef
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

/**
 * Extension function to safely convert a Firebase DocumentSnapshot into a local Room CollectionHymnCrossRef.
 */
fun DocumentSnapshot.toCollectionHymnCrossRef(): CollectionHymnCrossRef? {
    val collectionId = this.id

    return try {
        CollectionHymnCrossRef(
            collectionId = getString("collectionId") ?: return null,
            hymnId = getString("hymnId") ?: return null,
        )
    } catch (e: Exception) {
        Timber.e(e, "Error converting Firestore document $collectionId to CollectionEntity")
        null
    }
}
