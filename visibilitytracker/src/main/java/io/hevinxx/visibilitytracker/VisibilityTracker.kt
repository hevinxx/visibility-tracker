package io.hevinxx.visibilitytracker

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

/**
 * A composable function that tracks the visibility ratio of its content and invokes a callback
 * whenever the visible ratio changes. This is particularly useful for detecting when a component
 * becomes visible or hidden in parts, such as in scrolling lists or when obscured by other elements.
 *
 * @param threshold A float value representing the visibility threshold to trigger the callback. The
 *                  threshold should be a value between 0 and 1, where 0 indicates completely invisible
 *                  and 1 indicates fully visible. Default value is 1f, meaning the callback will only
 *                  be triggered when the composable becomes fully visible or not.
 * @param onVisibilityChanged A callback function that is invoked when the visibility ratio of the content
 *                            surpasses the specified threshold. The Boolean parameter signifies the
 *                            visibility state, with true indicating that the composable has become
 *                            more visible than the threshold ratio, and false indicating it has become
 *                            less visible. This callback is triggered whenever the visibility ratio
 *                            crosses the threshold in either direction.
 * @param treatOnStopAsInvisible A boolean parameter that determines whether to treat the content as
 *                               completely invisible (visibilityRatio = 0) when the onStop lifecycle
 *                               event is triggered. This can be useful for scenarios where visibility
 *                               needs to be reset or not counted when the component is not actively
 *                               displayed to the user.
 * @param content The composable content whose visibility is to be tracked. This content is wrapped
 *                within the VisibilityTracker and its visibility changes are monitored.
 *
 * Example usage:
 * ```
 * VisibilityTracker(
 *     threshold = 0.5f,
 *     onVisibilityChanged = { isVisible ->
 *         if (isVisible) println("More than a half of this component is visible.")
 *     }) {
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
    threshold: Float = 1f,
    onVisibilityChanged: (Boolean) -> Unit,
    treatOnStopAsInvisible: Boolean = true,
    content: @Composable () -> Unit,
) {
    var contentBounds by remember {
        mutableStateOf(Rect(-1f, -1f, -1f, -1f))
    }
    var givenArea by remember {
        mutableStateOf(Rect.Zero)
    }
    var isStarted by remember {
        mutableStateOf(false)
    }
    val visibleRatio by remember {
        derivedStateOf {
            if (treatOnStopAsInvisible && !isStarted) {
                0f
            } else if (contentBounds.isEmpty) {
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

    var lastVisibleRatio by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(key1 = visibleRatio) {
        val wasAbove = lastVisibleRatio >= threshold
        val isAbove = visibleRatio >= threshold
        if (wasAbove != isAbove) {
            onVisibilityChanged(isAbove > wasAbove)
        }
        lastVisibleRatio = visibleRatio
    }

    if (treatOnStopAsInvisible) {
        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    isStarted = true
                } else if (event == Lifecycle.Event.ON_STOP) {
                    isStarted = false
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    }
}