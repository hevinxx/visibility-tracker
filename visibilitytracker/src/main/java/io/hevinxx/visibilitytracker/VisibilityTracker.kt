package io.hevinxx.visibilitytracker

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
 * whenever the visible ratio changes or crosses predefined thresholds. This functionality is useful for
 * executing specific actions based on the visibility state of a composable, especially in scrollable layouts
 * or when obscured by other elements.
 *
 * @param thresholds An optional list of float values representing visibility thresholds to trigger the callback.
 *                   Each threshold should be a value between 0 and 1, where 0 indicates completely
 *                   invisible and 1 indicates fully visible. The callback is invoked whenever the
 *                   visibility ratio crosses any of these thresholds, either by becoming more or less
 *                   visible. If null, the callback is invoked on any visibility ratio change.
 *                   Default value is listOf(1f) which means the callback is only invoked when a
 *                   composable becomes fully visible (visibility ratio of 1) or not.
 * @param onVisibleRatioChanged A callback function that is invoked with the current visibility ratio.
 *                              The visibility ratio is a float value between 0 and 1, where 1 means
 *                              fully visible and 0 means not visible.
 * @param treatOnStopAsInvisible A boolean parameter that determines whether to treat the content as
 *                               completely invisible (visibilityRatio = 0) when the onStop lifecycle
 *                               event is triggered.
 * @param content The composable content whose visibility is to be tracked. This content is wrapped
 *                within the VisibilityTracker, and its visibility changes are monitored based on the
 *                specified thresholds or any change in visibility if thresholds is null.
 *
 * Example usage:
 * ```
 * VisibilityTracker(
 *     thresholds = listOf(0f, 0.5f, 1f),
 *     onVisibleRatioChanged = { value ->
 *         if (value >= 1f) {
 *             println("This component is fully visible")
 *         } else if (value >= 0.5f) {
 *             println("More than a half of this component is visible")
 *         } else if (value > 0f) {
 *             println("Less than a half of this component is visible")
 *         } else {
 *             println("This component is invisible")
 *         }
 *     }
 * ) {
 *     // Your composable content here
 * }
 * ```
 *
 * Note: The visibility ratio calculation takes into account the visible area of the content
 * relative to its total area, adjusting for any overlap with parent composables or the edge
 * of the screen. When the visibility ratio crosses a threshold or changes, the corresponding action can
 * be executed, making it ideal for dynamic UI interactions based on visibility changes.
 */
@Composable
fun VisibilityTracker(
    thresholds: List<Float>? = listOf(1f),
    onVisibleRatioChanged: (Float) -> Unit,
    treatOnStopAsInvisible: Boolean = false,
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

    var lastVisibleRatio by remember { mutableStateOf(0f) }
    LaunchedEffect(visibleRatio) {
        if (thresholds == null) {
            if (visibleRatio != lastVisibleRatio) {
                onVisibleRatioChanged(visibleRatio)
            }
        } else if (thresholds.any { threshold ->
                val wasAbove = if (threshold == 0f) {
                    lastVisibleRatio > threshold
                } else {
                    lastVisibleRatio >= threshold
                }
                val isAbove = if (threshold == 0f) {
                    visibleRatio > threshold
                } else {
                    visibleRatio >= threshold
                }
                wasAbove != isAbove
            }) onVisibleRatioChanged(visibleRatio)
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