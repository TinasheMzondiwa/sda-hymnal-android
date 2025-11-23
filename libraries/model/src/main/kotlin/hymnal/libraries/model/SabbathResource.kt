/*
 * Copyright (C) 2025 Tinashe Mzondiwa
 * SPDX-License-Identifier: Apache-2.0
 */

package hymnal.libraries.model

sealed interface SabbathResource {
    data class Scripture(
        val id: Int,
        val reference: String,
        val text: String,
        val section: String?
    ) : SabbathResource

    data class Quote(
        val id: Int,
        val text: String,
        val reference: String,
    ) : SabbathResource
}