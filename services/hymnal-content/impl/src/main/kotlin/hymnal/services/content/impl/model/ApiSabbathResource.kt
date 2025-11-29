/*
 * Copyright (C) 2025 Tinashe Mzondiwa
 * SPDX-License-Identifier: Apache-2.0
 */

package hymnal.services.content.impl.model

import androidx.annotation.Keep
import hymnal.storage.db.entity.SabbathResourceEntity
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

    @SerialName("scripture.section")
    val scriptureSection: String,

    @SerialName("egw.id")
    val egwId: Int,

    @SerialName("egw.reference")
    val egwReference: String,

    @SerialName("egw.summary")
    val egwSummary: String,
)

internal fun ApiSabbathResource.toEntity(): List<SabbathResourceEntity> = listOf(
    SabbathResourceEntity(
        id = "${week}_scripture",
        week = week,
        type = SabbathResourceEntity.Type.SCRIPTURE,
        reference = scriptureReference,
        text = scriptureSummary,
        section = scriptureSection
    ),
    SabbathResourceEntity(
        id = "${week}_egw",
        week = week,
        type = SabbathResourceEntity.Type.QUOTE,
        reference = egwReference,
        text = egwSummary,
        section = null,
    )
)