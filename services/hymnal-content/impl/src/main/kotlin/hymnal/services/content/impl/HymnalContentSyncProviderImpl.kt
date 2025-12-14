// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.content.impl

import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import androidx.core.content.edit
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import hymnal.libraries.coroutines.DispatcherProvider
import hymnal.libraries.coroutines.Scopable
import hymnal.libraries.coroutines.ioScopable
import hymnal.libraries.model.Hymnal
import hymnal.services.content.HymnalContentSyncProvider
import hymnal.services.content.impl.ext.downloadHymns
import hymnal.services.model.Hymn
import hymnal.services.model.HymnLyrics
import hymnal.storage.db.dao.CollectionDao
import hymnal.storage.db.dao.HymnsDao
import hymnal.storage.db.entity.CollectionEntity
import hymnal.storage.db.entity.CollectionHymnCrossRef
import hymnal.storage.db.entity.DbLyricType
import hymnal.storage.db.entity.HymnEntity
import hymnal.storage.db.entity.LyricPartEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.util.Locale

@ContributesBinding(scope = AppScope::class, binding<HymnalContentSyncProvider>())
@Inject
class HymnalContentSyncProviderImpl(
    private val appContext: Context,
    private val collectionsDao: CollectionDao,
    private val hymnsDao: HymnsDao,
    private val dispatcherProvider: DispatcherProvider,
    private val supabase: SupabaseClient,
) : HymnalContentSyncProvider, Scopable by ioScopable(dispatcherProvider) {

    private val exceptionLogger = CoroutineExceptionHandler { _, e -> Timber.e(e) }

    private val sharedPreferences: SharedPreferences by lazy {
        appContext.getSharedPreferences("app_migration_prefs", Context.MODE_PRIVATE)
    }

    override fun invoke() {
        scope.launch(exceptionLogger) {
            Hymnal.entries.forEach { syncHymnal(it) }

            maybePortLegacyFavorites()
        }
    }

    private suspend fun syncHymnal(hymnal: Hymnal) {
        val hymns = hymnsDao.getAllHymns(hymnal.year)

        // Download hymns if not found in the database or we need to migrate NewHymnal to v2
        if (hymns.isEmpty() || (hymnal == Hymnal.NewHymnal && hymns.any { it.hymn.revision == 1 })) {
            val downloadedHymns = supabase.storage.downloadHymns(hymnal)
            if (downloadedHymns != null) {
                saveHymnsToDatabase(downloadedHymns)
            } else {
                Timber.e("Failed to download hymns for ${hymnal.title}.")
                // We'll retry on next launch.
            }
        } else {
            Timber.i("Found ${hymns.size} hymns for ${hymnal.title} in the database, no sync needed.")
        }
    }

    private suspend fun saveHymnsToDatabase(hymns: List<Hymn>) {
        hymns.forEach { hymn ->
            val entity = HymnEntity(
                hymnId = hymn.index,
                number = hymn.number,
                title = hymn.title,
                majorKey = hymn.majorKey,
                author = hymn.author,
                authorLink = hymn.authorLink,
                year = hymn.year,
                revision = hymn.revision
            )

            val lyricParts = hymn.lyrics.map { lyrics ->
                val type = when (lyrics) {
                    is HymnLyrics.Chorus -> DbLyricType.CHORUS
                    is HymnLyrics.Verse -> DbLyricType.VERSE
                }

                LyricPartEntity(
                    hymnOwnerId = entity.hymnId,
                    type = type,
                    itemIndex = lyrics.index,
                    lines = lyrics.lines,
                )
            }

            hymnsDao.saveHymnAndLyrics(
                hymn = entity,
                lyricParts = lyricParts,
            )
        }
    }

    private suspend fun maybePortLegacyFavorites() {
        // Check the flag before running the logic
        val alreadyPorted = sharedPreferences.getBoolean(KEY_LEGACY_FAVORITES_PORTED, false)
        if (alreadyPorted) {
            Timber.i("Legacy favorites have already been ported. Skipping.")
            return
        }

        val ported = getLegacyFavorites(appContext)
        if (ported.isEmpty()) {
            sharedPreferences.edit {
                putBoolean(KEY_LEGACY_FAVORITES_PORTED, true)
            }
            return
        }

        val collection = CollectionEntity(
            collectionId = LEGACY_COLLECTION_ID,
            title = LEGACY_COLLECTION_TITLE,
            description = null,
            created = System.currentTimeMillis(),
            color = "#FFB300",
        )

        collectionsDao.insert(collection)

        ported.forEach { favorite ->
            val hymn = LegacyHymnal.from(favorite.language) ?: return@forEach
            val hymnId = when (hymn) {
                LegacyHymnal.New -> String.format(Locale.getDefault(), "%03d", favorite.number)
                LegacyHymnal.Old -> "${1000 + favorite.number}"
            }

            val ref = CollectionHymnCrossRef(
                collectionId = collection.collectionId,
                hymnId = hymnId,
            )

            collectionsDao.addHymnToCollection(ref)
        }

        sharedPreferences.edit {
            putBoolean(KEY_LEGACY_FAVORITES_PORTED, true)
        }

    }

    private data class PortedFavorite(val number: Int, val language: String)

    private enum class LegacyHymnal(val code: String) {
        New("eng"),
        Old("old");

        companion object {
            fun from(code: String) = entries.associateBy(LegacyHymnal::code)[code]
        }
    }

    /**
     * Extracts specific columns from the legacy 'favoritesManager' database
     */
    private fun getLegacyFavorites(context: Context): List<PortedFavorite> {
        val portedList = mutableListOf<PortedFavorite>()

        // 1. Define the exact Legacy DB Name
        val dbFile: File = context.getDatabasePath(LEGACY_DB_NAME)

        // If the old file doesn't exist, return empty list
        if (!dbFile.exists()) {
            return emptyList()
        }

        // 2. Open the database in Read-Only mode to avoid locking issues
        var db: SQLiteDatabase? = null
        try {
            db = SQLiteDatabase.openDatabase(
                dbFile.absolutePath,
                null,
                SQLiteDatabase.OPEN_READONLY
            )

            // 3. Query only the columns we need: 'number' and 'language'
            // We use the table name "favorites" from your legacy code
            val cursor = db.rawQuery("SELECT number, language FROM favorites", null)

            val numberColIdx = cursor.getColumnIndex("number")
            val langColIdx = cursor.getColumnIndex("language")

            // Safety check: ensure columns exist
            if (numberColIdx != -1 && langColIdx != -1) {
                while (cursor.moveToNext()) {
                    val numberStr = cursor.getString(numberColIdx)
                    val languageStr = cursor.getString(langColIdx)

                    // 4. Convert String to Int safeley
                    val numberInt = numberStr.toIntOrNull()

                    if (numberInt != null) {
                        portedList.add(PortedFavorite(numberInt, languageStr))
                    }
                }
            }
            cursor.close()

        } catch (e: Exception) {
            Timber.e(e)
            // I tried
        } finally {
            db?.close()
        }

        return portedList
    }

    private companion object {
        const val LEGACY_COLLECTION_ID = "legacy-favourites"
        const val LEGACY_COLLECTION_TITLE = "Legacy Favourites"
        const val LEGACY_DB_NAME = "favoritesManager"
        const val KEY_LEGACY_FAVORITES_PORTED = "legacy_favorites_ported"
    }
}