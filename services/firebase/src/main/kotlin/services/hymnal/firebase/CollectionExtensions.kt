// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package services.hymnal.firebase

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Sealed interface representing the three possible real-time events from a Firestore snapshot.
 * The item payload is the raw QueryDocumentSnapshot, requiring the caller to handle conversion.
 */
sealed interface SnapShotEvent {
    val snapshot: QueryDocumentSnapshot

    data class Added(override val snapshot: QueryDocumentSnapshot) : SnapShotEvent
    data class Modified(override val snapshot: QueryDocumentSnapshot) : SnapShotEvent
    data class Removed(override val snapshot: QueryDocumentSnapshot) : SnapShotEvent
}

/**
 * Extension function on CollectionReference to subscribe to real-time updates as a Flow.
 *
 * @return A Flow of SnapShotEvent which handles ADDED, MODIFIED, and REMOVED events.
 */
fun CollectionReference.subscribe(): Flow<SnapShotEvent> = callbackFlow {
    val subscription = this@subscribe.addSnapshotListener { snapshot, error ->
        if (error != null) {
            // Send the error down the flow channel and close it.
            close(error)
            return@addSnapshotListener
        }

        if (snapshot != null) {
            for (dc in snapshot.documentChanges) {

                val event = when (dc.type) {
                    DocumentChange.Type.ADDED -> SnapShotEvent.Added(dc.document)
                    DocumentChange.Type.MODIFIED -> SnapShotEvent.Modified(dc.document)
                    DocumentChange.Type.REMOVED -> SnapShotEvent.Removed(dc.document)
                }

                // Offer the event to the Flow collector.
                // trySend is used because the collector might be cancelled.
                trySend(event)
            }
        }
    }

    // Suspension block: Keeps the Flow alive until cancelled or explicitly closed.
    awaitClose {
        subscription.remove()
    }
}