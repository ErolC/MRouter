package com.erolc.mrouter.utils


import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.erolc.mrouter.backstack.WindowEntry
import com.erolc.mrouter.dialog.DialogOptions
import com.erolc.mrouter.model.WindowOptions
import com.erolc.mrouter.window.WindowSize
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.UIKit.UIScreen

@Composable
actual fun Platform(content: @Composable () -> Unit) {
    content()
}


@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformWindow(
    options: WindowOptions,
    entry: WindowEntry,
    content: @Composable () -> Unit
) {
    entry.getScope().windowSize.value = UIScreen.mainScreen.bounds.useContents {
        with(LocalDensity.current) {
            val size = DpSize(size.width.toFloat().toDp(), size.height.toFloat().toDp())
            WindowSize.calculateFromSize(size)
        }
    }
    content()
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


actual fun getPlatform(): Platform = Ios
