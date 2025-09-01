// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.storage.db.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class HymnWithCollections(
    @Embedded val hymn: HymnEntity,
    @Relation(
        parentColumn = "hymnId",
        entityColumn = "collectionId",
        associateBy = Junction(CollectionHymnCrossRef::class)
    )
    val collections: List<CollectionEntity>
)