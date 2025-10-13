// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.info

import com.slack.circuit.runtime.CircuitUiState

sealed interface State : CircuitUiState {
    data object Loading : State
}