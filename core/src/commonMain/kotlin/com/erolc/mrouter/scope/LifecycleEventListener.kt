package com.erolc.mrouter.scope

import com.erolc.lifecycle.Lifecycle


internal interface LifecycleEventListener {

    fun call(event: Lifecycle.Event)
}