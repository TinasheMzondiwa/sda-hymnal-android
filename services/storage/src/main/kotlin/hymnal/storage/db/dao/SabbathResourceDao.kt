// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.storage.db.dao

import androidx.room.Dao
import androidx.room.Query
import hymnal.storage.db.entity.SabbathResourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SabbathResourceDao : BaseDao<SabbathResourceEntity> {

    @Query("""
        SELECT * FROM sabbath_resources 
        WHERE week = :week
        ORDER BY 
            CASE type
                WHEN 'SCRIPTURE' THEN 1  -- Priority 1: SCRIPTURE
                WHEN 'QUOTE' THEN 2      -- Priority 2: QUOTE 
                ELSE 3                   -- Default priority for any unexpected type
            END,
            id ASC                       -- Secondary sort
    """)
    fun get(week: Int): Flow<List<SabbathResourceEntity>>
}