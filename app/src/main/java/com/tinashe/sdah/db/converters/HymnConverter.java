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

package com.tinashe.sdah.db.converters;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tinashe.sdah.model.Hymn;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by tinashe on 2017/07/02.
 */

public class HymnConverter {

    @TypeConverter
    public static List<Hymn> jsonToHymns(String jsonString) {

        Gson gson = new Gson();
        Type type = new TypeToken<List<Hymn>>() {
        }.getType();

        return gson.fromJson(jsonString, type);
    }

    @TypeConverter
    public static String hymnsToJson(List<Hymn> hymns) {
        Gson gson = new Gson();

        return gson.toJson(hymns);
    }
}
