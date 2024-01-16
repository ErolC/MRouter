package com.erolc.mrouter.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.erolc.mrouter.model.WindowOptions
import com.erolc.mrouter.window.calculateWindowSizeClass
import com.erolc.mrouter.backstack.WindowEntry


@Composable
actual fun Platform(content: @Composable () -> Unit) {

}

@Composable
actual fun PlatformWindow(
    options: WindowOptions,
    entry: WindowEntry,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    entry.getScope().windowSize.value = calculateWindowSizeClass(context = context)
    content()
}


actual fun getPlatform(): Platform = Android