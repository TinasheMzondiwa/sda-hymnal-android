// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.account

import androidx.compose.animation.ExperimentalSharedTransitionApi
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
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
import com.slack.circuit.overlay.OverlayEffect
import com.slack.circuit.sharedelements.PreviewSharedElementTransitionLayout
import com.slack.circuit.sharedelements.SharedElementTransitionScope
import com.slack.circuitx.overlays.BasicAlertDialogOverlay
import com.slack.circuitx.overlays.DialogResult
import dev.zacsweers.metro.AppScope
import hymnal.libraries.navigation.AccountScreen
import hymnal.libraries.navigation.key.AccountSharedTransitionKey
import hymnal.ui.haptics.LocalAppHapticFeedback
import hymnal.ui.theme.HymnalTheme
import hymnal.ui.theme.size.HymnalDimens
import hymnal.ui.theme.type.Poppins
import hymnal.ui.widget.content.ContentBox
import hymnal.ui.widget.image.RemoteImage
import hymnal.ui.widget.placeholder.placeholder
import hymnal.ui.widget.scaffold.HazeScaffold
import hymnal.account.R as AccountR
import hymnal.libraries.l10n.R as L10nR

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@CircuitInject(AccountScreen::class, AppScope::class)
@Composable
fun AccountScreenUi(state: State, modifier: Modifier = Modifier) {
    val hapticFeedback = LocalAppHapticFeedback.current
    SharedElementTransitionScope {
        HazeScaffold(
            modifier = modifier
                .sharedElement(
                    sharedContentState =
                        rememberSharedContentState(
                            AccountSharedTransitionKey(
                                type = AccountSharedTransitionKey.ElementType.Card,
                            )
                        ),
                    animatedVisibilityScope =
                        requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                ),
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(L10nR.string.account)) },
                    navigationIcon = {
                        IconButton({
                            hapticFeedback.performClick()
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
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedElementTransitionScope.LoadingUi(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = HymnalDimens.horizontalPadding()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(
            modifier = Modifier
                .sharedElement(
                    sharedContentState =
                        rememberSharedContentState(
                            AccountSharedTransitionKey(
                                type = AccountSharedTransitionKey.ElementType.Image,
                            )
                        ),
                    animatedVisibilityScope =
                        requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                )
                .size(90.dp)
                .placeholder(visible = true, shape = CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Spacer(
            modifier = Modifier
                .sharedBounds(
                    sharedContentState =
                        rememberSharedContentState(
                            AccountSharedTransitionKey(
                                type = AccountSharedTransitionKey.ElementType.Name,
                            )
                        ),
                    animatedVisibilityScope =
                        requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                )
                .fillMaxWidth(0.6f)
                .height(24.dp)
                .placeholder(visible = true)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Spacer(
            modifier = Modifier
                .sharedBounds(
                    sharedContentState =
                        rememberSharedContentState(
                            AccountSharedTransitionKey(
                                type = AccountSharedTransitionKey.ElementType.Email,
                            )
                        ),
                    animatedVisibilityScope =
                        requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                )
                .fillMaxWidth(0.8f)
                .height(20.dp)
                .placeholder(visible = true)
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedElementTransitionScope.LoggedOutUi(
    eventSink: (Event.NotLoggedIn) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val resources = LocalResources.current
    val hapticFeedback = LocalAppHapticFeedback.current
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
            modifier = Modifier
                .sharedElement(
                    sharedContentState =
                        rememberSharedContentState(
                            AccountSharedTransitionKey(
                                type = AccountSharedTransitionKey.ElementType.Image,
                            )
                        ),
                    animatedVisibilityScope =
                        requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                )
                .size(80.dp)
        )

        Text(
            text = stringResource(id = L10nR.string.account_not_logged_in_message),
            modifier = Modifier
                .sharedBounds(
                    sharedContentState =
                        rememberSharedContentState(
                            AccountSharedTransitionKey(
                                type = AccountSharedTransitionKey.ElementType.Email,
                            )
                        ),
                    animatedVisibilityScope =
                        requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                )
                .padding(vertical = 24.dp),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        ElevatedButton(
            onClick = {
                hapticFeedback.performClick()
                eventSink(Event.NotLoggedIn.OnLoginClick(context))
            }, colors = ButtonDefaults.elevatedButtonColors(
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
                            url = resources.getString(L10nR.string.app_policy),
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedElementTransitionScope.LoggedInUi(
    state: State.LoggedIn,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalAppHapticFeedback.current
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
                                shape = CircleShape,
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
                .sharedElement(
                    sharedContentState =
                        rememberSharedContentState(
                            AccountSharedTransitionKey(
                                type = AccountSharedTransitionKey.ElementType.Image,
                            )
                        ),
                    animatedVisibilityScope =
                        requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                )
                .size(AvatarSize)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = state.name ?: "",
            modifier = Modifier
                .sharedBounds(
                    sharedContentState =
                        rememberSharedContentState(
                            AccountSharedTransitionKey(
                                type = AccountSharedTransitionKey.ElementType.Name,
                            )
                        ),
                    animatedVisibilityScope =
                        requireAnimatedScope(SharedElementTransitionScope.AnimatedScope.Navigation),
                ),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))

        state.email?.let {
            Text(
                text = it,
                modifier = Modifier
                    .sharedBounds(
                        sharedContentState =
                            rememberSharedContentState(
                                AccountSharedTransitionKey(
                                    type = AccountSharedTransitionKey.ElementType.Email,
                                )
                            ),
                        animatedVisibilityScope =
                            requireAnimatedScope(
                                SharedElementTransitionScope.AnimatedScope.Navigation,
                            ),
                    ),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        ElevatedButton(onClick = {
            hapticFeedback.performClick()
            state.eventSink(Event.LoggedIn.OnLogoutClick)
        }) {
            Icon(imageVector = Icons.AutoMirrored.Rounded.Logout, contentDescription = null)
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(L10nR.string.account_sign_out))
        }

        Spacer(modifier = Modifier.height(12.dp))

        ElevatedButton(
            onClick = {
                hapticFeedback.performClick()
                state.eventSink(Event.LoggedIn.OnDeleteAccountClick)
            },
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

    Overlay(state.overlay)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Overlay(state: Overlay?) {
    val overlay = state ?: return
    val hapticFeedback = LocalAppHapticFeedback.current

    OverlayEffect(overlay) {
        when (overlay) {
            is Overlay.ConfirmDeleteAccount -> {
                val result = show(
                    BasicAlertDialogOverlay(
                        model = Unit,
                        onDismissRequest = { DialogResult.Dismiss },
                        content = { _, navigator ->

                            AlertDialog(
                                onDismissRequest = { navigator.finish(DialogResult.Dismiss) },
                                icon = null,
                                title = {
                                    Text(
                                        text = stringResource(L10nR.string.account_delete_confirm_title),
                                        style = MaterialTheme.typography.headlineSmall,
                                    )
                                },
                                text = { Text(text = stringResource(L10nR.string.account_delete_confirm_message)) },
                                confirmButton = {
                                    TextButton(onClick = {
                                        hapticFeedback.performError()
                                        navigator.finish(DialogResult.Confirm)
                                    }) {
                                        Text(text = stringResource(L10nR.string.delete))
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = {
                                        hapticFeedback.performClick()
                                        navigator.finish(DialogResult.Cancel)
                                    }) {
                                        Text(stringResource(L10nR.string.cancel))
                                    }
                                },
                            )
                        })
                )

                overlay.resultSink(result)

            }
        }

    }
}

private val AvatarSize = 90.dp

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewLightDark
@Composable
private fun Preview() {
    PreviewSharedElementTransitionLayout {
        HymnalTheme {
            AccountScreenUi(state = State.NotLoggedIn {})
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewLightDark
@Composable
private fun PreviewLoading() {
    PreviewSharedElementTransitionLayout {
        HymnalTheme {
            AccountScreenUi(state = State.Loading)
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewLightDark
@Composable
private fun PreviewLoggedIn() {
    PreviewSharedElementTransitionLayout {
        HymnalTheme {
            AccountScreenUi(
                state = State.LoggedIn(
                    name = "Tinashe Mzondiwa",
                    email = "test@email.com",
                    image = null,
                    overlay = null,
                    eventSink = {},
                )
            )
        }
    }
}