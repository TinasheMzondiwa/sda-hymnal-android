// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sing.state

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalResources
import com.slack.circuit.retained.rememberRetained
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import hymnal.libraries.model.Hymnal
import hymnal.services.playback.TuneItem
import hymnal.sing.components.HymnContent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import timber.log.Timber
import hymnal.sing.R as SingR

/**
 * A producer that determines the correct tune item for the specified hymn index in the hymnal.
 */
@Stable
interface TuneItemStateProducer {

    @Composable
    operator fun invoke(hymn: HymnContent?, hymnal: Hymnal): TuneItem?
}

@SingleIn(AppScope::class)
@Inject
@ContributesBinding(scope = AppScope::class)
class TuneItemStateProducerImpl : TuneItemStateProducer {
    @Composable
    override fun invoke(hymn: HymnContent?, hymnal: Hymnal): TuneItem? {
        val resources = LocalResources.current
        val hymnsMapping = rememberRetained(resources) {
            loadMapFromResources(resources)
        }

        return when (hymnal) {
            Hymnal.OldHymnal -> hymn?.let { content ->
                hymnsMapping[content.index]?.let {
                    TuneItem(
                        index = it,
                        number = content.number,
                        title = content.title,
                        hymnal = hymnal.label
                    )
                }

            }
            Hymnal.NewHymnal -> hymn?.let {
                TuneItem(
                    index = it.index,
                    number = it.number,
                    title = it.title,
                    hymnal = hymnal.label
                )
            }
            Hymnal.Choruses -> null
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun loadMapFromResources(resources: Resources): Map<String, String> {
        return try {
            resources.openRawResource(
                SingR.raw.church_sda_map
            ).use { inputStream ->
                Json.decodeFromStream<Map<String, String>>(inputStream)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to load hymn mapping json")
            emptyMap()
        }
    }

}