// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.storage.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "collection_hymn_join",
    primaryKeys = ["collectionId", "hymnId"],
    foreignKeys = [
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["collectionId"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.CASCADE // If a collection is deleted, remove its associations
        ),
        ForeignKey(
            entity = HymnEntity::class,
            parentColumns = ["hymnId"],
            childColumns = ["hymnId"],
            onDelete = ForeignKey.CASCADE // If a hymn is deleted, remove its associations
        )
    ],
    indices = [
        Index(value = ["collectionId"]),
        Index(value = ["hymnId"])
    ]
)
data class CollectionHymnCrossRef(
    val collectionId: String,
    val hymnId: String
)