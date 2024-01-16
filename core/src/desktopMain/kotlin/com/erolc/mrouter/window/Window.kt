package com.erolc.mrouter.window

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import com.erolc.mrouter.utils.loge
import java.awt.*
import java.awt.event.*
import javax.swing.JFrame

/**
 * Composes platform window in the current composition. When Window enters the composition,
 * a new platform window will be created and receives the focus. When Window leaves the
 * composition, window will be disposed and closed.
 *
 * Initial size of the window is controlled by [WindowState.size].
 * Initial position of the window is controlled by [WindowState.position].
 *
 * Usage in single-window application ([ApplicationScope.exitApplication] will close all the
 * windows and stop all effects defined in [application]):
 * ```
 * fun main() = application {
 *     Window(onCloseRequest = ::exitApplication)
 * }
 * ```
 *
 * or if it only needed to close the main window without closing all other opened windows:
 * ```
 * fun main() = application {
 *     val isOpen by remember { mutableStateOf(true) }
 *     if (isOpen) {
 *         Window(onCloseRequest = { isOpen = false })
 *     }
 * }
 * ```
 *
 * @param onCloseRequest Callback that will be called when the user closes the window.
 * Usually in this callback we need to manually tell Compose what to do:
 * - change `isOpen` state of the window (which is manually defined)
 * - close the whole application (`onCloseRequest = ::exitApplication` in [ApplicationScope])
 * - don't close the window on close request (`onCloseRequest = {}`)
 * @param state The state object to be used to control or observe the window's state
 * When size/position/status is changed by the user, state will be updated.
 * When size/position/status of the window is changed by the application (changing state),
 * the native window will update its corresponding properties.
 * If application changes, for example [WindowState.placement], then after the next
 * recomposition, [WindowState.size] will be changed to correspond the real size of the window.
 * If [WindowState.position] is not [WindowPosition.isSpecified], then after the first show on the
 * screen [WindowState.position] will be set to the absolute values.
 * @param visible Is [Window] visible to user.
 * If `false`:
 * - internal state of [Window] is preserved and will be restored next time the window
 * will be visible;
 * - native resources will not be released. They will be released only when [Window]
 * will leave the composition.
 * @param title Title in the titlebar of the window
 * @param icon Icon in the titlebar of the window (for platforms which support this).
 * On macOs individual windows can't have a separate icon. To change the icon in the Dock,
 * set it via `iconFile` in build.gradle
 * (https://github.com/JetBrains/compose-jb/tree/master/tutorials/Native_distributions_and_local_execution#platform-specific-options)
 * @param undecorated Disables or enables decorations for this window.
 * @param transparent Disables or enables window transparency. Transparency should be set
 * only if window is undecorated, otherwise an exception will be thrown.
 * @param resizable Can window be resized by the user (application still can resize the window
 * changing [state])
 * @param enabled Can window react to input events
 * @param focusable Can window receive focus
 * @param alwaysOnTop Should window always be on top of another windows
 * @param onPreviewKeyEvent This callback is invoked when the user interacts with the hardware
 * keyboard. It gives ancestors of a focused component the chance to intercept a [KeyEvent].
 * Return true to stop propagation of this event. If you return false, the key event will be
 * sent to this [onPreviewKeyEvent]'s child. If none of the children consume the event,
 * it will be sent back up to the root using the onKeyEvent callback.
 * @param onKeyEvent This callback is invoked when the user interacts with the hardware
 * keyboard. While implementing this callback, return true to stop propagation of this event.
 * If you return false, the key event will be sent to this [onKeyEvent]'s parent.
 * @param content Content of the window
 */
