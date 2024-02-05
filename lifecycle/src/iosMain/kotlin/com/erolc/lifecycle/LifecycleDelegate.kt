package com.erolc.lifecycle

import androidx.compose.runtime.staticCompositionLocalOf


/**
 * @author erolc
 * @since 2024/2/4 16:46
 */
@SinceKotlin("1.0")
class LifecycleDelegate private constructor() {

    private var call: ((Lifecycle.Event) -> Unit)? = null
    fun onCreate() = call?.invoke(Lifecycle.Event.ON_CREATE)
    fun onResume() = call?.invoke(Lifecycle.Event.ON_RESUME)
    fun onPause() = call?.invoke(Lifecycle.Event.ON_PAUSE)
    fun onDestroy() = call?.invoke(Lifecycle.Event.ON_DESTROY)
    internal fun onCall(call: (Lifecycle.Event) -> Unit) {
        this.call = call
    }

    companion object {
        internal val lifecycleDelegate = LifecycleDelegate()
    }
}

val localLifecycleDelegate =
    staticCompositionLocalOf { LifecycleDelegate.lifecycleDelegate }
