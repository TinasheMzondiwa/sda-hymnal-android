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

}