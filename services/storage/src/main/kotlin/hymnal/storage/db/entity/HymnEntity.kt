package hymnal.storage.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hymns")
data class HymnEntity(
    @PrimaryKey val id: Int = 0,
    val title: String,
    val number: Int,
    val content: String,
    val book: String,
    val majorKey: String?,
    val editedContent: String?
)
