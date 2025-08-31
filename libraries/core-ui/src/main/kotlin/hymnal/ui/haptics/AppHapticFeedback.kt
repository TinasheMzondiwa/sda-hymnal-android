// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.ui.haptics

import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

@Stable
interface AppHapticFeedback {

    /**
     * Performs a haptic feedback for a click action.
     */
    fun performClick()

    /**
     * Performs a haptic feedback for a long press action.
     */
    fun performLongPress()

    /**
     * Performs a haptic feedback for a success action.
     */
    fun performSuccess()

    /**
     * Performs a haptic feedback for an error action.
     */
    fun performError()

    /**
     * Performs a haptic feedback for a screen view action.
     */
    fun performScreenView()

    /**
     * Performs a haptic feedback for a segment switch action.
     */
    fun performSegmentSwitch()

    /**
     * Performs a haptic feedback for a switch action.
     */
    fun performToggleSwitch(on: Boolean)

    /**
     * Performs a haptic feedback for a gesture end action.
     */
    fun performGestureEnd()

}

val LocalAppHapticFeedback =
    staticCompositionLocalOf<AppHapticFeedback> { error("CompositionLocal AppHapticFeedback not present") }

internal class DefaultHapticFeedback(
    private val hapticFeedback: HapticFeedback
) : AppHapticFeedback {
    override fun performClick() = hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
    override fun performLongPress() = hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
    override fun performSuccess() = hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
    override fun performError() = hapticFeedback.performHapticFeedback(HapticFeedbackType.Reject)
    override fun performScreenView() = hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
    override fun performSegmentSwitch() = hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
    override fun performToggleSwitch(on: Boolean) = hapticFeedback.performHapticFeedback(if (on) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff)
    override fun performGestureEnd() = hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
}
