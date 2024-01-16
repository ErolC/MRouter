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
    internal val lifecycleEvent = mutableStateOf(Lifecycle.Event.ON_ANY)

//    internal val toastOptions = mutableStateOf<ToastOptions?>(null)
//
//    fun toast(msg: String, duration: Duration = 1.seconds) {
//        toastOptions.value = ToastOptions(msg, duration)
//    }
}