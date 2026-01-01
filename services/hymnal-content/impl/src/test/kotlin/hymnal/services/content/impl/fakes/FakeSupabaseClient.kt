// Copyright (C) 2026 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.content.impl.fakes

import hymnal.services.content.impl.fakes.stubs.StubSupabaseClient
import io.github.jan.supabase.plugins.PluginManager
import io.github.jan.supabase.storage.Storage

class FakeSupabaseClient : StubSupabaseClient() {

    override val pluginManager: PluginManager by lazy {
        PluginManager(
            mapOf(
                Storage.key to FakeStorage()
            )
        )
    }
}