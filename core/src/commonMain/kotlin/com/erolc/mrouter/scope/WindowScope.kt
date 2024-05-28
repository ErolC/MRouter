package com.erolc.mrouter.scope

import androidx.compose.runtime.mutableStateOf
import com.erolc.mrouter.route.ResourcePool
import com.erolc.mrouter.utils.PageCache
import com.erolc.mrouter.window.DefWindowSize

/**
 * 窗口的作用域
 */
class WindowScope(val id: String = "") {
    val windowSize = mutableStateOf(DefWindowSize)

    internal val isCloseWindow = mutableStateOf(false)

    internal val pageCache = PageCache()
    fun getPlatformRes(key: String) = ResourcePool.getPlatformRes()[key]

    /**
     * 关闭该window
     */
    fun close() {
        isCloseWindow.value = true
    }

}