package com.erolc.mrouter.backstack.entry

import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.erolc.lifecycle.Lifecycle
import com.erolc.lifecycle.LifecycleOwner
import com.erolc.lifecycle.LifecycleRegistry
import com.erolc.lifecycle.SystemLifecycle
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.route.DialogRouter
import com.erolc.mrouter.route.ExitImpl
import com.erolc.mrouter.route.SysBackPressed
import com.erolc.mrouter.route.transform.*
import com.erolc.mrouter.scope.LifecycleEventListener
import com.erolc.mrouter.scope.LocalPageScope
import com.erolc.mrouter.scope.PageScope
import com.erolc.mrouter.utils.logi
import com.erolc.mrouter.utils.rememberInPage

class PageEntry internal constructor(
    val scope: PageScope,
    address: Address
) : StackEntry(address), LifecycleOwner {
    private val registry: LifecycleRegistry = LifecycleRegistry(this)

    init {
        scope.lifecycle = registry
    }

    private var currentEvent = Lifecycle.Event.ON_ANY

    //transform中的prev在下一个页面打开的时候才会被赋值
    internal val transform = mutableStateOf(Transform.None)
    private var isUpdateTransform = false
    internal val transformState get() = scope.transformState

    private val shouldDestroy = mutableStateOf(false)
    private val shouldResume = mutableStateOf(false)
    internal val isSecond = mutableStateOf(false)
    internal val isExit = mutableStateOf(false)
    private val isIntercept get() = scope.isIntercept

    override val lifecycle: Lifecycle get() = registry


    private val listener = object : LifecycleEventListener {
        override fun call(event: Lifecycle.Event) {
            when {
                event == Lifecycle.Event.ON_RESUME -> resume()
                event == Lifecycle.Event.ON_PAUSE -> pause()
                event == Lifecycle.Event.ON_DESTROY -> destroy()
            }
        }
    }

    init {
        create()
    }

    @Composable
    override fun Content(modifier: Modifier) {
        CompositionLocalProvider(LocalPageScope provides scope) {
            SysBackPressed { scope.backPressed() }
            val inDialog = scope.router.parentRouter is DialogRouter
            if (inDialog) {
                //由于pageEntry除了会以window作为宿主之外，还会以dialog作为宿主
                Box(
                    modifier.clickable(
                        indication = null,
                        interactionSource = MutableInteractionSource(),
                        onClick = {})
                ) {
                    address.content()
                }

            } else
                OnlyPage(modifier)
        }

        lifecycle()

    }

    @Composable
    private fun dialog() {
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
        val isExit by remember(this) { isExit }
        val isIntercept by remember(this) { isIntercept }

        state.targetState = transformState.value

        val transition = rememberTransition(state).apply {
            if (enterStart) transformState.value = Resume
            if (exitFinished) {
                scope.router.parentRouter!!.backStack.pop()
            }
        }
        if (scope.transformTransition == null) scope.transformTransition = transition

        val transform by rememberInPage (this, transform) { transform }
        Box(transition.createModifier(address.path, transform, modifier, "Built-in")) {
            transform.gesture.run {
                remember(this) { setContent(address.content) }
                val pageModifier = pauseModifierPost.getModifier().fillMaxSize()
                Wrap(pageModifier) {
                    transformState.value = when (it) {
                        0f -> Resume
                        1f -> PostExit
                        else -> TransitionState(1 - it)
                    }
                }
                check(isUseContent) { "必须在Wrap方法中使用PageContent,请检查 $this 的Wrap方法" }
            }
            dialog()
        }
        if (isExit && !isIntercept) ExitImpl()
    }


    @Composable
    fun lifecycle() {
        val windowScope = LocalWindowScope.current

        val shouldDestroy by remember(this) { shouldDestroy }
        var shouldResume by remember(this) { shouldResume }
        val isSecond by remember(this) { isSecond }

        SystemLifecycle {
            when {
                it == Lifecycle.Event.ON_RESUME -> resume()
                it == Lifecycle.Event.ON_PAUSE -> pause()
                it == Lifecycle.Event.ON_DESTROY -> destroy()
            }
        }
        DisposableEffect(this, isSecond) {
            if (!isSecond) {
                shouldResume = true
                resume()
                windowScope.addLifecycleEventListener(listener)
            }
            onDispose {
                scope.transformTransition = null
                if (shouldDestroy) {
                    windowScope.removeLifeCycleEventListener(listener)
                    onPause()
                    onDestroy()
                }
            }
        }
    }

    @Composable
    fun shareTransform(entry: PageEntry) {
        val state by remember(this, transformState) {
            transformState
        }
        entry.transformState.value = when (state) {
            PreEnter, PostExit -> Resume
            Resume -> updatePrevTransform(entry)
            PauseState -> PauseState
            else -> Reverse(1 - state.progress)


        }
    }

    /**
     * 更新上一个页面的transform
     */
    private fun updatePrevTransform(prev: PageEntry): TransformState {
        if (prev.isUpdateTransform) return PauseState
        prev.transform.value = prev.transform.value.copy(prevPause = transform.value.prevPause)
        prev.transform.value.gesture.updatePauseModifier(transform.value.gesture.pauseModifierPost)
        prev.isUpdateTransform = true
        return PauseState
    }

    fun onCreate() {
        logi("tag", "$this onCreate ${address.path}")
        handleLifecycleEvent(currentEvent)
    }

    fun onResume() {
        logi("tag", "$this onResume ${address.path}")
        handleLifecycleEvent(currentEvent)

    }

    fun onPause() {
        logi("tag", "$this onPause ${address.path}")
        handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    fun onDestroy() {
        logi("tag", "$this onDestroy ${address.path}")
        handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    private fun create() {
        currentEvent = Lifecycle.Event.ON_CREATE
        onCreate()

    }

    internal fun resume() {
        if (currentEvent.targetState == Lifecycle.State.CREATED && shouldResume.value) {
            currentEvent = Lifecycle.Event.ON_RESUME
            onResume()
        }
    }

    internal fun pause(isSecond: Boolean = false) {
        this.isSecond.value = isSecond
        if (currentEvent.targetState == Lifecycle.State.RESUMED) {
            onPause()
            currentEvent = Lifecycle.Event.ON_PAUSE
        }
    }

    fun handleLifecycleEvent(event: Lifecycle.Event) {
        logi("tag", "$this event:$event")
        registry.handleLifecycleEvent(event)
    }

    override fun destroy() {
        super.destroy()
        shouldDestroy.value = true
    }
}

