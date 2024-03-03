package com.erolc.mrouter.utils

import androidx.compose.runtime.Composable
import com.erolc.mrouter.backstack.entry.WindowEntry
import com.erolc.mrouter.dialog.DialogOptions
import com.erolc.mrouter.model.WindowOptions

@Composable
expect fun PlatformWindow(
    options: WindowOptions,
    entry: WindowEntry,
    content: @Composable () -> Unit
)

@Composable
expect fun PlatformDialog(
    onDismissRequest: () -> Unit,
    options: DialogOptions,
    content: @Composable () -> Unit
)


sealed interface Platform

data object Android : Platform
data object Ios : Platform
data object Mac : Platform
data object Windows : Platform
data object Linux : Platform
data object Web : Platform
data object UnKnow : Platform

expect fun getPlatform(): Platform


val isMobile: Boolean
    get() {
        val platform = getPlatform()
        return platform == Android || platform == Ios
    }

val isDesktop: Boolean
    get() {
        val platform = getPlatform()
        return platform == Windows || platform == Linux || platform == Mac
    }

val isAndroid = getPlatform() == Android
val isIos = getPlatform() == Ios
