package com.erolc.mrouter.window


import java.awt.*
import java.awt.event.WindowEvent
import java.awt.event.WindowFocusListener

/**
 * Track position of all opened windows and provide an appropriate location for new created windows.
 *
 * Needed to place windows in cascade, and on the same screen.
 *
 * Singleton because we have only the single platform.
 * We basically override the standard behaviour of the window manager.
 */
internal object WindowLocationTracker {
    private val cascadeOffset = Point(48, 48)

    private var lastFocusedWindows = mutableSetOf<Window>()

    private val focusListener = object : WindowFocusListener {
        override fun windowGainedFocus(e: WindowEvent) {
            // put window on the top of the set
            lastFocusedWindows.remove(e.window)
            lastFocusedWindows.add(e.window)
        }

        override fun windowLostFocus(e: WindowEvent) = Unit
    }

    fun onWindowCreated(window: Window) {
        window.addWindowFocusListener(focusListener)
    }

    fun onWindowDisposed(window: Window) {
        window.removeWindowFocusListener(focusListener)
        lastFocusedWindows.remove(window)
    }

    val lastActiveGraphicsConfiguration: GraphicsConfiguration? get() =
        lastFocusedWindows.lastOrNull()?.graphicsConfiguration

    fun getCascadeLocationFor(window: Window): Point {
        val lastWindow = lastFocusedWindows.lastOrNull()
        val graphicsConfiguration = lastWindow?.graphicsConfiguration ?:
        GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice?.defaultConfiguration

        return if (graphicsConfiguration != null) {
            val screenBounds = graphicsConfiguration.bounds
            val screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(graphicsConfiguration)
            val screenLeftTop = screenBounds.leftTop + Point(screenInsets.left, screenInsets.top)
            val screenRightBottom = screenBounds.rightBottom - Point(screenInsets.right, screenInsets.bottom)

            val lastLocation = lastWindow?.location ?: screenLeftTop
            var location = lastLocation + cascadeOffset
            val rightBottom = location + window.size.rightBottom
            if (rightBottom.x > screenRightBottom.x || rightBottom.y > screenRightBottom.y) {
                location = screenLeftTop + cascadeOffset
            }
            location
        } else {
            cascadeOffset
        }
    }
}

internal val Dimension.rightBottom get() = Point(width, height)
internal operator fun Point.plus(other: Point) = Point(x + other.x, y + other.y)
internal operator fun Point.minus(other: Point) = Point(x - other.x, y - other.y)

internal val Rectangle.leftTop get() = Point(x, y)
internal val Rectangle.rightBottom get() = Point(x + width, y + height)
