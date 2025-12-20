// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.content.impl.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import hymnal.libraries.coroutines.DispatcherProvider
import hymnal.services.content.impl.ext.toCollectionEntity
import hymnal.services.content.impl.ext.toCollectionHymnCrossRef
import hymnal.storage.db.dao.CollectionDao
import hymnal.storage.db.entity.CollectionEntity
import hymnal.storage.db.entity.CollectionHymnCrossRef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import services.hymnal.firebase.FirebaseContentSync
import services.hymnal.firebase.SnapShotEvent
import services.hymnal.firebase.signedInUser
import services.hymnal.firebase.subscribe
import timber.log.Timber
import java.util.UUID

interface FirebaseSync {

    fun attachAfterSignIn()
    fun attachCollectionListener()
    fun detachCollectionListener()

    suspend fun createCollection(title: String, description: String?, color: String)
    suspend fun deleteCollection(collectionId: String)

    suspend fun addHymnToCollection(ref: CollectionHymnCrossRef)
    suspend fun removeHymnFromCollection(collectionId: String, hymnId: String)

    suspend fun removeUserContent()
}

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class FirebaseSyncImpl(
    private val collectionsDao: CollectionDao,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val dispatcherProvider: DispatcherProvider,
) : FirebaseSync {

    private val syncScope = CoroutineScope(dispatcherProvider.io + SupervisorJob())
    private var syncJob: Job? = null

    private val collectionsRef: CollectionReference?
        get() = auth.signedInUser()?.uid?.let { userId ->
            firestore.collection("users")
                .document(userId)
                .collection("collections")
        }
    private val hymnCollectionsRef: CollectionReference?
        get() = auth.signedInUser()?.uid?.let { userId ->
            firestore.collection("users")
                .document(userId)
                .collection("hymnCollections")
        }

    override fun attachAfterSignIn() {
        syncScope.launch {
            // persist everything in the DB to firebase here
            val collectionsRef = collectionsRef ?: return@launch
            val hymnCollectionsRef = hymnCollectionsRef ?: return@launch

            val collections = collectionsDao.getAll()
            collections.forEach { collection ->
                val collectionId = collection.collectionId
                val newCollectionRef = collectionsRef.document(collectionId)

                val collectionData = mapOf(
                    "collectionId" to collectionId,
                    "title" to collection.title,
                    "description" to collection.description,
                    "created" to collection.created,
                    "color" to collection.color,
                    "lastModified" to FieldValue.serverTimestamp(),
                    "deleted" to false
                )

                // Add this collection to firebase
                newCollectionRef.set(collectionData).await()

                // Now add all the hymns saved in this collection
                collectionsDao.getAllHymnsForCollection(collectionId)?.let { (_, hymns) ->
                    hymns.forEach { hymn ->
                        val joinData = mapOf(
                            "collectionId" to collectionId,
                            "hymnId" to hymn.hymnId,
                        )
                        hymnCollectionsRef.document("${collectionId}_${hymn.hymnId}")
                            .set(joinData)
                            .await()
                    }
                }

            }

            attachCollectionListener()
        }
    }

    override fun attachCollectionListener() {
        if (syncJob?.isActive == true) return

        Timber.i("Attaching collection listener")
        syncJob?.cancel()
        val ref = collectionsRef ?: return
        val hymnRef = hymnCollectionsRef ?: return

        syncJob = syncScope.launch {
            val collectionsJob = launch {
                ref.subscribe()
                    .flowOn(dispatcherProvider.io)
                    .catch { e -> Timber.e(e, "Collection sync failed") }
                    .collect { handleCollectionEvent(it) }
            }

            val hymnCollectionsJob = launch {
                hymnRef.subscribe()
                    .flowOn(dispatcherProvider.io)
                    .catch { e -> Timber.e(e, "Hymn Collections sync failed") }
                    .collect { handleHymnsEvent(it) }
            }

            joinAll(collectionsJob, hymnCollectionsJob)
        }
    }

    private suspend fun handleCollectionEvent(event: SnapShotEvent) {
        Timber.i("Received Collections event: ${event.javaClass.simpleName}")
        val entity = event.snapshot.toCollectionEntity() ?: return

        when (event) {
            is SnapShotEvent.Added,
            is SnapShotEvent.Modified -> {
                // For both ADDED and MODIFIED, we use INSERT OR REPLACE.
                // This handles new items and updates.
                collectionsDao.insert(entity)
            }

            is SnapShotEvent.Removed -> {
                // When Firestore signals REMOVED, it means the document was physically deleted
                // on the server OR the security rules made it invisible.
                collectionsDao.deleteCollection(entity.collectionId)
            }
        }
    }

    private suspend fun handleHymnsEvent(event: SnapShotEvent) {
        Timber.i("Received HymnCollections event: ${event.javaClass.simpleName}")
        val snapshot = event.snapshot
        val crossRef = snapshot.toCollectionHymnCrossRef() ?: return

        when (event) {
            is SnapShotEvent.Added,
            is SnapShotEvent.Modified -> {
                insetCollectionHymnCrossRef(crossRef)
            }

            is SnapShotEvent.Removed -> {
                collectionsDao.removeHymnFromCollection(
                    crossRef.collectionId,
                    crossRef.hymnId,
                )
            }
        }
    }

    private suspend fun insetCollectionHymnCrossRef(crossRef: CollectionHymnCrossRef) {
        // Attempt to find the collection locally
        var collection = collectionsDao.get(crossRef.collectionId)

        // We received the hymns event before the collection event,
        // This handles the race condition without hitting the network
        var attempts = 0
        while (collection == null && attempts < 5) {
            Timber.w("Waiting for collection ${crossRef.collectionId} to arrive...")
            delay(200) // Wait 200ms
            collection = collectionsDao.get(crossRef.collectionId)
            attempts++
        }
        if (collection != null) {
            collectionsDao.addHymnToCollection(crossRef)
        } else {
            Timber.e("Collection not found for hymn ${crossRef.hymnId}")
            // Handle race condition where collection is not persisted locally yet
            Timber.e("Skipping hymn insert: Parent Collection ${crossRef.collectionId} missing after waiting.")

            fetchAndInsertCollection(crossRef.collectionId)
            try {
                collectionsDao.addHymnToCollection(crossRef)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    private suspend fun fetchAndInsertCollection(id: String) {
        val entity = collectionsRef?.document(id)?.get()?.await()?.toCollectionEntity()
        if (entity != null) collectionsDao.insert(entity)
    }

    override fun detachCollectionListener() {
        syncJob?.cancel()
        syncJob = null
    }

    override suspend fun createCollection(
        title: String,
        description: String?,
        color: String
    ): Unit = withContext(dispatcherProvider.io) {
        // Use a random UUID to ensure a unique ID even if user is not signed in
        // (will be used as the Firestore Document ID later)
        val collectionId = UUID.randomUUID().toString()
        val creationTime = System.currentTimeMillis()

        collectionsRef?.let {
            val newCollectionRef = it.document(collectionId)

            val collectionData = mapOf(
                "collectionId" to collectionId,
                "title" to title,
                "description" to description,
                "created" to creationTime,
                "color" to color,
                "lastModified" to FieldValue.serverTimestamp(),
                "deleted" to false
            )

            // trigger real-time listeners
            newCollectionRef.set(collectionData).await()
        } ?: run {
            collectionsDao.insert(
                CollectionEntity(
                    collectionId = collectionId,
                    title = title,
                    description = description,
                    created = creationTime,
                    color = color,
                )
            )
        }
    }

    override suspend fun deleteCollection(collectionId: String): Unit =
        withContext(dispatcherProvider.io) {
            collectionsDao.deleteCollection(collectionId)
            collectionsRef?.document(collectionId)?.delete()?.await()
            hymnCollectionsRef?.whereEqualTo("collectionId", collectionId)
                ?.get()?.await()?.forEach { doc ->
                    doc.reference.delete()
                }
        }

    override suspend fun addHymnToCollection(ref: CollectionHymnCrossRef): Unit =
        withContext(dispatcherProvider.io) {
            hymnCollectionsRef?.let {
                val joinData = mapOf(
                    "collectionId" to ref.collectionId,
                    "hymnId" to ref.hymnId,
                )
                it.document("${ref.collectionId}_${ref.hymnId}")
                    .set(joinData)
                    .await()
            } ?: run {
                insetCollectionHymnCrossRef(ref)
            }
        }

    override suspend fun removeHymnFromCollection(
        collectionId: String,
        hymnId: String
    ): Unit = withContext(dispatcherProvider.io) {
        collectionsDao.removeHymnFromCollection(collectionId, hymnId)
        hymnCollectionsRef?.document("${collectionId}_${hymnId}")?.delete()?.await()
    }

    override suspend fun removeUserContent(): Unit = withContext(dispatcherProvider.io) {
        val hymnCollections = hymnCollectionsRef?.get()?.await()
        val collections = collectionsRef?.get()?.await()

        hymnCollections?.forEach { doc -> doc.reference.delete().await() }
        collections?.forEach { doc -> doc.reference.delete().await() }
    }
}

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class FirebaseContentSyncImpl(private val sync: FirebaseSync) : FirebaseContentSync {
    override fun signedIn() {
        sync.attachAfterSignIn()
    }

    override fun signedOut() {
        sync.detachCollectionListener()
    }

    override suspend fun deleteAccount() {
        sync.removeUserContent()
    }
}