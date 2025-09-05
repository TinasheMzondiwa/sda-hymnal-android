// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sabbath

import androidx.compose.runtime.Composable
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import hymnal.libraries.navigation.SabbathScreen

@Inject
class SabbathPresenter : Presenter<State> {

    @Composable
    override fun present(): State {
        return State
    }

    @CircuitInject(SabbathScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(): SabbathPresenter
    }
}