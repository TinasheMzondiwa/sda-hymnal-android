// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath.widget.data

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Stable
sealed interface WidgetState {
    data object Loading : WidgetState
    data object Error : WidgetState
    data class Data(val sabbathInfo: WidgetSabbathInfo) : WidgetState
}

@Immutable
data class WidgetSabbathInfo(
    val location: String,
    val label: String,
    val dayLabel: String?,
    val time: String,
)