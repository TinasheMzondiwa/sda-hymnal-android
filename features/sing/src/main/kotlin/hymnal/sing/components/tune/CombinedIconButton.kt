// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sing.components.tune

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults.rememberTooltipPositionProvider
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import hymnal.libraries.l10n.R as L10nR

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
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    showTooltip: Boolean = false,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = IconButtonDefaults.standardShape,
    content: @Composable () -> Unit,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val buttonContent = @Composable {
        Box(
            modifier =
                Modifier
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

    if (showTooltip) {
        TooltipContainer(modifier) {
            buttonContent()
        }
    } else {
        buttonContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TooltipContainer(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val tooltipState = rememberTooltipState(initialIsVisible = true)
    var isDismissed by rememberSaveable { mutableStateOf(false) }

    if (isDismissed) {
        content()
    } else {
        TooltipBox(
            modifier = modifier,
            positionProvider = rememberTooltipPositionProvider(TooltipAnchorPosition.Above, 6.dp),
            tooltip = {
                RichTooltip(
                    title = { Text(stringResource(L10nR.string.tune_tooltip_title)) },
                    action = {
                        TextButton(
                            onClick = {
                                tooltipState.dismiss()
                                isDismissed = true
                            }
                        ) { Text(stringResource(L10nR.string.tune_tooltip_action)) }
                    },
                ) {
                    Text(stringResource(L10nR.string.tune_tooltip_description))
                }
            },
            state = tooltipState,
            content = content
        )
    }

}

@Stable
private fun IconButtonColors.containerColor(enabled: Boolean) =
    if (enabled) containerColor else disabledContainerColor

@Stable
private fun IconButtonColors.contentColor(enabled: Boolean): Color =
    if (enabled) contentColor else disabledContentColor