package com.erolc.mrouter.lifecycle

class DefaultLifeCycleEventObserver(private val observer: LifecycleObserver) :
    LifecycleEventObserver {

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (observer is LifecycleEventObserver) observer.onStateChanged(source, event)
    }
}