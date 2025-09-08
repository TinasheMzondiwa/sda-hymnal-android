// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.sabbath.api

import androidx.annotation.Keep
import java.time.ZonedDateTime

@Keep
data class SabbathInfo(
    val location: String,
    val isSabbath: Boolean,
    val sabbathStart: ZonedDateTime,
    val sabbathEnd: ZonedDateTime,
)