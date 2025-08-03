package hymnal.storage.db.entity

import androidx.room.Embedded
import androidx.room.Relation
import hymnal.services.model.Hymn
import hymnal.services.model.HymnLyrics

data class HymnWithLyrics(
    @Embedded val hymn: HymnEntity,
    @Relation(
        parentColumn = "id",      // Primary key in HymnEntity (HymnEntity.id)
        entityColumn = "hymnOwnerId", // Foreign key in LyricPartEntity
        entity = LyricPartEntity::class
    )
    val lyricParts: List<LyricPartEntity>
) {
    fun toDomainHymn(): Hymn {
        return Hymn(
            index = hymn.id,
            title = hymn.title,
            number = hymn.number,
             majorKey = hymn.majorKey,
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
