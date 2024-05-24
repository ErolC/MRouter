package com.erolc.mrouter.platform

import androidx.compose.runtime.Composable
import com.erolc.mrouter.backstack.entry.WindowEntry
import com.erolc.mrouter.model.WindowOptions

/**
 * 平台window
 */
@Composable
expect fun PlatformWindow(
    options: WindowOptions,
    entry: WindowEntry,
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

expect fun safeAreaInsetsTop():Float

//判断ios是否有刘海
val iosHasNotch get() = isIos && safeAreaInsetsTop() > 20

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
