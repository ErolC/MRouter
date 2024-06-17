package com.erolc.mrouter.window

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

internal val DefHostSize = HostSize.calculateFromSize(DpSize.Zero)

/**
 * @author erolc
 * @since 2023/11/13 10:22
 */
@SinceKotlin("1.0")
class HostSize private constructor(val width: HostWidthSize, val height: HostHeightSize) {
    companion object {
        /**
         * Calculates [HostSize] for a given [size]. Should be used for testing purposes only
         * - to calculate a [HostSize] for the Activity's current window see
         * [calculateHostSize].
         *
         * @param size of the window
         * @return [HostSize] corresponding to the given width and height
         */
        fun calculateFromSize(size: DpSize): HostSize {
            val windowWidthSize = HostWidthSize.fromWidth(size.width)
            val windowHeightSize = HostHeightSize.fromHeight(size.height)
            return HostSize(windowWidthSize, windowHeightSize)
        }

        internal fun withWindowSize(windowSize: WindowSize): HostSize {
            return HostSize(
                HostWidthSize.fromWidth(windowSize.width.size),
                HostHeightSize.fromHeight(windowSize.height.size)
            )
        }
    }
}

class HostWidthSize private constructor(val value: Int) {
    var size: Dp = 0.dp
        private set

    companion object {
        /** Represents the majority of phones in portrait. */
        val Compact = HostWidthSize(0)

        /**
         * Represents the majority of tablets in portrait and large unfolded inner displays in
         * portrait.
         */
        val Medium = HostWidthSize(1)

        /**
         * Represents the majority of tablets in landscape and large unfolded inner displays in
         * landscape.
         */
        val Expanded = HostWidthSize(2)

        /** Calculates the [HostWidthSize] for a given [width] */
        internal fun fromWidth(width: Dp): HostWidthSize {
            require(width >= 0.dp) { "Width must not be negative" }
            return when {
                width < 600.dp -> Compact
                width < 840.dp -> Medium
                else -> Expanded
            }.apply { size = width }
        }
    }
}

class HostHeightSize private constructor(val value: Int) {
    var size: Dp = 0.dp
        private set

    companion object {
        /** Represents the majority of phones in landscape */
        val Compact = HostHeightSize(0)

        /** Represents the majority of tablets in landscape and majority of phones in portrait */
        val Medium = HostHeightSize(1)

        /** Represents the majority of tablets in portrait */
        val Expanded = HostHeightSize(2)

        /** Calculates the [HostHeightSize] for a given [height] */
        internal fun fromHeight(height: Dp): HostHeightSize {
            require(height >= 0.dp) { "Height must not be negative" }
            return when {
                height < 480.dp -> Compact
                height < 900.dp -> Medium
                else -> Expanded
            }.apply { size = height }
        }
    }
}