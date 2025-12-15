// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.ui.haptics

import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import timber.log.Timber

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
    override fun performClick() = safePerform(HapticFeedbackType.ContextClick)
    override fun performLongPress() = safePerform(HapticFeedbackType.LongPress)
    override fun performSuccess() = safePerform(HapticFeedbackType.Confirm)
    override fun performError() = safePerform(HapticFeedbackType.Reject)
    override fun performScreenView() = safePerform(HapticFeedbackType.Confirm)
    override fun performSegmentSwitch() = safePerform(HapticFeedbackType.SegmentTick)

    override fun performToggleSwitch(on: Boolean) = safePerform(
        if (on) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff
    )

    override fun performGestureEnd() = safePerform(HapticFeedbackType.GestureEnd)

    /**
     * Safely attempts to perform haptic feedback.
     * Catches exceptions caused by broken vendor implementations (e.g., ZTE Richtap).
     */
    private fun safePerform(type: HapticFeedbackType) {
        try {
            hapticFeedback.performHapticFeedback(type)
        } catch (e: Exception) {
            Timber.e(e, "Ignored haptic feedback crash on device")
        }
    }
}
