package com.erolc.mrouter.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.erolc.mrouter.backstack.entry.WindowEntry
import com.erolc.mrouter.model.WindowOptions
import com.erolc.mrouter.window.calculateWindowSizeClass

@Composable
actual fun PlatformWindow(
    options: WindowOptions, entry: WindowEntry, content: @Composable () -> Unit
) {
    val context = LocalContext.current
    entry.scope.windowSize.value = calculateWindowSizeClass(context = context)
    content()
}
actual fun getPlatform(): Platform = Android