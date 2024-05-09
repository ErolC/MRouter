package com.erolc.mrouter.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry


actual class LifecycleOwnerDelegate : LifecycleOwner {
    private val registry = LifecycleRegistry(this)

    @Composable
    actual fun SystemLifecycle() {
        val lifecycle = LocalLifecycleOwner.current.lifecycle
        DisposableEffect(lifecycle) {
            val observer = LifecycleEventObserver { _, event ->
                handleLifecycleEvent(event)
            }
            lifecycle.addObserver(observer)
            onDispose {
                lifecycle.removeObserver(observer)
            }
        }
    }

    fun handleLifecycleEvent(event: Lifecycle.Event) {
        if (event > Lifecycle.Event.ON_RESUME)
            registry.handleLifecycleEvent(event)
        else if (event.targetState > registry.currentState)
            registry.handleLifecycleEvent(event)
    }


    actual override val lifecycle: Lifecycle
        get() = registry

    actual fun handleLifecycleEvent(event: String) {
        registry.handleLifecycleEvent(withEvent(event))
    }
}