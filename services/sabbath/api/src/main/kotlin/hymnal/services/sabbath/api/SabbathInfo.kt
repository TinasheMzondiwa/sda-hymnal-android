// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.sabbath.api

import androidx.annotation.Keep

@Keep
data class SabbathInfo(
    val location: String,
    val isSabbath: Boolean,
    val sabbathStart: String,
    val sabbathEnd: String,
)