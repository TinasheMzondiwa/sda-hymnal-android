/*
 * Copyright (C) 2025 Tinashe Mzondiwa
 * SPDX-License-Identifier: Apache-2.0
 */

package hymnal.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import hymnal.ui.extensions.placeholder.asPlaceholder
import hymnal.ui.widget.content.ContentBox
import hymnal.ui.widget.image.RemoteImage

@Composable
fun AvatarNavigationIcon(
    photoUrl: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    ContentBox(
        content = RemoteImage(
            data = photoUrl,
            contentDescription = null,
            loading = {
                Box(
                    modifier = Modifier
                        .size(AvatarSize)
                        .asPlaceholder(
                            visible = true,
                            shape = CircleShape
                        )
                )
            },
            error = {
                Icon(
                    imageVector = Icons.Rounded.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier
                        .size(AvatarSize)
                        .clickable { onClick() },
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        ),
        modifier = modifier
            .size(AvatarSize)
            .clip(CircleShape)
            .clickable { onClick() }
    )
}

private val AvatarSize = 32.dp
