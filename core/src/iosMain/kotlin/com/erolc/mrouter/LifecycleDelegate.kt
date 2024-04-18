package com.erolc.mrouter

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import com.erolc.lifecycle.Lifecycle


/**
 * @author erolc
 * @since 2024/2/4 16:46
 */
@SinceKotlin("1.0")
class LifecycleDelegate private constructor() {

    private var call: ((Lifecycle.Event) -> Unit)? = null
    fun onCreate() = call?.invoke(Lifecycle.Event.ON_CREATE)
    fun onStart() = call?.invoke(Lifecycle.Event.ON_START)
    fun onResume() = call?.invoke(Lifecycle.Event.ON_RESUME)
    fun onPause() = call?.invoke(Lifecycle.Event.ON_PAUSE)

    fun onStop() = call?.invoke(Lifecycle.Event.ON_STOP)
    fun onDestroy() = call?.invoke(Lifecycle.Event.ON_DESTROY)
    internal fun onCall(call: (Lifecycle.Event) -> Unit) {
        this.call = call
    }

    companion object {
        val lifecycleDelegate = LifecycleDelegate()
    }
}

val LocalLifecycleDelegate: ProvidableCompositionLocal<LifecycleDelegate> =
    staticCompositionLocalOf { LifecycleDelegate.lifecycleDelegate }
