package com.erolc.lifecycle

class DefaultLifeCycleEventObserver(private val observer: LifecycleObserver) :
    LifecycleEventObserver {

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (observer is LifecycleEventObserver) observer.onStateChanged(source, event)
    }
}