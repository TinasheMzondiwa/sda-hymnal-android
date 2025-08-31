// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.ui.widget.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Composable for displaying a [ContentSlot]
 *
 * Example usage:
 * ```kotlin
 *      ContentBox(
 *          content = RemoteImage(...),
 *          modifier = Modifier.size(32.dp)
 *      )
 * ```
 *
 */
@Composable
fun ContentBox(
    content: ContentSlot,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.wrapContentSize()) {
        content.Content()
    }
}
