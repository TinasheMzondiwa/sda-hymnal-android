// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.storage.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "recent_hymns",
    foreignKeys = [
        ForeignKey(
            entity = HymnEntity::class,
            parentColumns = ["hymnId"],
            childColumns = ["hymnId"],
            onDelete = ForeignKey.CASCADE // If a hymn is deleted, remove it from history
        )
    ]
)
data class RecentHymnEntity(
    @PrimaryKey val hymnId: String,
    val accessedAt: Long = System.currentTimeMillis(),
)