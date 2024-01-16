package com.erolc.mrouter.window

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

internal val DefWindowSize = WindowSize.calculateFromSize(DpSize.Zero)

/**
 * @author erolc
 * @since 2023/11/13 10:22
 */
@SinceKotlin("1.0")
class WindowSize private constructor(val width: WindowWidthSize, val height: WindowHeightSize) {
    companion object {
        /**
         * Calculates [WindowSize] for a given [size]. Should be used for testing purposes only
         * - to calculate a [WindowSize] for the Activity's current window see
         * [calculateWindowSize].
         *
         * @param size of the window
         * @return [WindowSize] corresponding to the given width and height
         */
        fun calculateFromSize(size: DpSize): WindowSize {
            val windowWidthSize = WindowWidthSize.fromWidth(size.width)
            val windowHeightSize = WindowHeightSize.fromHeight(size.height)
            return WindowSize(windowWidthSize, windowHeightSize)
        }
    }
}

class WindowWidthSize private constructor(val value: Int) {
    var size: Dp = 0.dp
        private set

    companion object {
        /** Represents the majority of phones in portrait. */
        val Compact = WindowWidthSize(0)

        /**
         * Represents the majority of tablets in portrait and large unfolded inner displays in
         * portrait.
         */
        val Medium = WindowWidthSize(1)

        /**
         * Represents the majority of tablets in landscape and large unfolded inner displays in
         * landscape.
         */
        val Expanded = WindowWidthSize(2)

        /** Calculates the [WindowWidthSize] for a given [width] */
        internal fun fromWidth(width: Dp): WindowWidthSize {
            require(width >= 0.dp) { "Width must not be negative" }
            return when {
                width < 600.dp -> Compact
                width < 840.dp -> Medium
                else -> Expanded
            }.apply { size = width }
        }
    }
}

class WindowHeightSize private constructor(val value: Int) {
    var size: Dp = 0.dp
        private set

    companion object {
        /** Represents the majority of phones in landscape */
        val Compact = WindowHeightSize(0)

        /** Represents the majority of tablets in landscape and majority of phones in portrait */
        val Medium = WindowHeightSize(1)

        /** Represents the majority of tablets in portrait */
        val Expanded = WindowHeightSize(2)

        /** Calculates the [WindowHeightSize] for a given [height] */
        internal fun fromHeight(height: Dp): WindowHeightSize {
            require(height >= 0.dp) { "Height must not be negative" }
            return when {
                height < 480.dp -> Compact
                height < 900.dp -> Medium
                else -> Expanded
            }.apply { size = height }
        }
    }
}
