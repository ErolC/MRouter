package com.erolc.mrouter.utils

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.erolc.mrouter.Constants
import com.erolc.mrouter.LocalApplication
import com.erolc.mrouter.backstack.WindowEntry
import com.erolc.mrouter.dialog.DialogOptions
import com.erolc.lifecycle.Lifecycle
import com.erolc.mrouter.model.WindowOptions
import com.erolc.mrouter.scope.WindowScope
import com.erolc.mrouter.window.WindowSize
import com.erolc.mrouter.window.toDimension
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale


@Composable
actual fun Platform(content: @Composable () -> Unit) {

}


@Composable
actual fun PlatformWindow(
    options: WindowOptions,
    entry: WindowEntry,
    content: @Composable () -> Unit
) {
    val state = rememberWindowState(position = options.alignment?.let { WindowPosition(it) }
        ?: WindowPosition(options.position.x, options.position.y), size = options.size)
    val size by remember(state.size) {
        mutableStateOf(WindowSize.calculateFromSize(state.size))
    }

    val event = if (state.isMinimized) Lifecycle.Event.ON_PAUSE else Lifecycle.Event.ON_RESUME
    entry.getScope().onLifeEvent(event)
    entry.getScope().windowSize.value = size
    entry.options = options.copy(position = DpOffset(state.position.x,state.position.y), size = state.size)
    val scope = rememberCoroutineScope()
    val application =
        LocalApplication.current
            ?: throw RuntimeException("请使用mRouterApplication替代原本的application")
    var isCloseWindow by remember { entry.isCloseWindow }
    if (!isCloseWindow)
        Window(
            title = options.title,
            icon = options.icon,
            onCloseRequest = {
                isCloseWindow = true
                scope.launch {
                    delay(1000)
                    val isExit = entry.close()
                    if (isExit) application.exitApplication()
                }
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

@OptIn(ExperimentalComposeUiApi::class)
fun DialogOptions.asProperties(): DialogProperties {
    return DialogProperties(
        dismissOnBackPress,
        dismissOnClickOutside,
        false,
        usePlatformInsets,
        Color.Transparent
    )
}

@Composable
actual fun PlatformDialog(
    onDismissRequest: () -> Unit,
    options: DialogOptions,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest, options.asProperties()) {
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
