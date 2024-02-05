package com.erolc.lifecycle

interface LifecycleEventObserver:LifecycleObserver {

    fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event)
}