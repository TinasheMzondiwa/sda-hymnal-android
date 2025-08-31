// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.prefs.impl.education

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import hymnal.libraries.coroutines.DispatcherProvider
import hymnal.services.prefs.education.Education
import hymnal.services.prefs.education.UserOrientation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "hymnal_education")

@ContributesBinding(scope = AppScope::class)
@Inject
class UserOrientationImpl(
    private val context: Context,
    private val dispatcherProvider: DispatcherProvider
) : UserOrientation {

    override suspend fun track(education: Education) {
        withContext(dispatcherProvider.io) {
            val key = stringPreferencesKey(education.key)
            val now = System.currentTimeMillis()

            context.dataStore.edit { prefs ->
                val parsed = prefs[key]?.let(::decodePair)
                val currentCount = parsed?.first ?: 0
                val newCount = (currentCount + 1).coerceAtMost(education.timesToShow)
                prefs[key] = encodePair(newCount, now)
            }
        }
    }

    override fun shouldShow(education: Education): Flow<Boolean> {
        val key = stringPreferencesKey(education.key)
        val installTime = getFirstInstallTime(context)
        val eligibleAfterMs = installTime + TimeUnit.DAYS.toMillis(education.showAfterDays.toLong())

        return context.dataStore.data.map { prefs ->
            val now = System.currentTimeMillis()
            val eligibleByTime = now >= eligibleAfterMs

            val (shownCount, _) = prefs[key]?.let(::decodePair) ?: (0 to 0L)
            val underLimit = shownCount < education.timesToShow

            eligibleByTime && underLimit
        }.flowOn(dispatcherProvider.io)
    }

    // ----- Helpers -----

    /** Encoding timesShown and lastShown as "int:long" */
    private fun encodePair(count: Int, lastShownEpochMs: Long): String =
        "$count:$lastShownEpochMs"

    /** Decoding timesShown and lastShown with safety. */
    private fun decodePair(value: String): Pair<Int, Long>? {
        val parts = value.split(':')
        if (parts.size != 2) return null
        val count = parts.getOrNull(0)?.toIntOrNull() ?: return null
        val ts = parts.getOrNull(1)?.toLongOrNull() ?: return null
        return count to ts
    }

    private fun getFirstInstallTime(ctx: Context): Long {
        val pm = ctx.packageManager
        val pkg = ctx.packageName
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getPackageInfo(pkg, PackageManager.PackageInfoFlags.of(0)).firstInstallTime
        } else {
            @Suppress("DEPRECATION")
            pm.getPackageInfo(pkg, 0).firstInstallTime
        }
    }
}