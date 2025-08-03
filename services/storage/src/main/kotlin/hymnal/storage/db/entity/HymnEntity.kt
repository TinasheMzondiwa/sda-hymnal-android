package hymnal.storage.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hymns")
data class HymnEntity(
    @PrimaryKey val id: String,
    val title: String,
    val number: Int,
    val majorKey: String?,
)
