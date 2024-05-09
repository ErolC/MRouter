package com.erolc.mrouter.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

actual class LifecycleOwnerDelegate actual constructor() : LifecycleOwner {
    private val registry = LifecycleRegistry(this)
    actual override val lifecycle: Lifecycle
        get() = registry

    @Composable
    actual fun SystemLifecycle() {
        LocalLifecycleOwner.current.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                handleLifecycleEvent(event)
            }
        })
    }

    fun handleLifecycleEvent(event: Lifecycle.Event) {
        if (event > Lifecycle.Event.ON_RESUME)
            registry.handleLifecycleEvent(event)
        else if (event.targetState > registry.currentState)
            registry.handleLifecycleEvent(event)
    }

    actual fun handleLifecycleEvent(event: String) {
        registry.handleLifecycleEvent(withEvent(event))
    }
}