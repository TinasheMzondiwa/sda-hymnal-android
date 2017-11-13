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

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tinashe.sdah.model.Hymn

/**
 * Created by tinashe on 2017/07/02.
 */

class HymnConverter {

    @TypeConverter
    fun jsonToHymns(jsonString: String): List<Hymn>? {

        val gson = Gson()
        val type = object : TypeToken<List<Hymn>>() {

        }.type

        return gson.fromJson<List<Hymn>>(jsonString, type)
    }

    @TypeConverter
    fun hymnsToJson(hymns: List<Hymn>): String {
        val gson = Gson()

        return gson.toJson(hymns)
    }
}
