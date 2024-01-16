package com.erolc.mrouter.lifecycle

interface LifecycleEventObserver:LifecycleObserver {

    fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event)
}