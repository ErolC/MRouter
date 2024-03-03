package com.erolc.mrouter.scope

import androidx.compose.runtime.mutableStateOf
import com.erolc.lifecycle.Lifecycle
import com.erolc.mrouter.utils.loge
import com.erolc.mrouter.window.DefWindowSize

class WindowScope : PageScope() {
    val windowSize = mutableStateOf(DefWindowSize)
    private val listeners = mutableSetOf<LifecycleEventListener>()

    internal val isCloseWindow = mutableStateOf(false)

    internal fun onLifeEvent(event: Lifecycle.Event) {
        listeners.forEach { it.call(event) }
    }

    internal fun addLifecycleEventListener(listener: LifecycleEventListener) {
        listeners.add(listener)
    }

    internal fun removeLifeCycleEventListener(listener: LifecycleEventListener) {
        listeners.remove(listener)
    }

    /**
     * 关闭该window
     */
    fun close() {
        onLifeEvent(Lifecycle.Event.ON_DESTROY)
        isCloseWindow.value = true
    }

}