// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sing.components.tune

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role

/**
 * A simple icon button that also takes in a long-click.
 *
 * @param modifier The modifier to be applied to the button.
 * @param onClick The lambda to be invoked when the button is clicked.
 * @param onLongClick The lambda to be invoked when the button is long clicked.
 * @param enabled Controls the enabled state of the button. When `false`, this button will not be
 * clickable.
 * @param shape Defines the shape of the button's container.
 * @param colors [IconButtonColors] that will be used to resolve the colors for this button in
 * different states
 * @param interactionSource The [MutableInteractionSource] representing the stream of [Interaction]s
 * for this button.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun CombinedIconButton(
    modifier: Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = IconButtonDefaults.standardShape,
    content: @Composable () -> Unit,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    Box(
        modifier =
            modifier
                .minimumInteractiveComponentSize()
                .size(IconButtonDefaults.smallContainerSize())
                .clip(shape)
                .background(color = colors.containerColor(enabled), shape = shape)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick,
                    enabled = enabled,
                    role = Role.Button,
                    interactionSource = interactionSource,
                    indication = ripple(),
                ),
        contentAlignment = Alignment.Center,
    ) {
        val contentColor = colors.contentColor(enabled)
        CompositionLocalProvider(LocalContentColor provides contentColor, content = content)
    }
}

@Stable
private fun IconButtonColors.containerColor(enabled: Boolean) =
    if (enabled) containerColor else disabledContainerColor

@Stable
private fun IconButtonColors.contentColor(enabled: Boolean): Color =
    if (enabled) contentColor else disabledContentColor