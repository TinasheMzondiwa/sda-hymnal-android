// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.storage.db.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class CollectionWithHymns(
    @Embedded val collection: CollectionEntity,
    @Relation(
        parentColumn = "collectionId",
        entityColumn = "hymnId",
        associateBy = Junction(CollectionHymnCrossRef::class)
    )
    val hymns: List<HymnEntity>
)