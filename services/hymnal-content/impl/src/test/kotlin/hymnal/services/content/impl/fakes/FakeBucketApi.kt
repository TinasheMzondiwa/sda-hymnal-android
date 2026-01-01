// Copyright (C) 2026 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.content.impl.fakes

import hymnal.libraries.model.Hymnal
import hymnal.services.content.impl.ext.FILE_PATH
import hymnal.services.content.impl.ext.FILE_PATH_CHORUSES
import hymnal.services.content.impl.ext.FILE_PATH_OLD_HYMN
import hymnal.services.content.impl.fakes.stubs.StubBucketApi
import hymnal.services.content.impl.model.RemoteHymn
import io.github.jan.supabase.storage.DownloadOptionBuilder
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets

class FakeBucketApi : StubBucketApi() {

    override suspend fun downloadAuthenticated(
        path: String,
        options: DownloadOptionBuilder.() -> Unit
    ): ByteArray {
        return convertToByteArray(path)
    }

    private fun convertToByteArray(path: String): ByteArray {
        val hymnal = when (path) {
            FILE_PATH -> Hymnal.NewHymnal
            FILE_PATH_OLD_HYMN -> Hymnal.OldHymnal
            FILE_PATH_CHORUSES -> Hymnal.Choruses
            else -> return ByteArray(0)
        }

        val hymns = mutableListOf<RemoteHymn>()
        for (i in 1..hymnal.hymns) {
            hymns.add(
                RemoteHymn(
                    index = "$i",
                    number = i,
                    title = "Hymn $i",
                    majorKey = null,
                    lyrics = emptyList(),
                    author = null,
                    authorLink = null,
                    revision = 1
                )
            )
        }

        val json = Json { ignoreUnknownKeys = true }
        val jsonString = json.encodeToString(hymns)
        return jsonString.toByteArray(StandardCharsets.UTF_8)
    }
}