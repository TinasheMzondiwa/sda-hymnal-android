// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.hymns.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FlexibleBottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hymnal.hymns.OpenedHymnEvent
import hymnal.hymns.OpenedHymnState
import hymnal.ui.extensions.plus
import hymnal.ui.haptics.LocalAppHapticFeedback
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.theme.size.HymnalDimens
import hymnal.hymns.R as HymnsR
import hymnal.libraries.l10n.R as L10nR

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LastHymnBar(
    state: OpenedHymnState.Visible,
    modifier: Modifier = Modifier,
    scrollBehavior: BottomAppBarScrollBehavior? = null,
) {
    val hapticFeedback = LocalAppHapticFeedback.current
    val layoutDirection = LocalLayoutDirection.current
    val horizontalPadding = HymnalDimens.horizontalPadding()
    val contentPadding = WindowInsets.safeDrawing.only(
        WindowInsetsSides.Horizontal
    ).asPaddingValues().plus(
        layoutDirection = layoutDirection,
        start = horizontalPadding,
        end = horizontalPadding,
    )
    val defaultInsets = BottomAppBarDefaults.windowInsets
    // Create custom insets that add 6dp bottom padding
    val customInsets = defaultInsets.add(WindowInsets(bottom = 6.dp))

    SwipeToDismissBox(
        state = rememberSwipeToDismissBoxState(),
        backgroundContent = {},
        onDismiss = {
            hapticFeedback.performGestureEnd()
            state.eventSink(OpenedHymnEvent.DismissCard)
        },
    ) {
        FlexibleBottomAppBar(
            modifier = modifier,
            containerColor = Color.Transparent,
            contentPadding = contentPadding,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            windowInsets = customInsets,
            scrollBehavior = scrollBehavior,
        ) {
            ListItem(
                headlineContent = {
                    Text(
                        text = state.title,
                        modifier = Modifier,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 14.sp,
                        ),
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .clip(Shape)
                    .border(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.65f),
                        shape = Shape
                    )
                    .clickable {
                        hapticFeedback.performClick()
                        state.eventSink(OpenedHymnEvent.GoToHymn(state.index))
                    },
                supportingContent = {
                    SupportingContent()
                },
                leadingContent = {
                    NumberText(state.number)
                },
                trailingContent = {
                    Icon(
                        painter = painterResource(HymnsR.drawable.ic_open_in_full),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    headlineColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    trailingIconColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.65f),
                ),
            )
        }
    }

}

private val Shape = RoundedCornerShape(12.dp)

@Composable
private fun NumberText(number: Int, modifier: Modifier = Modifier) {
    val verticalSpacing = if (number >= 100) 8.dp else 4.dp

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 40.dp, minHeight = 40.dp)
            .background(
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                shape = CircleShape,
            )
            .border(0.5.dp, DividerDefaults.color, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = verticalSpacing),
            color = MaterialTheme.colorScheme.secondaryContainer,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
        )
    }
}

@Composable
private fun SupportingContent(modifier: Modifier = Modifier) {
    val lastHymnString = buildAnnotatedString {
        appendInlineContent(INLINE_ICON_ID)
        append(" ")
        append(stringResource(L10nR.string.last_hymn))
    }
    val inlineContent = mapOf(
        INLINE_ICON_ID to
                InlineTextContent(
                    Placeholder(
                        width = 14.sp,
                        height = 14.sp,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                }
    )

    Text(
        text = lastHymnString,
        modifier = modifier,
        inlineContent = inlineContent,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
    )
}

private const val INLINE_ICON_ID = "icon"

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        Surface {
            LastHymnBar(
                state = OpenedHymnState.Visible("100", 100, "Great is thy faithfulness") {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}