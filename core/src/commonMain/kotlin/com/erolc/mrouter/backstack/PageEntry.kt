package com.erolc.mrouter.backstack

import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.erolc.lifecycle.Lifecycle
import com.erolc.lifecycle.SystemLifecycle
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.route.DialogRouter
import com.erolc.mrouter.route.SysBackPressed
import com.erolc.mrouter.route.transform.*
import com.erolc.mrouter.route.transform.TransitionState
import com.erolc.mrouter.scope.LocalPageScope
import com.erolc.mrouter.scope.PageScope
import com.erolc.mrouter.utils.*

class PageEntry internal constructor(
    scope: PageScope,
    address: Address
) : StackEntry(scope, address) {
    private var currentEvent = Lifecycle.Event.ON_ANY

    //transform中的prev在下一个页面打开的时候才会被赋值
    internal val transform = mutableStateOf(Transform.None)
    internal val transformState get() = scope.transformState

    private var shouldDestroy = false
    private var shouldResume: Boolean = false

    init {
        create()
    }

    @Composable
    override fun Content(modifier: Modifier) {
        CompositionLocalProvider(LocalPageScope provides scope) {
            SysBackPressed { scope.backPressed() }
            val inDialog = scope.router.parentRouter is DialogRouter
            if (inDialog)
                Box(modifier) {
                    address.content()
                }
            else
                OnlyPage(modifier)
        }

        Lifecycle()

        scope.router.getBackStack().collectAsState().let {
            val stack by remember { it }
            stack.forEach {
                (it as? DialogEntry)?.Content(Modifier)
            }
        }
    }


    @OptIn(ExperimentalTransitionApi::class)
    @Composable
    fun OnlyPage(modifier: Modifier) {
        val state = remember(this) {
            MutableTransitionState(transformState.value)
        }
        state.targetState = transformState.value

        val transition = rememberTransition(state).apply {
            if (enterStart) transformState.value = Resume
            if (exitFinished) scope.router.parentRouter!!.backStack.pop()
        }
        if (scope.transformTransition == null) scope.transformTransition = transition

        val transform by remember(this, transform) { transform }
        Box(transition.createModifier(address.path, transform, modifier, "Built-in")) {
            transform.gesture.run {
                val pageModifier = pauseModifierPost.getModifier().fillMaxSize()
                Wrap(pageModifier) {

                }
                check(isUseContent) { "必须在Wrap方法中使用content,请检查$this 的Wrap方法" }
            }
        }
    }


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

    @Composable
    fun ShareLife(entry: PageEntry) {
        val state by remember(this, transformState) {
            transformState
        }
        entry.transformState.value = when (state) {
            PreEnter, PostExit -> Resume
            Resume -> {
                entry.transform.value = entry.transform.value.copy(prevPause = transform.value.prevPause)
                entry.transform.value.gesture.updatePauseModifier(transform.value.gesture.pauseModifierPost)
                PauseState
            }

            PauseState -> PauseState
            is TransitionState -> TransitionState(1 - state.progress)
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

    internal fun resume() {
        if (currentEvent.targetState == Lifecycle.State.CREATED && shouldResume) {
            currentEvent = Lifecycle.Event.ON_RESUME
            onResume()
        }
    }

    internal fun pause() {
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

