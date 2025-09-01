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

        // Prepare FTS data including title, number, and lyrics
        val lyricsText = lyricsWithOwnerId.joinToString(separator = "\n") { lyricPart ->
            lyricPart.lines.joinToString(separator = "\n")
        }
        // Combine title, number (as string), and lyrics for FTS
        val searchableContent = "${hymn.title}\n${hymn.number}\n$lyricsText"

        if (searchableContent.isNotBlank()) {
            insertHymnFts(HymnFtsEntity(hymnId = hymn.hymnId, lyricsContent = searchableContent))
        }
    }

    @Transaction
    @Query("SELECT * FROM hymns WHERE hymnId = :hymnId")
    fun getHymnWithLyricsById(hymnId: String): Flow<HymnWithLyrics?>

    @Transaction
    @Query("SELECT * FROM hymns WHERE number = :number")
    suspend fun getHymnWithLyricsByNumber(number: Int): HymnWithLyrics?

    @Transaction
    @Query("SELECT * FROM hymns")
    suspend fun getAllHymns(): List<HymnWithLyrics>

    @Transaction
    @Query("SELECT * FROM hymns")
    fun getAllHymnsWithLyrics(): Flow<List<HymnWithLyrics>>

    /**
     * Searches hymns based on lyrics content.
     * The query should be a valid FTS query string (e.g., "search term", "search* term", etc.).
     */
    @Transaction
    @Query("""
        SELECT h.* FROM hymns h
        JOIN hymns_fts fts ON h.hymnId = fts.hymnId
        WHERE fts.lyricsContent MATCH :query
    """)
    fun searchLyrics(query: String): Flow<List<HymnWithLyrics>>
}

