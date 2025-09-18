// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.storage.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hymns")
data class HymnEntity(
    @PrimaryKey val hymnId: String,
    val title: String,
    val number: Int,
    val majorKey: String?,
    val author: String?,
)
