/*
 * Copyright (c) 2017.
 * Tinashe Mzondiwa (www.tinashemzondiwa.co.za)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.tinashe.sdah.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters

import com.tinashe.sdah.db.converters.HymnConverter
import com.tinashe.sdah.model.HymnBook.Companion.TABLE_NAME
import com.tinashe.sdah.model.constants.Hymnal

import java.io.Serializable

/**
 * Created by tinashe on 2017/07/02.
 */

@Entity(tableName = TABLE_NAME)
@TypeConverters(HymnConverter::class)
class HymnBook : Serializable {

    @Hymnal
    @PrimaryKey
    @ColumnInfo(name = COLUMN_TYPE)
    @get:Hymnal
    var type: Int = 0

    var name: String? = null

    var hymns: List<Hymn>? = null

    companion object {

        const val TABLE_NAME = "hymnbooks"
        const val COLUMN_TYPE = "type"
    }
}
