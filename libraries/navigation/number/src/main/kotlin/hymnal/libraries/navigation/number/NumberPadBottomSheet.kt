// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.libraries.navigation.number

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.slack.circuit.foundation.CircuitContent
import com.slack.circuit.foundation.NavEvent
import com.slack.circuit.overlay.Overlay
import com.slack.circuit.overlay.OverlayNavigator
import kotlinx.coroutines.launch

class NumberPadBottomSheet(private val hymns: Int) : Overlay<NumberPadBottomSheet.Result> {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content(navigator: OverlayNavigator<Result>) {
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )
        val coroutineScope = rememberCoroutineScope()

        ModalBottomSheet(
            onDismissRequest = { navigator.finish(Result.Cancel) },
            sheetState = sheetState,
            modifier = Modifier.windowInsetsPadding(
                WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Top
                )
            ),
            shape = RoundedCornerShape(topStart = CORNER_RADIUS.dp, topEnd = CORNER_RADIUS.dp),
            content = {
                CircuitContent(screen = PadContentScreen(hymns), onNavEvent = { navEvent ->
                    when (navEvent) {
                        is NavEvent.Pop -> {
                            val popResult = navEvent.result
                            val result = if (popResult is NumPadPopResult) {
                                Result.Confirm(popResult.number)
                            } else {
                                Result.Cancel
                            }
                            coroutineScope.launch {
                                sheetState.hide()
                                navigator.finish(result)
                            }
                        }
                        is NavEvent.GoTo,
                        is NavEvent.ResetRoot -> Unit
                    }
                })
            },
        )
    }

    companion object {
        private const val CORNER_RADIUS = 16
    }

    sealed interface Result {
        data class Confirm(val number: Int) : Result
        object Cancel : Result
    }
}