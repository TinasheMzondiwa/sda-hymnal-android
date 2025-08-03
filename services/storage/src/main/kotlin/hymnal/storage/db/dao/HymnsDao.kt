package hymnal.storage.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import hymnal.storage.db.entity.HymnEntity
import hymnal.storage.db.entity.HymnWithLyrics
import hymnal.storage.db.entity.LyricPartEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HymnsDao : BaseDao<HymnEntity> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHymn(hymn: HymnEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLyricParts(lyricParts: List<LyricPartEntity>)

    @Transaction
    @Query("SELECT * FROM hymns WHERE id = :hymnId")
    suspend fun getHymnWithLyricsById(hymnId: String): HymnWithLyrics?

    @Transaction
    @Query("SELECT * FROM hymns")
    fun getAllHymnsWithLyrics(): Flow<List<HymnWithLyrics>>
}
