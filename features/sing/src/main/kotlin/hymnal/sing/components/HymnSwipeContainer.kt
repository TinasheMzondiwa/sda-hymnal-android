// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.sing.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hymnal.sing.components.model.TextStyleSpec
import hymnal.sing.components.text.toFamily
import hymnal.ui.haptics.LocalAppHapticFeedback
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * A container that wraps content and adds horizontal swipe gestures to navigate to the previous
 * or next hymn.
 *
 * This composable detects horizontal drag gestures and translates them into navigation events.
 * It also displays translucent indicators (the previous or next hymn number) on the sides
 * that dynamically change opacity and size based on the drag progress.
 *
 * @param currentNumber The number of the currently displayed hymn.
 * @param previousEnabled Whether sweeping towards the previous hymn is allowed (e.g. false if on the first hymn).
 * @param nextEnabled Whether sweeping towards the next hymn is allowed (e.g. false if on the last hymn).
 * @param textStyle The current app appearance styling to apply to the numerical indicators.
 * @param onSwipePrevious Callback triggered when the user successfully swipes to the previous hymn.
 * @param onSwipeNext Callback triggered when the user successfully swipes to the next hymn.
 * @param modifier Additional modifiers to be applied to the container.
 * @param content The primary content (typically a [LazyColumn]) to be displayed and moved during swipe.
 */
@Composable
internal fun HymnSwipeContainer(
    currentNumber: Int,
    previousEnabled: Boolean,
    nextEnabled: Boolean,
    textStyle: TextStyleSpec,
    onSwipePrevious: () -> Unit,
    onSwipeNext: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val hapticFeedback = LocalAppHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }

    val currentOnSwipePrevious by rememberUpdatedState(onSwipePrevious)
    val currentOnSwipeNext by rememberUpdatedState(onSwipeNext)

    val maxDragX = with(LocalDensity.current) { 100.dp.toPx() }
    val dragThreshold = with(LocalDensity.current) { 80.dp.toPx() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(previousEnabled, nextEnabled) {
                val velocityTracker = VelocityTracker()
                detectHorizontalDragGestures(
                    onDragStart = { velocityTracker.resetTracking() },
                    onDragEnd = {
                        val velocity = velocityTracker.calculateVelocity().x
                        coroutineScope.launch {
                            if (offsetX.value > dragThreshold || (offsetX.value > dragThreshold / 2 && velocity > 1000f)) {
                                currentOnSwipePrevious()
                                offsetX.animateTo(0f, spring())
                                hapticFeedback.performGestureEnd()
                            } else if (offsetX.value < -dragThreshold || (offsetX.value < -dragThreshold / 2 && velocity < -1000f)) {
                                currentOnSwipeNext()
                                offsetX.animateTo(0f, spring())
                                hapticFeedback.performGestureEnd()
                            } else {
                                offsetX.animateTo(0f, spring())
                            }
                        }
                    },
                    onDragCancel = {
                        coroutineScope.launch {
                            offsetX.animateTo(0f, spring())
                        }
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        velocityTracker.addPointerInputChange(change)
                        coroutineScope.launch {
                            val newOffset = offsetX.value + dragAmount
                            // Restrict to max drag amount
                            val boundedOffset = when {
                                newOffset > 0 && !previousEnabled -> 0f
                                newOffset < 0 && !nextEnabled -> 0f
                                newOffset > maxDragX -> maxDragX
                                newOffset < -maxDragX -> -maxDragX
                                else -> newOffset
                            }
                            offsetX.snapTo(boundedOffset)
                        }
                    }
                )
            }
    ) {
        // "Previous" indicator
        if (previousEnabled && currentNumber > 1) {
            val progress = (offsetX.value / maxDragX).coerceIn(0f, 1f)
            if (progress > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .safeDrawingPadding()
                        .padding(start = 16.dp)
                        .alpha(progress),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${currentNumber - 1}",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        fontFamily = textStyle.font.toFamily(),
                        fontSize = 40.sp * (0.8f + 0.2f * progress)
                    )
                }
            }
        }

        // "Next" indicator
        if (nextEnabled) {
            val progress = (-offsetX.value / maxDragX).coerceIn(0f, 1f)
            if (progress > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .safeDrawingPadding()
                        .padding(end = 16.dp)
                        .alpha(progress),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${currentNumber + 1}",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        fontFamily = textStyle.font.toFamily(),
                        fontSize = 40.sp * (0.8f + 0.2f * progress)
                    )
                }
            }
        }

        // Main Content
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .fillMaxSize()
        ) {
            content()
        }
    }
}
