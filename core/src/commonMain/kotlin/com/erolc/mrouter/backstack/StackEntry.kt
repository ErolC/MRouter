package com.erolc.mrouter.backstack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.erolc.lifecycle.Lifecycle
import com.erolc.lifecycle.LifecycleOwner
import com.erolc.lifecycle.LifecycleRegistry
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.route.SysBackPressed
import com.erolc.mrouter.scope.LocalPageScope
import com.erolc.mrouter.scope.PageScope
import com.erolc.mrouter.utils.logi


/**
 * 后退栈的一个条目。
 */
sealed class StackEntry(val scope: PageScope, val address: Address) : LifecycleOwner {
   private val registry: LifecycleRegistry = LifecycleRegistry(this)
    init {
        scope.lifecycle = registry
    }

    override val lifecycle: Lifecycle get() = registry

    @Composable
    open fun Content(modifier: Modifier = Modifier) {}


    open fun handleLifecycleEvent(event: Lifecycle.Event) {
        logi("tag", "$this event:$event")
        registry.handleLifecycleEvent(event)
    }

    open fun destroy() {}
}
