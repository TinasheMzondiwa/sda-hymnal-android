// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.storage.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sabbath_resources")
data class SabbathResourceEntity(
    @PrimaryKey val id: String,
    val week: Int,
    val type: Type,
    val reference: String,
    val text: String,
    val section: String?
) {

    enum class Type {
        SCRIPTURE, QUOTE
    }
}