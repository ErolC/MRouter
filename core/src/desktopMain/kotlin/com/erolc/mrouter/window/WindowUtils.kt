package com.erolc.mrouter.window

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import com.erolc.mrouter.Constants
import com.erolc.mrouter.backstack.entry.LocalWindowScope
import com.erolc.mrouter.model.WindowMenu
import com.erolc.mrouter.model.WindowState
import com.erolc.mrouter.register.RegisterBuilder
import java.awt.*
import java.awt.event.ComponentListener
import java.awt.event.WindowFocusListener
import java.awt.event.WindowListener
import java.awt.event.WindowStateListener
import java.util.*
import kotlin.math.roundToInt

/**
 * 注册[windowId]对应的窗口菜单
 */
fun RegisterBuilder.windowMenu(
    windowId: String = Constants.DEFAULT_WINDOW,
    menu: @Composable FrameWindowScope.() -> Unit
) {
    registerPlatformResource(windowId, WindowMenu(windowId, menu))
}

@Composable
internal fun FrameWindowScope.Menu(windowId: String) {
    val windowMenu = LocalWindowScope.current.getPlatformRes(windowId) as? WindowMenu
    windowMenu?.run { menu() }
}


/**
 * Sets the position of the window, given its placement.
 * If the window is already visible, then change the position only if it's floating, in order to
 * avoid resetting the maximized / fullscreen state.
 * If the window is not visible yet, we _do_ set its size so that it will have an "un-maximized"
 * position to go to when the user un-maximizes the window.
 */
internal fun Window.setPositionSafely(
    position: WindowPosition,
    placement: WindowPlacement,
    platformDefaultPosition: () -> Point
) {
    if (!isVisible || (placement == WindowPlacement.Floating)) {
        setPositionImpl(position, platformDefaultPosition)
    }
}

/**
 * Sets the size of the window, given its placement.
 * If the window is already visible, then change the size only if it's floating, in order to
 * avoid resetting the maximized / fullscreen state.
 * If the window is not visible yet, we _do_ set its size so that:
 * - It will have an "un-maximized" size to go to when the user un-maximizes the window.
 * - To allow drawing the first frame (at the correct size) before the window is made visible.
 */
internal fun Window.setSizeSafely(size: DpSize, placement: WindowPlacement) {
    if (!isVisible || (placement == WindowPlacement.Floating)) {
        setSizeImpl(size)
    }
}

private fun Window.setSizeImpl(size: DpSize) {
    val availableSize by lazy {
        val screenBounds = graphicsConfiguration.bounds
        val screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(graphicsConfiguration)

        IntSize(
            width = screenBounds.width - screenInsets.left - screenInsets.right,
            height = screenBounds.height - screenInsets.top - screenInsets.bottom
        )
    }

    val isWidthSpecified = size.isSpecified && size.width.isSpecified
    val isHeightSpecified = size.isSpecified && size.height.isSpecified

    val width = if (isWidthSpecified) {
        size.width.value.roundToInt().coerceAtLeast(0)
    } else {
        availableSize.width
    }

    val height = if (isHeightSpecified) {
        size.height.value.roundToInt().coerceAtLeast(0)
    } else {
        availableSize.height
    }

    var computedPreferredSize: Dimension? = null
    if (!isWidthSpecified || !isHeightSpecified) {
        preferredSize = Dimension(width, height)
        pack()  // Makes it displayable

        // We set preferred size to null, and then call getPreferredSize, which will compute the
        // actual preferred size determined by the content (see the description of setPreferredSize)
        preferredSize = null
        computedPreferredSize = preferredSize
    }

    if (!isDisplayable) {
        // Pack to allow drawing the first frame
        preferredSize = Dimension(width, height)
        pack()
    }

    setSize(
        if (isWidthSpecified) width else computedPreferredSize!!.width,
        if (isHeightSpecified) height else computedPreferredSize!!.height,
    )
    revalidate()  // Calls doLayout on the ComposeLayer, causing it to update its size
}

internal fun Window.setPositionImpl(
    position: WindowPosition,
    platformDefaultPosition: () -> Point
) = when (position) {
    WindowPosition.PlatformDefault -> location = platformDefaultPosition()
    is WindowPosition.Aligned -> align(position.alignment)
    is WindowPosition.Absolute -> setLocation(
        position.x.value.roundToInt(),
        position.y.value.roundToInt()
    )
}

