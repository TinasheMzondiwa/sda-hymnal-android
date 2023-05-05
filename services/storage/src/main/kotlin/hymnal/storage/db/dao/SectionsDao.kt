package hymnal.storage.db.dao

import androidx.room.Dao
import androidx.room.Query
import hymnal.storage.db.entity.SectionEntity

@Dao
interface SectionsDao : BaseDao<SectionEntity> {

    @Query("SELECT * FROM sections ORDER By firstHymn")
    fun listAll(): List<SectionEntity>

}