package com.erolc.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

@Composable
actual fun SystemLifecycle(call: (Lifecycle.Event) -> Unit) {
    val owner = LocalLifecycleOwner.current
    //这里必须加remember以保证observe只添加一次。
    remember(owner) {
        owner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(
                source: LifecycleOwner, event: androidx.lifecycle.Lifecycle.Event
            ) {
                when (event) {
                    androidx.lifecycle.Lifecycle.Event.ON_CREATE -> Lifecycle.Event.ON_CREATE
                    androidx.lifecycle.Lifecycle.Event.ON_RESUME -> Lifecycle.Event.ON_RESUME
                    androidx.lifecycle.Lifecycle.Event.ON_PAUSE -> Lifecycle.Event.ON_PAUSE
                    androidx.lifecycle.Lifecycle.Event.ON_DESTROY -> Lifecycle.Event.ON_DESTROY
                    androidx.lifecycle.Lifecycle.Event.ON_ANY -> Lifecycle.Event.ON_ANY
                    else -> null
                }?.let(call)
            }

        })
        ""
    }
}