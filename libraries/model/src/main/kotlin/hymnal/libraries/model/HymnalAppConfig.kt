/*
 * Copyright (C) 2025 Tinashe Mzondiwa
 * SPDX-License-Identifier: Apache-2.0
 */

package hymnal.libraries.model

import androidx.annotation.Keep

@Keep
data class HymnalAppConfig(
    val version: String,
    val buildNumber: Int,
    val isDebug: Boolean,
    val webClientId: String,
    val appId: String,
)