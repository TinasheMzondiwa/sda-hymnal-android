// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.storage.di

import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import hymnal.storage.db.HymnalDatabase
import hymnal.storage.db.dao.CollectionDao
import hymnal.storage.db.dao.HymnsDao
import hymnal.storage.db.dao.SabbathResourceDao
import hymnal.storage.db.dao.SabbathTimesDao

@ContributesTo(AppScope::class)
@BindingContainer
object StorageBindings {

    @Provides
    @SingleIn(AppScope::class)
    fun provideHymnsDao(
        context: Context
    ): HymnsDao = HymnalDatabase.getInstance(context).hymnsDao()

    @Provides
    @SingleIn(AppScope::class)
    fun provideCollectionDao(
        context: Context
    ): CollectionDao = HymnalDatabase.getInstance(context).collectionsDao()

    @Provides
    @SingleIn(AppScope::class)
    fun provideSabbathTimesDao(
        context: Context
    ): SabbathTimesDao = HymnalDatabase.getInstance(context).sabbathTimesDao()

    @Provides
    @SingleIn(AppScope::class)
    fun provideSabbathResourcesDao(
        context: Context
    ): SabbathResourceDao = HymnalDatabase.getInstance(context).sabbathResourceDao()

}