/*
 * Copyright (C) 2025 Tinashe Mzondiwa
 * SPDX-License-Identifier: Apache-2.0
 */

package hymnal.ui.extensions.placeholder

import android.annotation.SuppressLint
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Applies a placeholder modifier used in loading states.
 */
fun Modifier.asPlaceholder(
    visible: Boolean,
    shape: Shape = RoundedCornerShape(8.dp),
    color: Color,
    highlightColor: Color
) = placeholder(
    visible = visible,
    color = color,
    shape = shape,
    highlight = PlaceholderHighlight.Companion.fade(
        highlightColor = highlightColor
    )
)

/**
 * Applies a placeholder modifier
 */
@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.asPlaceholder(
    visible: Boolean,
    color: Color? = null,
    shape: Shape = RoundedCornerShape(8.dp)
) = composed {
    asPlaceholder(
        visible = visible,
        shape = shape,
        color = color ?: MaterialTheme.colorScheme.inverseOnSurface,
        highlightColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
    )
}
