// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.account.card

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.zacsweers.metro.AppScope
import hymnal.libraries.navigation.AccountCardScreen
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.widget.content.ContentBox
import hymnal.ui.widget.image.RemoteImage
import hymnal.ui.widget.placeholder.placeholder
import hymnal.account.R as AccountR
import hymnal.libraries.l10n.R as L10nR

@CircuitInject(AccountCardScreen::class, AppScope::class)
@Composable
fun AccountCardScreenUi(state: AccountCardState, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(L10nR.string.account),
            style = MaterialTheme.typography.labelLarge,
        )

        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
            when (state) {
                AccountCardState.Loading -> LoadingContent()
                is AccountCardState.LoggedIn -> LoggedInContent(
                    state = state,
                    modifier = Modifier.clickable {
                        state.eventSink(AccountCardEvent.OnAccountCardClick)
                    },
                )
                is AccountCardState.NotLoggedIn -> NotLoggedInContent(
                    Modifier.clickable {
                        state.eventSink(AccountCardEvent.OnAccountCardClick)
                    }
                )
            }
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(24.dp)

    ListItem(
        headlineContent = {
            Spacer(
                Modifier
                    .size(width = 160.dp, height = 16.dp)
                    .placeholder(visible = true, shape = shape)
            )
        },
        modifier = modifier,
        supportingContent = {
            Spacer(
                Modifier
                    .padding(top = 6.dp)
                    .size(width = 320.dp, height = 16.dp)
                    .placeholder(visible = true, shape = shape)
            )
        },
        leadingContent = {
            Spacer(
                modifier = Modifier
                    .size(24.dp)
                    .placeholder(visible = true, shape = CircleShape)
            )
        },
    )
}

@Composable
private fun NotLoggedInContent(modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = {
            Text(text = stringResource(L10nR.string.account_not_logged_in_title))
        },
        modifier = modifier,
        leadingContent = {
            Icon(
                painter = painterResource(AccountR.drawable.ic_account_circle),
                contentDescription = null
            )
        },
        supportingContent = {
            Text(text = stringResource(L10nR.string.account_not_logged_in_message),)
        },
        trailingContent = {
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null
            )
        }
    )
}

@Composable
private fun LoggedInContent(state: AccountCardState.LoggedIn, modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = {
            Text(text = state.name)
        },
        modifier = modifier,
        leadingContent = {
            ContentBox(
                content = RemoteImage(
                    data = state.image,
                    contentDescription = null,
                    loading = {
                        Box(
                            modifier = Modifier
                                .size(AvatarSize)
                                .placeholder(
                                    visible = true,
                                    shape = CircleShape
                                )
                        )
                    },
                    error = {
                        Icon(
                            painter = painterResource(AccountR.drawable.ic_account_circle),
                            contentDescription = null
                        )
                    }
                ),
                modifier = Modifier
                    .size(AvatarSize)
                    .clip(CircleShape)
            )

        },
        supportingContent = {
            Text(text = state.email)
        }, trailingContent = {
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null
            )
        }
    )
}

private val AvatarSize = 24.dp

@PreviewLightDark
@Composable
private fun PreviewLoading() {
    HymnalTheme {
        Surface {
            AccountCardScreenUi(
                state = AccountCardState.Loading,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewLoggedIn() {
    HymnalTheme {
        Surface {
            AccountCardScreenUi(
                state = AccountCardState.LoggedIn(
                    name = "Tinashe Mzondiwa",
                    email = "test@gmail.com",
                    image = "",
                    eventSink = {},
                ),
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewNotLoggedIn() {
    HymnalTheme {
        Surface {
            AccountCardScreenUi(
                state = AccountCardState.NotLoggedIn(eventSink = {}),
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}