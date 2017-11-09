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

package com.tinashe.sdah.injection;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.tinashe.sdah.db.HymnalDatabase;
import com.tinashe.sdah.db.dao.HymnsDao;

import dagger.Module;
import dagger.Provides;

import static com.tinashe.sdah.db.HymnalDatabase.DB_NAME;

/**
 * Created by tinashe on 2017/11/09.
 */

@Module
class HymnalAppModule {

    @Provides
    Context provideContext(Application application) {
        return application;
    }

    @Provides
    HymnalDatabase provideDatabase(Context context) {
        return Room.databaseBuilder(context, HymnalDatabase.class, DB_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    HymnsDao provideHymnsDao(HymnalDatabase database) {
        return database.hymnsDao();
    }
}
