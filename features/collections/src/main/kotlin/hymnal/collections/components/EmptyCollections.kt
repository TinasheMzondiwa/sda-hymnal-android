// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.collections.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import hymnal.ui.theme.HymnalTheme
import hymnal.libraries.l10n.R as L10nR
import hymnal.ui.R as UiR

internal fun LazyListScope.emptyCollections() {
    item("empty-collections") {
        EmptyCollections(Modifier.animateItem().padding(top = 64.dp))
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun EmptyCollections(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = 0.12f
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(UiR.drawable.ic_list_heart),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = stringResource(L10nR.string.empty_collections_title),
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = stringResource(L10nR.string.empty_collections_message),
            style = MaterialTheme.typography.bodyMediumEmphasized,
            textAlign = TextAlign.Center,
        )
    }
}

@PreviewLightDark
@Composable
private fun EmptyCollectionsPreview() {
    HymnalTheme {
        Surface { EmptyCollections(Modifier.padding(16.dp)) }
    }
}
