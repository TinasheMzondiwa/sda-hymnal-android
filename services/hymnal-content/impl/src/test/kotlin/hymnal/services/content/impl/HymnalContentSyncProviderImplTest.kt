// Copyright (C) 2026 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.content.impl

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import hymnal.libraries.model.Hymnal
import hymnal.services.content.HymnalContentSyncProvider
import hymnal.services.content.impl.fakes.FakeCollectionDao
import hymnal.services.content.impl.fakes.FakeHymnsDao
import hymnal.services.content.impl.fakes.FakeSupabaseClient
import hymnal.services.content.impl.fakes.TestDispatcherProvider
import hymnal.storage.db.entity.HymnEntity
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HymnalContentSyncProviderImplTest {

    private val fakeCollectionsDao = FakeCollectionDao()
    private val fakeHymnsDao = FakeHymnsDao()

    private val syncProvider: HymnalContentSyncProvider = HymnalContentSyncProviderImpl(
        appContext = ApplicationProvider.getApplicationContext(),
        collectionsDao = fakeCollectionsDao,
        hymnsDao = fakeHymnsDao,
        dispatcherProvider = TestDispatcherProvider(),
        supabase = FakeSupabaseClient(),
    )

    @Test
    fun `sync downloads hymns when hymnal is empty`() = runTest {
        insertHymns(Hymnal.OldHymnal)
        insertHymns(Hymnal.Choruses)

        syncProvider()

        val inserted = fakeHymnsDao.insertedHymns.filter { it.year == Hymnal.NewHymnal.year }
        assertThat(inserted.size).isEqualTo(Hymnal.NewHymnal.hymns)
    }

    @Test
    fun `sync downloads hymns when hymnal is partially downloaded`() = runTest {
        insertHymns(Hymnal.NewHymnal)
        insertHymns(Hymnal.OldHymnal)
        insertHymns(Hymnal.Choruses, partialSize = 15)

        syncProvider()

        val inserted = fakeHymnsDao.insertedHymns.filter { it.year == Hymnal.Choruses.year }
        assertThat(inserted.size).isEqualTo(Hymnal.Choruses.hymns)
    }

    private suspend fun insertHymns(hymnal: Hymnal, partialSize: Int? = null) {
        for (i in 1..(partialSize ?: hymnal.hymns)) {
            val hymn = HymnEntity(
                hymnId = "$i",
                number = i,
                title = "Hymn $i",
                majorKey = null,
                author = null,
                authorLink = null,
                year = hymnal.year,
                revision = 1
            )
            fakeHymnsDao.insert(hymn)
        }
    }
}