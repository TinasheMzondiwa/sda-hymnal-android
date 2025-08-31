// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package app.hymnal.initializer

import android.content.Context
import androidx.startup.Initializer
import app.hymnal.HymnalApplication

class HymnalContentInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        val app = context.applicationContext as HymnalApplication
        app.appGraph.contentSyncProvider()
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> {
        return listOf(TimberInitializer::class.java)
    }
}