// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath

import com.slack.circuit.runtime.CircuitUiState
import hymnal.sabbath.components.SabbathInfoItem
import hymnal.services.prefs.model.AppTheme
import kotlinx.collections.immutable.ImmutableList

sealed interface State : CircuitUiState {
    val theme: AppTheme

    data class Loading(override val theme: AppTheme) : State
    data class NoLocation(
        override val theme: AppTheme,
        val eventSink: (Event.NoLocation) -> Unit
    ) : State

    data class SabbathInfo(
        override val theme: AppTheme,
        val items: ImmutableList<SabbathInfoItem>,
    ) : State
}

sealed interface Event {
    sealed interface NoLocation : Event {
        data object OnLocationGranted : NoLocation
        data object OnLocationDenied : NoLocation
    }

    sealed interface SabbathInfo : Event {
        data class OnReminderToggled(val enabled: Boolean) : SabbathInfo
        data object OnSabbathHymnsClicked : SabbathInfo
    }
}