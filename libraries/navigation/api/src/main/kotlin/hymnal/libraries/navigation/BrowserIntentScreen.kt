// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.libraries.navigation

import android.net.Uri
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

/** A circuit [Screen] for launching an androidx-browser tab with the provided [url]. */
@Parcelize
data class BrowserIntentScreen(val url: Uri) : Screen