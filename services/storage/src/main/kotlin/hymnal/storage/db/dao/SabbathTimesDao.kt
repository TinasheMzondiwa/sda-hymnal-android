// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.storage.db.dao

import androidx.room.Dao
import androidx.room.Query
import hymnal.storage.db.entity.SabbathTimesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SabbathTimesDao : BaseDao<SabbathTimesEntity> {

    @Query("SELECT * FROM sabbath_times WHERE id = :id")
    suspend fun get(id: String) : SabbathTimesEntity?

    @Query("SELECT * FROM sabbath_times LIMIT 1")
    suspend fun get(): SabbathTimesEntity?

    @Query("SELECT * FROM sabbath_times LIMIT 1")
    fun getFlow(): Flow<SabbathTimesEntity?>

    @Query("DELETE FROM sabbath_times")
    suspend fun clear()

    suspend fun replace(entity: SabbathTimesEntity) {
        clear()
        insert(entity)
    }
}