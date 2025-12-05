// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.storage.db.entity

import androidx.room.Embedded
import androidx.room.Relation
import hymnal.services.model.Hymn
import hymnal.services.model.HymnLyrics

data class HymnWithLyrics(
    @Embedded val hymn: HymnEntity,
    @Relation(
        parentColumn = "hymnId",      // Primary key in HymnEntity (HymnEntity.hymnId)
        entityColumn = "hymnOwnerId", // Foreign key in LyricPartEntity
        entity = LyricPartEntity::class
    )
    val lyricParts: List<LyricPartEntity>
) {
    fun toDomainHymn(): Hymn {
        return Hymn(
            index = hymn.hymnId,
            title = hymn.title,
            number = hymn.number,
            majorKey = hymn.majorKey,
            author = hymn.author,
            authorLink = hymn.authorLink,
            year = hymn.year,
            lyrics = lyricParts.map { part ->
                when (part.type) {
                    DbLyricType.VERSE -> HymnLyrics.Verse(
                        index = part.itemIndex,
                        lines = part.lines
                    )

                    DbLyricType.CHORUS -> HymnLyrics.Chorus(
                        index = part.itemIndex,
                        lines = part.lines
                    )
                }
            }
        )
    }
}
