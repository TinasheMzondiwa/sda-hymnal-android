// Copyright (C) 2026 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.content.impl.fakes

import hymnal.services.content.impl.fakes.stubs.StubSupabaseClient
import io.github.jan.supabase.logging.LogLevel
import io.github.jan.supabase.logging.SupabaseLogger
import io.github.jan.supabase.logging.SupabaseLoggingProcessor
import io.github.jan.supabase.plugins.PluginManager
import io.github.jan.supabase.storage.Storage

class FakeSupabaseClient : StubSupabaseClient() {

    override val logger: SupabaseLogger = SupabaseLogger(
        level = LogLevel.NONE,
        tag = "FakeSupabaseClient",
        processorFactory = {
            object : SupabaseLoggingProcessor {
                override fun isEnabled(level: LogLevel): Boolean = false
                override fun processLog(level: LogLevel, tag: String, throwable: Throwable?, message: String) = Unit
            }
        }
    )

    override val pluginManager: PluginManager by lazy {
        PluginManager(
            mapOf(
                Storage.key to FakeStorage()
            ),
            logger
        )
    }
}
