// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.donate

import androidx.compose.runtime.Composable
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import hymnal.libraries.navigation.DonateScreen

@AssistedInject
class DonatePresenter : Presenter<State> {

    @CircuitInject(DonateScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(): DonatePresenter
    }

    @Composable
    override fun present(): State {
        return State
    }
}