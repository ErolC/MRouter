package com.erolc.mrouter.scope

import androidx.compose.runtime.mutableStateOf
import com.erolc.lifecycle.Lifecycle
import com.erolc.mrouter.window.DefWindowSize

class WindowScope : PageScope() {
    val windowSize = mutableStateOf(DefWindowSize)

    var lifecycleEvent: ((Lifecycle.Event) -> Unit)? = null

    internal val isCloseWindow = mutableStateOf(false)

    internal fun onLifeEvent(event: Lifecycle.Event) {
        lifecycleEvent?.invoke(event)
    }

    /**
     * 关闭该window
     */
    fun close() {
        isCloseWindow.value = true
    }

}