package com.erolc.mrouter.scope

import androidx.compose.runtime.mutableStateOf
import com.erolc.mrouter.route.ResourcePool
import com.erolc.mrouter.utils.PageCache
import com.erolc.mrouter.window.DefWindowSize
import com.erolc.mrouter.window.HostSize

/**
 * 窗口的作用域
 */
class WindowScope(val id: String = "") : HostScope() {
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

    override fun setHostSize(size: HostSize) {
    }

    fun setWindowHostSize(size: HostSize) {
        super.setHostSize(size)
    }
}