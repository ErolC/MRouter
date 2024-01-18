package com.erolc.mrouter.scope

import androidx.compose.runtime.mutableStateOf
import com.erolc.mrouter.dialog.toast.ToastOptions
import com.erolc.mrouter.lifecycle.Lifecycle
import com.erolc.mrouter.window.DefWindowSize
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class WindowScope : PageScope() {
    val windowSize = mutableStateOf(DefWindowSize)

    var lifecycleEvent: ((Lifecycle.Event) -> Unit)? = null
    internal fun onLifeEvent(event: Lifecycle.Event) {
        lifecycleEvent?.invoke(event)
    }

//    internal val toastOptions = mutableStateOf<ToastOptions?>(null)
//
//    fun toast(msg: String, duration: Duration = 1.seconds) {
//        toastOptions.value = ToastOptions(msg, duration)
//    }
}