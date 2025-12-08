// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.donate

import android.app.Activity
import androidx.annotation.StringRes
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import hymnal.donate.ui.TierButtonSpec
import kotlinx.collections.immutable.ImmutableList
import hymnal.libraries.l10n.R as L10nR

sealed interface State : CircuitUiState {
    data class Loading(val eventSink: (Event.OnClose) -> Unit) : State
    data class Donate(
        val type: DonateType,
        val tiers: ImmutableList<TierButtonSpec>,
        val selectedTier: TierButtonSpec?,
        val eventSink: (Event) -> Unit,
    ) : State
}

sealed interface Event : CircuitUiEvent {
    data object OnClose : Event
    data class OnSelectDonateType(val type: DonateType): Event
    data class SelectTier(val tier: TierButtonSpec) : Event
    data class OnPrimaryButtonClick(val activity: Activity) : Event
}

enum class DonateType(@param:StringRes val titleRes: Int) {
    ONE_TIME(L10nR.string.donate_one_time),
    SUBSCRIPTION(L10nR.string.donate_sub);
}