internal fun Window.align(alignment: Alignment) {
    val screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(graphicsConfiguration)
    val screenBounds = graphicsConfiguration.bounds
    val size = IntSize(size.width, size.height)
    val screenSize = IntSize(
        screenBounds.width - screenInsets.left - screenInsets.right,
        screenBounds.height - screenInsets.top - screenInsets.bottom
    )
    val location = alignment.align(size, screenSize, LayoutDirection.Ltr)

    setLocation(
        screenBounds.x + screenInsets.left + location.x,
        screenBounds.y + screenInsets.top + location.y
    )
}


/**
 * We cannot call [Frame.setUndecorated] if window is showing - AWT will throw an exception.
 * But we can call [Frame.setUndecoratedSafely] if isUndecorated isn't changed.
 */
internal fun Frame.setUndecoratedSafely(value: Boolean) {
    if (this.isUndecorated != value) {
        this.isUndecorated = value
    }
}


/**
 * Stores the previous applied state, and provide ability to update component if the new state is
 * changed.
 */
internal class ComponentUpdater {
    private var updatedValues = mutableListOf<Any?>()

    fun update(body: UpdateScope.() -> Unit) {
        UpdateScope().body()
    }

    inner class UpdateScope {
        private var index = 0

        /**
         * Compare [value] with the old one and if it is changed - store a new value and call
         * [update]
         */
        fun <T : Any?> set(value: T, update: (T) -> Unit) {
            if (index < updatedValues.size) {
                if (updatedValues[index] != value) {
                    update(value)
                    updatedValues[index] = value
                }
            } else {
                check(index == updatedValues.size)
                update(value)
                updatedValues.add(value)
            }

            index++
        }
    }
}

internal class ListenerOnWindowRef<T>(
    private val register: Window.(T) -> Unit,
    private val unregister: Window.(T) -> Unit
) {
    private var value: T? = null

    fun registerWithAndSet(window: Window, listener: T) {
        window.register(listener)
        value = listener
    }

    fun unregisterFromAndClear(window: Window) {
        value?.let {
            window.unregister(it)
            value = null
        }
    }
}


internal fun windowStateListenerRef() = ListenerOnWindowRef<WindowStateListener>(
    register = Window::addWindowStateListener,
    unregister = Window::removeWindowStateListener
)

internal fun windowListenerRef() = ListenerOnWindowRef<WindowListener>(
    register = Window::addWindowListener,
    unregister = Window::removeWindowListener
)

internal fun windowFocusListenerRef() = ListenerOnWindowRef<WindowFocusListener>(
    register = Window::addWindowFocusListener,
    unregister = Window::removeWindowFocusListener
)

internal fun componentListenerRef() = ListenerOnWindowRef<ComponentListener>(
    register = Component::addComponentListener,
    unregister = Component::removeComponentListener
)


private val GraphicsConfiguration.density: Density
    get() = Density(
        defaultTransform.scaleX.toFloat(),
        fontScale = 1f
    )

internal val Component.density: Density get() = graphicsConfiguration.density


internal val ComponentOrientation.layoutDirection: LayoutDirection
    get() = when {
        isLeftToRight -> LayoutDirection.Ltr
        isHorizontal -> LayoutDirection.Rtl
        else -> LayoutDirection.Ltr
    }

internal val Locale.layoutDirection: LayoutDirection
    get() = ComponentOrientation.getOrientation(this).layoutDirection

/**
 * Compute the [LayoutDirection] the given AWT/Swing component should have, based on its own,
 * non-Compose attributes.
 */
internal fun layoutDirectionFor(component: Component): LayoutDirection {
    val orientation = component.componentOrientation
    return if (orientation != ComponentOrientation.UNKNOWN) {
        orientation.layoutDirection
    } else {
        // To preserve backwards compatibility we fall back to the locale
        return component.locale.layoutDirection
    }
}

private val iconSize = Size(32f, 32f)

internal fun Window.setIcon(painter: Painter?) {
    setIconImage(painter?.toAwtImage(density, layoutDirectionFor(this), iconSize))
}


@Composable
fun DpSize.toDimension() = Dimension(width.value.toInt(), height.value.toInt())

@Composable
fun Size.toDimension() = Dimension(width.toInt(), height.toInt())


fun WindowState.toPlacement() = when (this) {
    WindowState.Floating -> WindowPlacement.Floating
    WindowState.Maximized -> WindowPlacement.Maximized
    WindowState.Fullscreen -> WindowPlacement.Fullscreen
}