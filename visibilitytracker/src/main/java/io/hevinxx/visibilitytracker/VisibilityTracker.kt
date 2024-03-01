package io.hevinxx.visibilitytracker

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onGloballyPositioned

/**
 * A composable function that tracks the visibility ratio of its content and invokes a callback
 * whenever the visible ratio changes. This is particularly useful for detecting when a component
 * becomes visible or hidden in parts, such as in scrolling lists or when obscured by other elements.
 *
 * @param onVisibleRatioChanged A callback function that is invoked with the current visibility ratio.
 *                              The visibility ratio is a float value between 0 and 1, where 1 means
 *                              fully visible and 0 means not visible.
 * @param content The composable content whose visibility is to be tracked. This content is wrapped
 *                within the VisibilityTracker and its visibility changes are monitored.
 *
 * Example usage:
 * ```
 * VisibilityTracker(onVisibleRatioChanged = { ratio ->
 *     println("Current visibility ratio: $ratio")
 * }) {
 *     // Your composable content here
 * }
 * ```
 *
 * Note: The visibility ratio calculation takes into account the visible area of the content
 * relative to its total area, adjusting for any overlap with parent composables or the edge
 * of the screen.
 */
@Composable
fun VisibilityTracker(
    onVisibleRatioChanged: (Float) -> Unit,
    content: @Composable () -> Unit,
) {
    var contentBounds by remember {
        mutableStateOf(Rect(-1f, -1f, -1f, -1f))
    }
    var givenArea by remember {
        mutableStateOf(Rect.Zero)
    }
    val visibleRatio by remember {
        derivedStateOf {
            if (contentBounds.isEmpty) {
                0f
            } else {
                (givenArea.width * givenArea.height) / (contentBounds.width * contentBounds.height)
            }
        }
    }

    Box(
        modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
            contentBounds = layoutCoordinates.toRect()
            var intersection = contentBounds
            var node = layoutCoordinates
            while (node.parentLayoutCoordinates != null) {
                node = node.parentLayoutCoordinates!!
                intersection = intersection.intersect(node.toRect())
            }
            givenArea = intersection
        }
    ) {
        content()
    }

    var previousVisibleRatio by remember { mutableStateOf<Float?>(null) }
    LaunchedEffect(key1 = visibleRatio) {
        if (visibleRatio != previousVisibleRatio) {
            previousVisibleRatio = visibleRatio
            onVisibleRatioChanged(visibleRatio)
        }
    }
}