package com.erolc.mrouter.platform


import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.erolc.mrouter.backstack.entry.WindowEntry
import com.erolc.mrouter.model.WindowOptions
import com.erolc.mrouter.window.WindowSize
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.UIKit.UIScreen
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.random.Random

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


@OptIn(ExperimentalStdlibApi::class)
fun randomUUID(): String {
    val bytes = Random.nextBytes(16).also {
        it[6] = it[6] and 0x0f // clear version
        it[6] = it[6] or 0x40 // set to version 4
        it[8] = it[8] and 0x3f // clear variant
        it[8] = it[8] or 0x80.toByte() // set to IETF variant
    }
    return StringBuilder(36)
        .append(bytes.toHexString(0, 4))
        .append('-')
        .append(bytes.toHexString(4, 6))
        .append('-')
        .append(bytes.toHexString(6, 8))
        .append('-')
        .append(bytes.toHexString(8, 10))
        .append('-')
        .append(bytes.toHexString(10))
        .toString()
}

