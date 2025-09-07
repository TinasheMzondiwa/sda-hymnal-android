// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.sabbath.api

import kotlinx.coroutines.flow.Flow

interface SabbathRepository {
    fun getSabbathInfo(latitude: Double, longitude: Double): Flow<Result<SabbathInfo>>
}