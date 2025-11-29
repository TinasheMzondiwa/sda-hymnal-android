// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.storage.db.dao

import androidx.room.Dao
import androidx.room.Query
import hymnal.storage.db.entity.SabbathResourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SabbathResourceDao : BaseDao<SabbathResourceEntity> {

    @Query("SELECT * FROM sabbath_resources WHERE week = :week")
    fun get(week: Int): Flow<List<SabbathResourceEntity>>
}