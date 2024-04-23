package com.erolc.mrouter.utils

import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import com.erolc.lifecycle.WindowLifecycleListener
import com.erolc.mrouter.LocalApplicationScope
import com.erolc.mrouter.backstack.entry.WindowEntry
import com.erolc.mrouter.model.WindowOptions
import com.erolc.mrouter.scope.rememberInWindow
import com.erolc.mrouter.window.*
import kotlinx.coroutines.delay
import java.util.*

@Composable
actual fun PlatformWindow(
    options: WindowOptions,
    entry: WindowEntry,
    content: @Composable () -> Unit
) {
    val state = rememberInWindow("window_state") {
        WindowState(
            placement = options.state.toPlacement(),
            position = options.alignment?.let { WindowPosition(it) }
                ?: WindowPosition(options.position.x, options.position.y),
            size = options.size
        )
    }
    val size by remember(state.size) {
        mutableStateOf(WindowSize.calculateFromSize(state.size))
    }
    val windowListenerRef = remember { windowListenerRef() }
    val windowFocusListenerRef = remember { windowFocusListenerRef() }

    entry.scope.windowSize.value = size
    entry.options.value =
        options.copy(position = DpOffset(state.position.x, state.position.y), size = state.size)

    val isCloseWindow by rememberInWindow("window_close") { entry.scope.isCloseWindow }
    if (!isCloseWindow)
        Window(
            title = options.title,
            icon = options.icon,
            alwaysOnTop = options.alwaysOnTop,
            resizable = options.resizable,
            onCloseRequest = {
                entry.scope.close()
            },
            state = state
        ) {
            val minimumSize by rememberUpdatedState(options.minimumSize.toDimension())
            val maximumSize by rememberUpdatedState(options.maximumSize)
            window.minimumSize = minimumSize
            if (maximumSize.isSpecified) window.maximumSize = maximumSize.toDimension()
            Menu(options.id)
            content()
            DisposableEffect(window) {
                windowListenerRef.registerWithAndSet(window, WindowLifecycleListener)
                windowFocusListenerRef.registerWithAndSet(window, WindowLifecycleListener)
                onDispose {
                    windowListenerRef.unregisterFromAndClear(window)
                    windowFocusListenerRef.unregisterFromAndClear(window)
                }
            }
        }
    else {
        LaunchedEffect(Unit) {
            delay(10)
        }
        if (entry.shouldExit()) LocalApplicationScope.current.exitApplication()
    }

}

actual fun getPlatform(): Platform {
    val os = System.getProperty("os.name").lowercase(Locale.getDefault())
    return when {
        os.contains("win") -> Windows
        os.contains("mac") -> Mac
        os.contains("nix") || os.contains("nux") || os.contains("nux") || os.contains("bsd") -> Linux
        else -> UnKnow
    }
}
