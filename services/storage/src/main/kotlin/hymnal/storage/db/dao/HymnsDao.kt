// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.storage.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import hymnal.storage.db.entity.HymnEntity
import hymnal.storage.db.entity.HymnFtsEntity
import hymnal.storage.db.entity.HymnWithLyrics
import hymnal.storage.db.entity.LyricPartEntity
import hymnal.storage.db.entity.RecentHymnEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HymnsDao : BaseDao<HymnEntity> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun internalInsertHymn(hymn: HymnEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun internalInsertLyricParts(lyricParts: List<LyricPartEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHymnFts(hymnFtsEntity: HymnFtsEntity)

    @Query("DELETE FROM lyric_parts WHERE hymnOwnerId = :hymnId")
    suspend fun deleteLyricPartsByHymnId(hymnId: String)

    @Query("DELETE FROM hymns_fts WHERE hymnId = :hymnId")
    suspend fun deleteHymnFtsByHymnId(hymnId: String)

    @Transaction
    suspend fun saveHymnAndLyrics(hymn: HymnEntity, lyricParts: List<LyricPartEntity>) {
        internalInsertHymn(hymn)

        // Clear existing lyric parts and FTS entry for this hymn
        deleteLyricPartsByHymnId(hymn.hymnId)
        deleteHymnFtsByHymnId(hymn.hymnId)

        // Insert new lyric parts
        val lyricsWithOwnerId = lyricParts.map { it.copy(hymnOwnerId = hymn.hymnId) }
        internalInsertLyricParts(lyricsWithOwnerId)

        // Prepare FTS data
        val lyricsText = lyricsWithOwnerId.joinToString(separator = "\n") { lyricPart ->
            lyricPart.lines.joinToString(separator = "\n")
        }
        // Content for the 'content' column (everything except the title)
        val otherContent = "${hymn.number}\n${hymn.majorKey.orEmpty()}\n${hymn.author.orEmpty()}\n$lyricsText"

        // Insert into the FTS table with separate columns
        insertHymnFts(
            HymnFtsEntity(
                hymnId = hymn.hymnId,
                title = hymn.title,
                lyricsContent = otherContent
            )
        )
    }

    @Transaction
    @Query("SELECT * FROM hymns WHERE hymnId = :hymnId")
    fun getHymnWithLyricsById(hymnId: String): Flow<HymnWithLyrics?>

    @Transaction
    @Query("SELECT * FROM hymns WHERE number = :number AND year = :year")
    suspend fun getHymnWithLyricsByNumber(number: Int, year: String): HymnWithLyrics?

    @Transaction
    @Query("SELECT * FROM hymns WHERE year = :year ORDER BY number ASC")
    suspend fun getAllHymns(year: String): List<HymnWithLyrics>

    @Transaction
    @Query("SELECT * FROM hymns WHERE year = :year ORDER BY number ASC")
    fun getAllHymnsWithLyrics(year: String): Flow<List<HymnWithLyrics>>

    @Transaction
    @Query("SELECT * FROM hymns WHERE number IN (:numbers) AND year = :year ORDER BY number ASC")
    fun getHymnsWithLyricsInRange(numbers: List<Int>, year: String): Flow<List<HymnWithLyrics>>

    /**
     * Searches hymns based on lyrics content, prioritizing title matches.
     * The query should be a valid FTS query string (e.g., "search term", "search* term", etc.).
     */
    @Transaction
    @Query("""
    SELECT h.* FROM hymns h
    JOIN (
        -- Priority 1: Matches in the TITLE
        SELECT hymnId, 1 AS rank_score 
        FROM hymns_fts 
        WHERE title MATCH :query
        
        UNION ALL
        
        -- Priority 2: Matches in the LYRICS
        SELECT hymnId, 2 AS rank_score 
        FROM hymns_fts 
        WHERE lyricsContent MATCH :query
    ) AS fts_hits ON h.hymnId = fts_hits.hymnId
    
    -- Grouping handles cases where a hymn matches BOTH title and lyrics.
    -- We keep the MIN(rank_score) so it counts as a "Priority 1" match.
    GROUP BY h.hymnId 
    ORDER BY MIN(fts_hits.rank_score) ASC, h.number ASC
""")
    fun searchLyrics(query: String): Flow<List<HymnWithLyrics>>

    /**
     * Get recent hymns: Joins the tables and sorts by time
     */
    @Query(
        """
        SELECT hymns.* FROM hymns 
        INNER JOIN recent_hymns ON hymns.hymnId = recent_hymns.hymnId 
        ORDER BY recent_hymns.accessedAt DESC 
        LIMIT :limit
    """
    )
    fun getRecentHymns(limit: Int = 10): Flow<List<HymnWithLyrics>>

    /**
     * Add/Update history: "REPLACE" updates the timestamp if the ID already exists
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentHymn(recent: RecentHymnEntity)

    /**
     * Maintenance: Keep history clean by deleting old entries
     */
    @Query("DELETE FROM recent_hymns WHERE hymnId NOT IN (SELECT hymnId FROM recent_hymns ORDER BY accessedAt DESC LIMIT 50)")
    suspend fun trimRecentHistory()
}

