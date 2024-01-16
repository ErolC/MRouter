package com.erolc.mrouter.backstack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import com.erolc.mrouter.lifecycle.Lifecycle
import com.erolc.mrouter.lifecycle.LifecycleOwner
import com.erolc.mrouter.lifecycle.LifecycleRegistry
import com.erolc.mrouter.lifecycle.SystemLifecycle
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.route.SysBackPressed
import com.erolc.mrouter.scope.GroupScope
import com.erolc.mrouter.scope.LocalPageScope
import com.erolc.mrouter.scope.PageScope
import com.erolc.mrouter.utils.loge
import kotlin.math.abs


/**
 * 后退栈的一个条目。
 */
sealed class StackEntry(val scope: PageScope, val address: Address) : LifecycleOwner {
    private val lifeEvent = mutableStateOf(Lifecycle.Event.ON_ANY)
    private var state = Lifecycle.State.INITIALIZED
    val registry: LifecycleRegistry = LifecycleRegistry(this)

    init {
        scope.lifecycle = registry
    }

    override val lifecycle: Lifecycle
        get() = registry

    //是否应该停止，如果不应该停止，那么事件将会停留在ON_PAUSE
    private var shouldStop = true
    private var shouldDestroy = false

    @Composable
    open fun Content() {
        CompositionLocalProvider(LocalPageScope provides scope) {
            var event by remember { lifeEvent }
            val windowEvent by remember { scope.windowScope.lifecycleEvent }
            when (windowEvent) {
                Lifecycle.Event.ON_STOP -> {
                    if (state == Lifecycle.State.RESUMED) {
                        shouldStop = true
                        event = windowEvent
                    }
                }

                Lifecycle.Event.ON_RESUME -> if (state == Lifecycle.State.STARTED) event =
                    windowEvent


                Lifecycle.Event.ON_DESTROY -> shouldDestroy = true
                else -> {}
            }

            SystemLifecycle {
                if (it == Lifecycle.Event.ON_DESTROY)
                    shouldDestroy = true

                //当targetState比state大1时才触发
                if (abs(it.targetState.compareTo(state)) == 1) {
                    if (it == Lifecycle.Event.ON_STOP)
                        shouldStop = true
                    event = it
                }

//                when (it) {
//                    Lifecycle.Event.ON_CREATE -> {
//                        if (state == Lifecycle.State.INITIALIZED) event = it
//                    }
//
//                    Lifecycle.Event.ON_START -> {
//                        if (state == Lifecycle.State.CREATED) event = it
//                    }
//
//                    Lifecycle.Event.ON_RESUME -> {
//                        if (state == Lifecycle.State.STARTED) event = it
//                    }
//
//                    Lifecycle.Event.ON_PAUSE -> {
//                        if (state == Lifecycle.State.RESUMED) event = it
//                    }
//
//                    Lifecycle.Event.ON_STOP -> {
//                        shouldStop = true
//                        if (state == Lifecycle.State.STARTED) event = it
//                    }
//
//                    Lifecycle.Event.ON_DESTROY -> {
//                        shouldDestroy = true
//                    }
//
//                    else -> {}
//                }

            }
            SysBackPressed { scope.backPressed() }

            if (event != Lifecycle.Event.ON_ANY) dispatcher(event)

            DisposableEffect(scope, address) {
                Lifecycle.Event.upFrom(state)?.let {
                    lifeEvent.value = it
                    state = it.targetState
                }
                onDispose {
                    val wEvent = scope.windowScope.lifecycleEvent.value
                    if (wEvent == Lifecycle.Event.ON_DESTROY) shouldDestroy = true
                    event = Lifecycle.Event.ON_PAUSE
                    negativeDispatcher(event)
                }
            }
            address.content()
        }
    }

    private fun upFrom(event: Lifecycle.Event) {
        state = event.targetState
        lifeEvent.value = Lifecycle.Event.upFrom(state) ?: Lifecycle.Event.ON_ANY
    }

    private fun downFrom(event: Lifecycle.Event) {
        state = event.targetState
        if ((event == Lifecycle.Event.ON_PAUSE && shouldStop) || (event == Lifecycle.Event.ON_STOP && shouldDestroy)) Lifecycle.Event.downFrom(
            state
        )?.let {
            lifeEvent.value = it
            negativeDispatcher(it)
        }

        state = Lifecycle.State.CREATED
    }

    private fun dispatcher(event: Lifecycle.Event) {
        if (event in arrayOf(
                Lifecycle.Event.ON_CREATE, Lifecycle.Event.ON_START, Lifecycle.Event.ON_RESUME
            )
        ) {
            handleLifecycleEvent(event)
            upFrom(event)
        }
    }

    private fun negativeDispatcher(event: Lifecycle.Event) {
        if (event in listOf(
                Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP, Lifecycle.Event.ON_DESTROY
            )
        ) {
            handleLifecycleEvent(event)
            downFrom(event)

        }
    }

    open fun handleLifecycleEvent(event: Lifecycle.Event) {
        loge("tag", "${this::class.simpleName} event:$event")
        registry.handleLifecycleEvent(event)
    }

    /**
     * 负面,根据当前的状态得到负面的事件
     */
    private fun negative() {
        Lifecycle.Event.downFrom(state)?.let {
            lifeEvent.value = it
        }
    }

    internal fun pause() {
        shouldStop = false
    }

    internal fun stop() {
        shouldStop = true
    }

    internal fun destroy() {
        shouldDestroy = true
    }
}

class DialogEntry internal constructor(scope: PageScope, private val entry: PageEntry) :
    StackEntry(scope, entry.apply { this.scope.parentScope = scope }.address) {

    @Composable
    override fun Content() {
        Dialog(onDismissRequest = {}) {
            entry.Content()
        }
    }
}

class PageEntry internal constructor(scope: PageScope, address: Address) :
    StackEntry(scope, address) {
    lateinit var windowEntry: WindowEntry
    override val lifecycle: Lifecycle
        get() = registry
}


class GroupEntry internal constructor(scope: GroupScope, address: Address) :
    StackEntry(scope, address) {

    override val lifecycle: Lifecycle
        get() = registry

    private val stacks = mutableListOf<BackStack>()

    @Composable
    internal fun getBackStack(key: String) = BackStack(key).apply {
        stacks.add(this)
    }.backStack.collectAsState()


    fun addEntry(key: String, entry: StackEntry) {
        stacks.find { it.name == key }?.addEntry(entry)
    }

    @Composable
    override fun Content() {

    }

}

