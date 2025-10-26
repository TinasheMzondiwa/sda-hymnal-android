// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.zacsweers.metro.AppScope
import hymnal.libraries.navigation.AccountScreen
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.theme.size.HymnalDimens
import hymnal.ui.theme.type.Poppins
import hymnal.ui.widget.content.ContentBox
import hymnal.ui.widget.image.RemoteImage
import hymnal.ui.widget.placeholder.placeholder
import hymnal.ui.widget.scaffold.HazeScaffold
import hymnal.account.R as AccountR
import hymnal.libraries.l10n.R as L10nR

@OptIn(ExperimentalMaterial3Api::class)
@CircuitInject(AccountScreen::class, AppScope::class)
@Composable
fun AccountScreenUi(state: State, modifier: Modifier = Modifier) {
    HazeScaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(L10nR.string.account)) },
                navigationIcon = {
                    IconButton({
                        when (state) {
                            State.Loading -> Unit
                            is State.LoggedIn -> state.eventSink(Event.LoggedIn.OnNavBack)
                            is State.NotLoggedIn -> state.eventSink(Event.NotLoggedIn.OnNavBack)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        blurTopBar = true
    ) { contentPadding ->
        when (state) {
            is State.Loading -> LoadingUi(modifier = Modifier.padding(contentPadding))
            is State.NotLoggedIn -> LoggedOutUi(
                eventSink = state.eventSink,
                modifier = Modifier.padding(contentPadding)
            )

            is State.LoggedIn -> LoggedInUi(
                state = state,
                modifier = Modifier.padding(contentPadding)
            )
        }
    }
}

@Composable
private fun LoadingUi(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = HymnalDimens.horizontalPadding()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(
            modifier = Modifier
                .size(90.dp)
                .placeholder(visible = true, shape = CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Spacer(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(24.dp)
                .placeholder(visible = true)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Spacer(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(20.dp)
                .placeholder(visible = true)
        )
    }
}

@Composable
private fun LoggedOutUi(
    eventSink: (Event.NotLoggedIn) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val horizontalPadding = HymnalDimens.horizontalPadding()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = horizontalPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            painter = painterResource(AccountR.drawable.ic_account_circle),
            contentDescription = null,
            modifier = Modifier.size(80.dp)
        )

        Text(
            text = stringResource(id = L10nR.string.account_not_logged_in_message),
            modifier = Modifier.padding(vertical = 24.dp),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        ElevatedButton(
            onClick = { eventSink(Event.NotLoggedIn.OnLoginClick(context)) },
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Icon(
                painter = painterResource(id = AccountR.drawable.ic_google_logo),
                contentDescription = null,
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))

            Text(text = stringResource(id = L10nR.string.sign_in_with_google))
        }
        Spacer(modifier = Modifier.height(24.dp))

        val textColor = MaterialTheme.colorScheme.onSurfaceVariant
        val annotatedText = remember {
                buildAnnotatedString {
                    append("By continuing, you agree to our Terms of Service as described in our ")
                    withLink(
                        link = LinkAnnotation.Url(
                            url = context.getString(L10nR.string.app_policy),
                            styles = TextLinkStyles(
                                style = SpanStyle(
                                    color = textColor,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = Poppins,
                                    textDecoration = TextDecoration.Underline,
                                )
                            )
                        )
                    ) {
                        append("Privacy Policy")
                    }
                    append(".")
                    append(" SDA Hymnal App collects User IDs to help identify and restore user saved content.")
                }
        }
        Text(
            text = annotatedText,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = textColor),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LoggedInUi(
    state: State.LoggedIn,
    modifier: Modifier = Modifier
) {
    val horizontalPadding = HymnalDimens.horizontalPadding()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = horizontalPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

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
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            ),
            modifier = Modifier
                .size(AvatarSize)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = state.name ?: "",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = state.email ?: "",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(24.dp))

        ElevatedButton(onClick = { state.eventSink(Event.LoggedIn.OnLogoutClick) }) {
            Icon(imageVector = Icons.AutoMirrored.Rounded.Logout, contentDescription = null)
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(L10nR.string.account_sign_out))
        }

        Spacer(modifier = Modifier.height(12.dp))

        ElevatedButton(
            onClick = { state.eventSink(Event.LoggedIn.OnDeleteAccountClick) },
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
            ),
        ) {
            Icon(imageVector = Icons.Rounded.DeleteForever, contentDescription = null)
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(L10nR.string.account_delete))
        }
    }
}

private val AvatarSize = 90.dp


@PreviewLightDark
@Composable
private fun Preview() {
    HymnalTheme {
        AccountScreenUi(state = State.NotLoggedIn {})
    }
}

@PreviewLightDark
@Composable
private fun PreviewLoading() {
    HymnalTheme {
        AccountScreenUi(state = State.Loading)
    }
}

@PreviewLightDark
@Composable
private fun PreviewLoggedIn() {
    HymnalTheme {
        AccountScreenUi(
            state = State.LoggedIn(
                name = "Tinashe Mzondiwa",
                email = "test@email.com",
                image = null,
                eventSink = {},
                )
        )
    }
}