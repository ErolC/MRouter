package com.erolc.mrouter.window

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity

import androidx.window.layout.WindowMetricsCalculator

@Composable
internal fun calculateWindowSizeClass(context: Context): WindowSize {
    val density = LocalDensity.current
    val metrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(context)
    val size = with(density) { metrics.bounds.toComposeRect().size.toDpSize() }
    return WindowSize.calculateFromSize(size)
}
