// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.sabbath.impl.service.model

import java.time.ZonedDateTime

data class SabbathTimes(
    val friday: ZonedDateTime,
    val saturday: ZonedDateTime,
)