@Composable
fun Window(
    onCloseRequest: () -> Unit,
    state: WindowState = rememberWindowState(),
    minimumSize: DpSize = DpSize(600.dp, 400.dp),
    visible: Boolean = true,
    title: String = "Untitled",
    icon: Painter? = null,
    undecorated: Boolean = false,
    transparent: Boolean = false,
    resizable: Boolean = true,
    enabled: Boolean = true,
    focusable: Boolean = true,
    alwaysOnTop: Boolean = false,
    onPreviewKeyEvent: (KeyEvent) -> Boolean = { false },
    onKeyEvent: (KeyEvent) -> Boolean = { false },
    content: @Composable FrameWindowScope.() -> Unit
) {
    val currentState by rememberUpdatedState(state)
    val currentTitle by rememberUpdatedState(title)
    val currentIcon by rememberUpdatedState(icon)
    val currentUndecorated by rememberUpdatedState(undecorated)
    val currentTransparent by rememberUpdatedState(transparent)
    val currentResizable by rememberUpdatedState(resizable)
    val currentEnabled by rememberUpdatedState(enabled)
    val currentFocusable by rememberUpdatedState(focusable)
    val currentAlwaysOnTop by rememberUpdatedState(alwaysOnTop)
    val currentOnCloseRequest by rememberUpdatedState(onCloseRequest)
    val currentMinimumSize by rememberUpdatedState(minimumSize)

    val updater = remember(::ComponentUpdater)

    // the state applied to the window. exist to avoid races between WindowState changes and the state stored inside the native window
    val appliedState = remember {
        object {
            var size: DpSize? = null
            var position: WindowPosition? = null
            var placement: WindowPlacement? = null
            var isMinimized: Boolean? = null
        }
    }

    val listeners = remember {
        object {
            var windowListenerRef = windowListenerRef()
            var windowStateListenerRef = windowStateListenerRef()
            var componentListenerRef = componentListenerRef()

            fun removeFromAndClear(window: ComposeWindow) {
                windowListenerRef.unregisterFromAndClear(window)
                windowStateListenerRef.unregisterFromAndClear(window)
                componentListenerRef.unregisterFromAndClear(window)
            }
        }
    }

    Window(
        visible = visible,
        onPreviewKeyEvent = onPreviewKeyEvent,
        onKeyEvent = onKeyEvent,
        create = {
            val graphicsConfiguration = WindowLocationTracker.lastActiveGraphicsConfiguration
            ComposeWindow(graphicsConfiguration = graphicsConfiguration).apply {
                // close state is controlled by WindowState.isOpen
                defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
                listeners.windowListenerRef.registerWithAndSet(
                    this,
                    object : WindowAdapter() {
                        override fun windowClosing(e: WindowEvent) {
                            currentOnCloseRequest()
                        }
                    }
                )
                listeners.windowStateListenerRef.registerWithAndSet(this) {
                    currentState.placement = placement
                    currentState.isMinimized = isMinimized
                    appliedState.placement = currentState.placement
                    appliedState.isMinimized = currentState.isMinimized
                }
                listeners.componentListenerRef.registerWithAndSet(
                    this,
                    object : ComponentAdapter() {
                        override fun componentResized(e: ComponentEvent) {
                            // we check placement here and in windowStateChanged,
                            // because fullscreen changing doesn't
                            // fire windowStateChanged, only componentResized
                            currentState.placement = placement
                            val width = if (width.dp < currentMinimumSize.width) currentMinimumSize.width else width.dp
                            val height = if (height.dp < currentMinimumSize.height) currentMinimumSize.height else height.dp
                            currentState.size = DpSize(width, height)
                            appliedState.placement = currentState.placement
                            appliedState.size = currentState.size
                        }

                        override fun componentMoved(e: ComponentEvent) {
                            currentState.position = WindowPosition(x.dp, y.dp)
                            appliedState.position = currentState.position
                        }
                    }
                )
                WindowLocationTracker.onWindowCreated(this)
            }
        },
        dispose = {
            WindowLocationTracker.onWindowDisposed(it)
            // We need to remove them because AWT can still call them after dispose()
            listeners.removeFromAndClear(it)
            it.dispose()
        },
        update = { window ->
            updater.update {
                set(currentTitle, window::setTitle)
                set(currentIcon, window::setIcon)
                set(currentUndecorated, window::setUndecoratedSafely)
                set(currentTransparent, window::isTransparent::set)
                set(currentResizable, window::setResizable)
                set(currentEnabled, window::setEnabled)
                set(currentFocusable, window::setFocusableWindowState)
                set(currentAlwaysOnTop, window::setAlwaysOnTop)
            }
            if (state.size != appliedState.size) {
                window.setSizeSafely(state.size, state.placement)
                appliedState.size = state.size
            }
            if (state.position != appliedState.position) {
                window.setPositionSafely(
                    state.position,
                    state.placement,
                    platformDefaultPosition = { WindowLocationTracker.getCascadeLocationFor(window) }
                )
                appliedState.position = state.position
            }
            if (state.placement != appliedState.placement) {
                window.placement = state.placement
                appliedState.placement = state.placement
            }
            if (state.isMinimized != appliedState.isMinimized) {
                window.isMinimized = state.isMinimized
                appliedState.isMinimized = state.isMinimized
            }
        },
        content = content
    )
}





