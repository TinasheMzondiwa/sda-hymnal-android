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

package com.tinashe.sdah.injection

import android.arch.persistence.room.Room
import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.tinashe.sdah.HymnalApp
import com.tinashe.sdah.db.HymnalDatabase
import com.tinashe.sdah.db.HymnalDatabase.Companion.DB_NAME
import com.tinashe.sdah.db.dao.HymnsDao
import com.tinashe.sdah.prefs.HymnalPrefs
import com.tinashe.sdah.prefs.HymnalPrefsImpl
import com.tinashe.sdah.retrofit.RestClient
import com.tinashe.sdah.retrofit.UnSplashApi
import com.tinashe.sdah.util.RxSchedulers
import dagger.Module
import dagger.Provides

/**
 * Created by tinashe on 2017/11/09.
 */

@Module
internal class HymnalAppModule {

    @Provides
    fun provideContext(application: HymnalApp): Context {
        return application
    }

    @Provides
    fun provideRxSchedulers(): RxSchedulers {
        return RxSchedulers()
    }

    @Provides
    fun providePrefs(context: Context): HymnalPrefs {
        return HymnalPrefsImpl(context)
    }

    @Provides
    fun provideDatabase(context: Context): HymnalDatabase {
        return Room.databaseBuilder(context, HymnalDatabase::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
    }

    @Provides
    fun provideFirebaseDb(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Provides
    fun provideHymnsDao(database: HymnalDatabase): HymnsDao {
        return database.hymnsDao()
    }

    @Provides
    fun provideUnsSplashApi(): UnSplashApi {
        return RestClient.createService(UnSplashApi::class.java,
                "https://api.unsplash.com")
    }

}
