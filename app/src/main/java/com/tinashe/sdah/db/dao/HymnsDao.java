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

package com.tinashe.sdah.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.tinashe.sdah.model.HymnBook;
import com.tinashe.sdah.model.constants.Hymnal;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by tinashe on 2017/11/09.
 */

@Dao
public interface HymnsDao extends BaseDao<HymnBook> {

    @Query("SELECT * FROM " + Companion.getTABLE_NAME())
    Flowable<List<HymnBook>> listAllBooks();

    @Query("SELECT * FROM " + Companion.getTABLE_NAME() + " WHERE " + Companion.getCOLUMN_TYPE() + " = :type LIMIT 1")
    Flowable<HymnBook> findByType(@Hymnal int type);
}
