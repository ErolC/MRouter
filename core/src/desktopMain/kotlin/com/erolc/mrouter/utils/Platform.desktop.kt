package com.erolc.mrouter.utils

import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.erolc.mrouter.Constants
import com.erolc.mrouter.LocalApplication
import com.erolc.mrouter.backstack.WindowEntry
import com.erolc.mrouter.lifecycle.Lifecycle
import com.erolc.mrouter.model.WindowOptions
import com.erolc.mrouter.scope.WindowScope
import com.erolc.mrouter.window.WindowSize
import com.erolc.mrouter.window.toDimension
import java.util.Locale


@Composable
actual fun Platform(content: @Composable () -> Unit) {

}


@Composable
actual fun PlatformWindow(options: WindowOptions, entry: WindowEntry, content: @Composable () -> Unit) {
    val state = rememberWindowState(position = options.alignment?.let { WindowPosition(it) }
        ?: WindowPosition(options.position.x, options.position.y), size = options.size)
    val size by remember(state.size) {
        mutableStateOf(WindowSize.calculateFromSize(state.size))
    }
    var lifecycleEvent by remember(entry.getScope()) { entry.getScope().lifecycleEvent }
    lifecycleEvent = if (state.isMinimized) Lifecycle.Event.ON_STOP else Lifecycle.Event.ON_RESUME
    entry.getScope().windowSize.value = size
    val application =
        LocalApplication.current
            ?: throw RuntimeException("请使用mRouterApplication替代原本的application")
    Window(
        title = options.title,
        icon = options.icon,
        onCloseRequest = {
            val isExit = entry.close()
            if (isExit) application.exitApplication()
        },
        state = state
    ) {
        val minimumSize by rememberUpdatedState(options.minimumSize.toDimension())
        val maximumSize by rememberUpdatedState(options.maximumSize)
        window.minimumSize = minimumSize
        if (maximumSize.isSpecified) window.maximumSize = maximumSize.toDimension()
        //todo 需要考虑menu
        content()
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
