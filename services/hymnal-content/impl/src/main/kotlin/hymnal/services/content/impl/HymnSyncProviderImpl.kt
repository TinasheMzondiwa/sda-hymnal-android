// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.content.impl

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import hymnal.libraries.coroutines.DispatcherProvider
import hymnal.libraries.coroutines.Scopable
import hymnal.libraries.coroutines.ioScopable
import hymnal.libraries.model.HymnalAppConfig
import hymnal.services.content.HymnSyncProvider
import hymnal.storage.db.dao.HymnsDao
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.CoroutineExceptionHandler
import timber.log.Timber

@ContributesBinding(scope = AppScope::class, binding<HymnSyncProvider>())
@Inject
class HymnSyncProviderImpl(
    private val appConfig: HymnalAppConfig,
    private val hymnsDao: HymnsDao,
    private val dispatcherProvider: DispatcherProvider,
    private val supabase: SupabaseClient,
) : HymnSyncProvider, Scopable by ioScopable(dispatcherProvider) {

    private val exceptionLogger = CoroutineExceptionHandler { _, e -> Timber.e(e) }

    override fun invoke(index: String) {
        TODO("Not yet implemented")
    }
}