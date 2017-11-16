/*
 * Copyright (c) 2017. Tinashe Mzondiwa (www.tinashemzondiwa.co.za)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.tinashe.sdah.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import com.tinashe.sdah.db.dao.HymnsDao
import com.tinashe.sdah.model.Hymn
import com.tinashe.sdah.model.HymnBook
import com.tinashe.sdah.model.constants.Hymnal
import com.tinashe.sdah.util.ioThread
import java.io.IOException
import java.lang.reflect.Type
import java.nio.charset.Charset

/**
 * Created by tinashe on 2017/11/09.
 */

@Database(entities = arrayOf(HymnBook::class), version = 1)
@TypeConverters(HymnConverter::class)
abstract class HymnalDatabase : RoomDatabase() {

    abstract fun hymnsDao(): HymnsDao

    companion object {

        private const val DB_NAME = "hymnal-db"

        @Volatile private var INSTANCE: HymnalDatabase? = null

        fun getInstance(context: Context): HymnalDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        HymnalDatabase::class.java, DB_NAME)
                        .fallbackToDestructiveMigration()
                        // prepopulate the database after onCreate was called
                        .addCallback(object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                // insert the data on the IO Thread
                                ioThread {
                                    readHymnBook(context, Hymnal.ENGLISH)?.let {
                                        getInstance(context).hymnsDao().insertAll(it)
                                    }
                                    readHymnBook(context, Hymnal.ENGLISH_OLD)?.let {
                                        getInstance(context).hymnsDao().insertAll(it)
                                    }
                                }
                            }
                        })
                        .build()

        private fun readHymnBook(context: Context, @Hymnal type: Int): HymnBook? {
            val name = when (type) {
                Hymnal.ENGLISH -> "SDA Hymnal"
                Hymnal.ENGLISH_OLD -> "SDA hymnal (Old)"
                Hymnal.SPANISH -> ""
                else -> return null
            }

            val language = when (type) {
                Hymnal.ENGLISH -> "English"
                Hymnal.ENGLISH_OLD -> "English"
                Hymnal.SPANISH -> "Spanish"
                else -> ""
            }

            val path = when (type) {
                Hymnal.ENGLISH -> "sdah"
                Hymnal.ENGLISH_OLD -> "sdah_old"
                Hymnal.SPANISH -> ""
                else -> return null
            }

            val jsonString = readJsonFile(context, "json/$path.json")
            if (jsonString == null || jsonString.isBlank() || jsonString.isEmpty()) {
                return null
            }
            val hymnType: Type = object : TypeToken<List<Hymn>>() {
            }.type
            val hymns: List<Hymn> = Gson().fromJson(jsonString, hymnType)

            return HymnBook(type, name, language, hymns)

        }

        /**
         * Read a json file from assets
         */
        private fun readJsonFile(context: Context, fileName: String): String? {
            var jsonString: String? = null

            try {
                val `is` = context.assets.open(fileName)

                val size = `is`.available()

                val buffer = ByteArray(size)

                `is`.read(buffer)

                `is`.close()

                jsonString = String(buffer, Charset.forName("UTF8"))


            } catch (ex: IOException) {
                Log.e(HymnalDatabase::class.java.name, ex.message, ex)
            }


            return jsonString
        }
    }
}
