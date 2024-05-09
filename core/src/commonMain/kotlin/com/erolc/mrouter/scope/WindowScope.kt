package com.erolc.mrouter.scope

import androidx.compose.runtime.mutableStateOf
import com.erolc.mrouter.utils.PageCache
import com.erolc.mrouter.window.DefWindowSize

/**
 * 窗口的作用域
 */
class WindowScope(val id: String = "") {
    val windowSize = mutableStateOf(DefWindowSize)

    internal val isCloseWindow = mutableStateOf(false)
    internal lateinit var platformRes: Map<String, Any>

    internal val pageCache = PageCache()
    fun getPlatformRes(key: String) = platformRes[key]

    /**
     * 关闭该window
     */
    fun close() {
        isCloseWindow.value = true
    }

}