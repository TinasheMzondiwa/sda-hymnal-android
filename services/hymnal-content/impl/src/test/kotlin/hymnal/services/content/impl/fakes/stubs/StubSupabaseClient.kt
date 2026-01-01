// Copyright (C) 2026 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.content.impl.fakes.stubs

import io.github.jan.supabase.AccessTokenProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.SupabaseSerializer
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.network.KtorSupabaseHttpClient
import io.github.jan.supabase.plugins.PluginManager
import kotlinx.coroutines.CoroutineDispatcher

open class StubSupabaseClient : SupabaseClient {

    override val supabaseHttpUrl: String
        get() = TODO("Not yet implemented")
    override val supabaseUrl: String
        get() = TODO("Not yet implemented")
    override val supabaseKey: String
        get() = TODO("Not yet implemented")
    override val pluginManager: PluginManager
        get() = TODO("Not yet implemented")
    override val httpClient: KtorSupabaseHttpClient
        get() = TODO("Not yet implemented")
    override val useHTTPS: Boolean
        get() = TODO("Not yet implemented")
    override val defaultSerializer: SupabaseSerializer
        get() = TODO("Not yet implemented")

    @SupabaseInternal
    override val accessToken: AccessTokenProvider?
        get() = TODO("Not yet implemented")

    @SupabaseInternal
    override val coroutineDispatcher: CoroutineDispatcher
        get() = TODO("Not yet implemented")

    override suspend fun close() {
        TODO("Not yet implemented")
    }
}