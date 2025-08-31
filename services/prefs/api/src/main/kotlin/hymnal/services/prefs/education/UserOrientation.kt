// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.services.prefs.education

import kotlinx.coroutines.flow.Flow

/**
 * User orientation or feature education tracker.
 * Resets after app re-install.
 */
interface UserOrientation {

    /** Tracks an [education] has been shown. */
    suspend fun track(education: Education)

    /** Returns true if this [education] should be shown. */
    fun shouldShow(education: Education): Flow<Boolean>
}