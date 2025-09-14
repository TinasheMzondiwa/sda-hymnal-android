// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.storage.db.dao

import androidx.room.Dao
import androidx.room.Query
import hymnal.storage.db.entity.SabbathTimesEntity

@Dao
interface SabbathTimesDao : BaseDao<SabbathTimesEntity> {

    @Query("SELECT * FROM sabbath_times WHERE id = :id")
    suspend fun get(id: String) : SabbathTimesEntity?
}