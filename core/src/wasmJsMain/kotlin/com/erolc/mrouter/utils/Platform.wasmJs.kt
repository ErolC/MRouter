package com.erolc.mrouter.utils

import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.erolc.mrouter.backstack.entry.LocalWindowScope
import com.erolc.mrouter.backstack.entry.WindowEntry
import com.erolc.mrouter.model.WindowOptions
import com.erolc.mrouter.window.WindowSize
import kotlinx.browser.window
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.random.Random

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


internal class WeakReference<T : Any> (
    private var reference: T?
) {
    fun get(): T? = reference

    fun clear() {
        reference = null
    }
}

