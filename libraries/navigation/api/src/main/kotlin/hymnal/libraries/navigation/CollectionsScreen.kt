// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.libraries.navigation

import com.slack.circuit.runtime.screen.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
data object CollectionsScreen : ParcelableScreen

@Parcelize
data class CollectionHymnsScreen(val collectionId: String): ParcelableScreen