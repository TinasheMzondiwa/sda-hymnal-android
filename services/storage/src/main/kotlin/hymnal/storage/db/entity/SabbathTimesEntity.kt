// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.storage.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sabbath_times")
data class SabbathTimesEntity(
    @PrimaryKey val id: String,
    val friday: String,
    val saturday: String,
    val location: String,
)
