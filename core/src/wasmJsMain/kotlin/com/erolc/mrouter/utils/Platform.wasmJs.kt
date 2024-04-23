package com.erolc.mrouter.utils

import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.erolc.mrouter.backstack.entry.LocalWindowScope
import com.erolc.mrouter.backstack.entry.WindowEntry
import com.erolc.mrouter.model.WindowOptions
import com.erolc.mrouter.window.WindowSize
import kotlinx.browser.window

@Composable
actual fun PlatformWindow(
    options: WindowOptions,
    entry: WindowEntry,
    content: @Composable () -> Unit
) {
    var size by remember {
        mutableStateOf(DpSize(window.innerWidth.dp, window.innerWidth.dp))
    }
    window.addEventListener("resize") {
        size = DpSize(window.innerWidth.dp, window.innerWidth.dp)
    }
    LocalWindowScope.current.windowSize.value = WindowSize.calculateFromSize(size)
    content()
}

actual fun getPlatform(): Platform = Web