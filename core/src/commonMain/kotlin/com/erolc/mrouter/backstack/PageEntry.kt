package com.erolc.mrouter.backstack

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.erolc.lifecycle.Lifecycle
import com.erolc.lifecycle.SystemLifecycle
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.route.DialogRouter
import com.erolc.mrouter.route.SysBackPressed
import com.erolc.mrouter.scope.LocalPageScope
import com.erolc.mrouter.scope.PageScope
import com.erolc.mrouter.utils.loge
import com.erolc.mrouter.utils.logi

class PageEntry internal constructor(
    scope: PageScope,
    address: Address
) : StackEntry(scope, address) {
    private var currentEvent = Lifecycle.Event.ON_ANY

    init {
        create()
    }

    @Composable
    override fun Content(modifier: Modifier) {
        val inDialog = scope.router.parentRouter is DialogRouter
        CompositionLocalProvider(LocalPageScope provides scope) {
            SysBackPressed { scope.backPressed() }
            Box(modifier) {
                address.content()
            }
        }

        Lifecycle()

        scope.router.getBackStack().collectAsState().let {
            val stack by remember { it }
            stack.forEach {
                (it as? DialogEntry)?.Content(modifier)
            }
        }
    }

    private var shouldDestroy = false
    private var shouldResume: Boolean = false


    @Composable
    fun Lifecycle() {
        val windowScope = LocalWindowScope.current
        SystemLifecycle {
            when {
                it == Lifecycle.Event.ON_RESUME -> resume()
                it == Lifecycle.Event.ON_PAUSE -> pause()
                it == Lifecycle.Event.ON_DESTROY -> destroy()
            }
        }
        DisposableEffect(Unit) {
            shouldResume = true
            resume()
            windowScope.lifecycleEvent = {
                when {
                    it == Lifecycle.Event.ON_RESUME -> resume()
                    it == Lifecycle.Event.ON_PAUSE -> pause()
                    it == Lifecycle.Event.ON_DESTROY -> destroy()
                }
            }
            onDispose {
                if (shouldDestroy) {
                    onPause()
                    onDestroy()
                }
            }
        }
    }

    fun onCreate() {
        logi("tag", "$this onCreate $currentEvent")
        handleLifecycleEvent(currentEvent)
    }

    fun onResume() {
        logi("tag", "$this onResume")
        handleLifecycleEvent(currentEvent)

    }

    fun onPause() {
        logi("tag", "$this onPause")
        handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    fun onDestroy() {
        logi("tag", "$this onDestroy")
        handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    private fun create() {
        currentEvent = Lifecycle.Event.ON_CREATE
        onCreate()

    }

    private fun resume() {
        if (currentEvent.targetState == Lifecycle.State.CREATED && shouldResume) {
            currentEvent = Lifecycle.Event.ON_RESUME
            onResume()
        }
    }

    private fun pause() {
        if (currentEvent.targetState == Lifecycle.State.RESUMED) {
            onPause()
            currentEvent = Lifecycle.Event.ON_PAUSE
        }
    }


    override fun destroy() {
        super.destroy()
        shouldDestroy = true
    }
}

