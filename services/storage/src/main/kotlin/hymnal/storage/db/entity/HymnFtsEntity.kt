package hymnal.storage.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "hymns_fts")
@Fts4
data class HymnFtsEntity(
    // This column will store the HymnEntity.id to link back to the original hymn
    @ColumnInfo(name = "hymnId")
    val hymnId: String,

    // This column will contain the concatenated lyrics and will be indexed for FTS
    @ColumnInfo(name = "lyricsContent")
    val lyricsContent: String
)