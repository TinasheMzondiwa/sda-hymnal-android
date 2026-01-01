// Copyright (C) 2026 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.content.impl.fakes

import hymnal.storage.db.dao.HymnsDao
import hymnal.storage.db.entity.HymnEntity
import hymnal.storage.db.entity.HymnFtsEntity
import hymnal.storage.db.entity.HymnWithLyrics
import hymnal.storage.db.entity.LyricPartEntity
import hymnal.storage.db.entity.RecentHymnEntity
import kotlinx.coroutines.flow.Flow

class FakeHymnsDao : FakeBaseDao<HymnEntity>(), HymnsDao {

    val insertedHymns = mutableListOf<HymnEntity>()

    override suspend fun internalInsertHymn(hymn: HymnEntity): Long {
        insertedHymns.add(hymn)
        return hymn.number.toLong()
    }

    override suspend fun internalInsertLyricParts(lyricParts: List<LyricPartEntity>) {
    }

    override suspend fun insertHymnFts(hymnFtsEntity: HymnFtsEntity) {

    }

    override suspend fun deleteLyricPartsByHymnId(hymnId: String) {

    }

    override suspend fun deleteHymnFtsByHymnId(hymnId: String) {

    }

    override fun getHymnWithLyricsById(hymnId: String): Flow<HymnWithLyrics?> {
        TODO("Not yet implemented")
    }

    override suspend fun getHymnWithLyricsByNumber(
        number: Int,
        year: String
    ): HymnWithLyrics? {
        TODO("Not yet implemented")
    }

    override suspend fun getAllHymns(year: String): List<HymnWithLyrics> {
        val hymnsForYear = items.filter { it.year == year }
        return hymnsForYear.map {
            HymnWithLyrics(it, emptyList())
        }
    }

    override fun getAllHymnsWithLyrics(year: String): Flow<List<HymnWithLyrics>> {
        TODO("Not yet implemented")
    }

    override fun getHymnsWithLyricsInRange(
        numbers: List<Int>,
        year: String
    ): Flow<List<HymnWithLyrics>> {
        TODO("Not yet implemented")
    }

    override fun searchLyrics(query: String): Flow<List<HymnWithLyrics>> {
        TODO("Not yet implemented")
    }

    override fun getRecentHymns(limit: Int): Flow<List<HymnWithLyrics>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertRecentHymn(recent: RecentHymnEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun trimRecentHistory() {
        TODO("Not yet implemented")
    }

    override suspend fun get(index: String): HymnWithLyrics? {
        TODO("Not yet implemented")
    }
}