// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.sabbath.api

interface SabbathRepository {
    suspend fun getSabbathInfo(latitude: Double, longitude: Double): Result<SabbathInfo>
}