// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sing.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.slack.circuit.overlay.Overlay
import com.slack.circuit.overlay.OverlayNavigator

private const val CORNER_RADIUS = 16

/** A circuit [Overlay] that shows a [ModalBottomSheet]. */
class BottomSheetOverlay(
    private val skipPartiallyExpanded: Boolean = false,
    private val content: @Composable (ColumnScope) -> Unit,
) : Overlay<BottomSheetOverlay.Result> {

    sealed interface Result {
        data object Dismissed : Result
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content(navigator: OverlayNavigator<Result>) {
        val hapticFeedback = LocalHapticFeedback.current
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = skipPartiallyExpanded
        )

        ModalBottomSheet(
            onDismissRequest = {
                navigator.finish(Result.Dismissed)
                if (sheetState.targetValue == SheetValue.Hidden) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                }
            },
            modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top)),
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = CORNER_RADIUS.dp, topEnd = CORNER_RADIUS.dp),
            content = content,
        )
    }
}