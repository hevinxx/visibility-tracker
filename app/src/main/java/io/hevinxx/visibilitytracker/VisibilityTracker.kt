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