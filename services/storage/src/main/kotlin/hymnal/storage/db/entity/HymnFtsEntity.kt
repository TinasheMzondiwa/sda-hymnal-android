// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.storage.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "hymns_fts")
@Fts4
data class HymnFtsEntity(
    @ColumnInfo(name = "hymnId")
    val hymnId: String,

    @ColumnInfo(name = "lyricsContent")
    val lyricsContent: String
)