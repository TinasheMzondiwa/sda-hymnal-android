// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.collections.add

import androidx.compose.runtime.Immutable
import hymnal.services.model.HymnsCollection

@Immutable
data class ChooseCollectionSpec(
    val collection: HymnsCollection,
    val isSelected: Boolean,
    val id: String = collection.collectionId
)
