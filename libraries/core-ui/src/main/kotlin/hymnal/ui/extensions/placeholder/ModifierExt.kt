// Copyright (C) 2025 Tinashe Mzondiwa
// SPDX-License-Identifier: Apache-2.0

package hymnal.ui.extensions.placeholder

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.dp

/**
 * Applies a placeholder modifier
 */
fun Modifier.asPlaceholder(
    visible: Boolean,
    color: Color? = null,
    shape: Shape = RoundedCornerShape(8.dp)
): Modifier = this.then(
    PlaceholderElement(
        visible = visible,
        color = color,
        shape = shape
    )
)

private data class PlaceholderElement(
    val visible: Boolean,
    val color: Color?,
    val shape: Shape
) : ModifierNodeElement<PlaceholderNode>() {

    override fun create(): PlaceholderNode = PlaceholderNode(visible, color, shape)

    override fun update(node: PlaceholderNode) {
        node.visible = visible
        node.color = color
        node.shape = shape
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "asPlaceholder"
        properties["visible"] = visible
        properties["color"] = color
        properties["shape"] = shape
    }
}

private class PlaceholderNode(
    var visible: Boolean,
    var color: Color?,
    var shape: Shape
) : Modifier.Node()
