package hymnal.storage.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import hymnal.storage.db.converter.StringListConverter

@Entity(
    tableName = "lyric_parts",
    foreignKeys = [
        ForeignKey(
            entity = HymnEntity::class,
            parentColumns = ["id"], // Primary key in HymnEntity is 'id: String'
            childColumns = ["hymnOwnerId"], // Foreign key in LyricPartEntity
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["hymnOwnerId"])]
)
@TypeConverters(StringListConverter::class)
data class LyricPartEntity(
    @PrimaryKey(autoGenerate = true) val partId: Long = 0,
    val hymnOwnerId: String, // Matches the type of HymnEntity.id
    val type: DbLyricType,
    val itemIndex: Int,
    val lines: List<String>
)
