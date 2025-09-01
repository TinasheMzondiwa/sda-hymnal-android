// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.storage.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import hymnal.storage.db.dao.CollectionDao
import hymnal.storage.db.dao.HymnsDao
import hymnal.storage.db.entity.CollectionEntity
import hymnal.storage.db.entity.CollectionHymnCrossRef
import hymnal.storage.db.entity.HymnEntity
import hymnal.storage.db.entity.HymnFtsEntity
import hymnal.storage.db.entity.LyricPartEntity

@Database(
    entities = [
        HymnEntity::class,
        LyricPartEntity::class,
        HymnFtsEntity::class,
        CollectionEntity::class,
        CollectionHymnCrossRef::class,
    ],
    version = 1,
    exportSchema = true,
)
internal abstract class HymnalDatabase : RoomDatabase() {

    abstract fun hymnsDao(): HymnsDao

    abstract fun collectionsDao(): CollectionDao

    companion object {
        private const val DATABASE_NAME = "hymnal.db"

        @Volatile
        private var INSTANCE: HymnalDatabase? = null

        fun getInstance(context: Context): HymnalDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context): HymnalDatabase =
            Room.databaseBuilder(context, HymnalDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
    }
}