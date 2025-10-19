// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.ui.widget.placeholder

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * A modifier that applies a shimmer placeholder effect.
 *
 * @param visible Whether the placeholder is visible or not.
 * @param color The color of the placeholder background.
 * @param shape The shape of the placeholder.
 * @param highlightColor The color of the shimmer highlight.
 * @param animationSpec The animation spec for the shimmer effect.
 */
fun Modifier.placeholder(
    visible: Boolean,
    color: Color,
    shape: Shape,
    highlightColor: Color,
    animationSpec: InfiniteRepeatableSpec<Float> = infiniteRepeatable(
        animation = tween(durationMillis = 1700, delayMillis = 200),
        repeatMode = RepeatMode.Restart
    )
): Modifier {
    if (!visible) {
        return this
    }
    return this.clip(shape)
        .then(PlaceholderElement(color, highlightColor, animationSpec))
}

/**
 * A modifier that applies a shimmer placeholder effect with default values from [MaterialTheme].
 *
 * @param visible Whether the placeholder is visible or not.
 * @param shape The shape of the placeholder. Defaults to [MaterialTheme.shapes.small].
 */
@Composable
fun Modifier.placeholder(
    visible: Boolean,
    shape: Shape = MaterialTheme.shapes.small,
): Modifier {
    if (!visible) {
        return this
    }
    return placeholder(
        visible = true,
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = shape,
        highlightColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    )
}

private data class PlaceholderElement(
    private val color: Color,
    private val highlightColor: Color,
    private val animationSpec: InfiniteRepeatableSpec<Float>
) : ModifierNodeElement<PlaceholderNode>() {

    override fun create(): PlaceholderNode {
        return PlaceholderNode(color, highlightColor, animationSpec)
    }

    override fun update(node: PlaceholderNode) {
        node.color = color
        node.highlightColor = highlightColor
        node.animationSpec = animationSpec
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "placeholder"
        properties["color"] = color
        properties["highlightColor"] = highlightColor
        properties["animationSpec"] = animationSpec
    }
}

private class PlaceholderNode(
    var color: Color,
    var highlightColor: Color,
    var animationSpec: InfiniteRepeatableSpec<Float>
) : Modifier.Node(), DrawModifierNode, GlobalPositionAwareModifierNode {

    private val shimmerBrush: Brush
        get() = Brush.linearGradient(
            colors = listOf(
                color,
                highlightColor,
                color,
            ),
            start = Offset(offset, 0f),
            end = Offset(offset + size.width.toFloat(), size.height.toFloat())
        )

    private var size: IntSize = IntSize.Zero
    private var offset: Float = 0f
    private var animation: Job? = null

    override fun onAttach() {
        startAnimation()
    }

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        val newSize = coordinates.size
        if (newSize != size) {
            size = newSize
            startAnimation()
        }
    }

    override fun ContentDrawScope.draw() {
        drawRect(brush = shimmerBrush)
    }

    private fun startAnimation() {
        animation?.cancel()
        if (size.width == 0) return

        val initialValue = -size.width.toFloat()
        val targetValue = size.width.toFloat() * 2
        val animatable = Animatable(initialValue)

        animation = coroutineScope.launch {
            animatable.animateTo(
                targetValue = targetValue,
                animationSpec = animationSpec,
                block = {
                    offset = value
                    invalidateDraw()
                }
            )
        }
    }

    override fun onDetach() {
        animation?.cancel()
    }
}
