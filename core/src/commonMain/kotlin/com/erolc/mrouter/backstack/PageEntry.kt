package com.erolc.mrouter.backstack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.erolc.mrouter.lifecycle.Lifecycle
import com.erolc.mrouter.lifecycle.SystemLifecycle
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.scope.PageScope
import com.erolc.mrouter.utils.loge

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
        super.Content(modifier)
        Lifecycle()
        scope.router.getBackStack().collectAsState().let {
            val stack by remember { it }
            stack.forEach {
                it.Content(modifier)
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
        loge("tag", "$this onCreate $currentEvent")
        handleLifecycleEvent(currentEvent)
    }

    fun onResume() {
        loge("tag", "$this onResume")
        handleLifecycleEvent(currentEvent)

    }

    fun onPause() {
        loge("tag", "$this onPause")
        handleLifecycleEvent(currentEvent)

    }

    fun onDestroy() {
        loge("tag", "$this onDestroy")
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
            currentEvent = Lifecycle.Event.ON_PAUSE
            onPause()
        }
    }


    override fun destroy() {
        super.destroy()
        shouldDestroy = true
    }
}

