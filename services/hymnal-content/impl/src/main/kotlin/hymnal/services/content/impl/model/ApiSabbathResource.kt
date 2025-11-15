/*
 * Copyright (C) 2025 Tinashe Mzondiwa
 * SPDX-License-Identifier: Apache-2.0
 */

package hymnal.services.content.impl.model

import androidx.annotation.Keep
import hymnal.libraries.model.SabbathResource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ApiSabbathResource(
    val week: Int,

    @SerialName("scripture.id")
    val scriptureId: Int,

    @SerialName("scripture.reference")
    val scriptureReference: String,

    @SerialName("scripture.summary")
    val scriptureSummary: String,

    @SerialName("egw.id")
    val egwId: Int,

    @SerialName("egw.reference")
    val egwReference: String,

    @SerialName("egw.summary")
    val egwSummary: String,
)

internal fun ApiSabbathResource.toDomain(): List<SabbathResource> = listOf(
    SabbathResource.Scripture(
        id = scriptureId,
        reference = scriptureReference,
        text = scriptureSummary
    ),
    SabbathResource.Quote(
        id = egwId,
        reference = egwReference,
        text = egwSummary,
    )
)