package com.erolc.mrouter.scope

import androidx.compose.runtime.mutableStateOf
import com.erolc.mrouter.lifecycle.Lifecycle
import com.erolc.mrouter.window.DefWindowSize

class WindowScope : PageScope() {
    val windowSize = mutableStateOf(DefWindowSize)

    var lifecycleEvent: ((Lifecycle.Event) -> Unit)? = null
    internal fun onLifeEvent(event: Lifecycle.Event) {
        lifecycleEvent?.invoke(event)
    }

}