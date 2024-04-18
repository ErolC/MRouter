package com.erolc.mrouter.scope

import androidx.compose.runtime.mutableStateOf
import com.erolc.lifecycle.Lifecycle
import com.erolc.mrouter.utils.PageCache
import com.erolc.mrouter.window.DefWindowSize

/**
 * 窗口的作用域
 */
class WindowScope(val id: String = "") {
    val windowSize = mutableStateOf(DefWindowSize)
    private val listeners = mutableSetOf<LifecycleEventListener>()

    internal val isCloseWindow = mutableStateOf(false)
    internal lateinit var platformRes: Map<String, Any>

    internal val pageCache = PageCache()

    internal fun onLifeEvent(event: Lifecycle.Event) {
        listeners.forEach { it.call(event) }
    }

    internal fun addLifecycleEventListener(listener: LifecycleEventListener) {
        listeners.add(listener)
    }

    internal fun removeLifeCycleEventListener(listener: LifecycleEventListener) {
        listeners.remove(listener)
    }

    fun getPlatformRes(key: String) = platformRes[key]

    /**
     * 关闭该window
     */
    fun close() {
        onLifeEvent(Lifecycle.Event.ON_DESTROY)
        isCloseWindow.value = true
    }

}