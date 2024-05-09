package com.erolc.mrouter.lifecycle

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner


fun Lifecycle.addEventObserver(body: (source: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
    addObserver(LifecycleEventObserver { source, event ->
        body(source, event)
    })
}

fun withEvent(event: String): Lifecycle.Event {
    return when (event) {
        "ON_CREATE" -> Lifecycle.Event.ON_CREATE
        "ON_START" -> Lifecycle.Event.ON_START
        "ON_RESUME" -> Lifecycle.Event.ON_RESUME
        "ON_PAUSE" -> Lifecycle.Event.ON_PAUSE
        "ON_STOP" -> Lifecycle.Event.ON_STOP
        "ON_DESTROY" -> Lifecycle.Event.ON_DESTROY
        else -> Lifecycle.Event.ON_ANY
    }
}

fun withEventStr(event: Lifecycle.Event): String {
    return when (event) {
        Lifecycle.Event.ON_CREATE -> "ON_CREATE"
        Lifecycle.Event.ON_START -> "ON_START"
        Lifecycle.Event.ON_RESUME -> "ON_RESUME"
        Lifecycle.Event.ON_PAUSE -> "ON_PAUSE"
        Lifecycle.Event.ON_STOP -> "ON_STOP"
        Lifecycle.Event.ON_DESTROY -> "ON_DESTROY"
        else -> "ON_ANY"
    }
}

expect class LifecycleOwnerDelegate() : LifecycleOwner {

    override val lifecycle: Lifecycle
    @Composable
    fun SystemLifecycle()

    fun handleLifecycleEvent(event: String)
}