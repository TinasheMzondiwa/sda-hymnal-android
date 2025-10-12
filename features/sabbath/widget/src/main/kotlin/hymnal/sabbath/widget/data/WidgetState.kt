// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath.widget.data

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import hymnal.services.sabbath.api.SabbathInfo

@Stable
sealed interface WidgetState {
    data object Loading : WidgetState
    data object Error : WidgetState

    @Immutable
    data class Data(val sabbathInfo: SabbathInfo) : WidgetState
}