// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.libraries.navigation.key

import com.slack.circuit.sharedelements.SharedTransitionKey

data class CollectionSharedTransitionKey(val id: String, val type: ElementType) :
    SharedTransitionKey {
    enum class ElementType {
        Card,
        Title,
        Description,
    }
}
