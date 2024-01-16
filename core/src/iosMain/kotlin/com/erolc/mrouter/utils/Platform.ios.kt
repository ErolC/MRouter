package com.erolc.mrouter.utils


import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import com.erolc.mrouter.backstack.WindowEntry
import com.erolc.mrouter.model.WindowOptions
import com.erolc.mrouter.window.WindowSize
import platform.UIKit.UIScreen

@Composable
actual fun Platform(content: @Composable () -> Unit) {
    content()
}


@Composable
actual fun PlatformWindow(options: WindowOptions, entry: WindowEntry, content: @Composable () -> Unit) {
    entry.getScope().windowScope.windowSize.value = UIScreen.mainScreen.bounds.useContents {
        with(LocalDensity.current) {
            val size = DpSize(size.width.toFloat().toDp(), size.height.toFloat().toDp())
            WindowSize.calculateFromSize(size)
        }
    }
    content()
}

actual fun getPlatform(): Platform = Ios
