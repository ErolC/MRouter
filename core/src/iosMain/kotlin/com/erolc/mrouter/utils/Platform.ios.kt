package com.erolc.mrouter.utils


import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.erolc.mrouter.backstack.entry.WindowEntry
import com.erolc.mrouter.model.WindowOptions
import com.erolc.mrouter.window.WindowSize
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.UIKit.UIScreen

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformWindow(
    options: WindowOptions,
    entry: WindowEntry,
    content: @Composable () -> Unit
) {
    entry.scope.windowSize.value = UIScreen.mainScreen.bounds.useContents {
        val size = DpSize(size.width.dp, size.height.dp)
        WindowSize.calculateFromSize(size)
    }
    content()
}

actual fun getPlatform(): Platform = Ios
