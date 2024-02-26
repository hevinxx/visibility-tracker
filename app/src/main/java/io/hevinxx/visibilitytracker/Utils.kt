package io.hevinxx.visibilitytracker

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInWindow

fun LayoutCoordinates.toRect() = Rect(
    left = this.positionInWindow().x,
    top = this.positionInWindow().y,
    right = this.positionInWindow().x + this.size.width,
    bottom = this.positionInWindow().y + this.size.height,
)