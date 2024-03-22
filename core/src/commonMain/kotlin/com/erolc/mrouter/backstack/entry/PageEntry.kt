package com.erolc.mrouter.backstack.entry

import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.erolc.lifecycle.Lifecycle
import com.erolc.lifecycle.LifecycleOwner
import com.erolc.lifecycle.LifecycleRegistry
import com.erolc.lifecycle.SystemLifecycle
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.route.ExitImpl
import com.erolc.mrouter.route.NormalFlag
import com.erolc.mrouter.route.RouteFlag
import com.erolc.mrouter.route.SysBackPressed
import com.erolc.mrouter.route.router.EmptyRouter
import com.erolc.mrouter.route.router.PageRouter
import com.erolc.mrouter.route.transform.*
import com.erolc.mrouter.scope.LifecycleEventListener
import com.erolc.mrouter.scope.LocalPageScope
import com.erolc.mrouter.scope.PageScope
import com.erolc.mrouter.utils.loge
import com.erolc.mrouter.utils.logi
import com.erolc.mrouter.utils.rememberInPage

open class PageEntry internal constructor(
    val scope: PageScope,
    override val address: Address
) : StackEntry, LifecycleOwner {
    private val registry: LifecycleRegistry = LifecycleRegistry(this)

    init {
        scope.lifecycle = registry
    }

    //页面当前的事件
    private var currentEvent = Lifecycle.Event.ON_ANY

    //transform中的prev在下一个页面打开的时候才会被赋值
    internal val transform = mutableStateOf(Transform.None)

    //是否已更新transform，避免二次更新
    private var isUpdateTransform = false

    //路由到当前界面的标识，需要在路由完成时（当前界面完全展示时）执行相应的操作
    internal var flag: RouteFlag = NormalFlag

    // transform的状态
    internal val transformState get() = scope.transformState

    //是否销毁
    private val isDestroy = mutableStateOf(false)
    internal val shouldResume = mutableStateOf(true)

    //是否需要退出
    internal val isExit = mutableStateOf(false)

    //是否拦截
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
            Page(modifier)
        }

        lifecycle()
    }


    @OptIn(ExperimentalTransitionApi::class)
    @Composable
    private fun Page(modifier: Modifier) {
        val state = remember(this) {
            MutableTransitionState(transformState.value)
        }
        var isExit by remember(this) { isExit }
        val isIntercept by remember(this) { isIntercept }

        state.targetState = transformState.value

        val transition = rememberTransition(state).apply {
            if (enterStart) transformState.value = Resume
            if (exitFinished
                && !(scope.router.parentRouter as PageRouter).backStack.pop()
            ) isExit = true

            if (resume) {
                (scope.router.parentRouter as PageRouter).backStack.execute(flag)
                flag = NormalFlag
            }
        }
        if (scope.transformTransition == null) scope.transformTransition = transition

        val transform by rememberInPage(this, transform) { transform }
        Box(transition.createModifier(transform, modifier, "Built-in")) {
            transform.gesture.run {
                remember(this) {
                    setContent(RealContent())
                }
                val pageModifier = gestureModifier.getModifier().fillMaxSize()
                Wrap(pageModifier) {
                    transformState.value = when (it) {
                        0f -> Resume
                        1f -> PostExit
                        else -> TransitionState(1 - it)
                    }
                }
                check(isUseContent) { "必须在Wrap方法中使用PageContent,请检查 $this 的Wrap方法" }
            }
        }
        if (isExit && !isIntercept && scope.router !is EmptyRouter) ExitImpl()
    }

    open fun RealContent(): @Composable () -> Unit {
        return address.content
    }

    @Composable
    fun lifecycle() {
        val windowScope = LocalWindowScope.current

        val shouldDestroy by remember(this) { isDestroy }

        SystemLifecycle {
            when {
                it == Lifecycle.Event.ON_RESUME -> resume()
                it == Lifecycle.Event.ON_PAUSE -> pause()
                it == Lifecycle.Event.ON_DESTROY -> destroy()
            }
        }
        resume()
        DisposableEffect(this) {
            windowScope.addLifecycleEventListener(listener)
            onDispose {
                scope.transformTransition = null
                if (shouldDestroy) {
                    windowScope.removeLifeCycleEventListener(listener)
                    pause()
                    onDestroy()
                }
            }
        }
    }

    @Composable
    fun shareTransform(entry: PageEntry) {
        val state by remember(this) {
            transformState
        }
        entry.transformState.value = when (state) {
            PreEnter -> Resume
            PostExit -> {
                entry.run {
                    transform.value.gesture.releasePauseModifier()
                    isUpdateTransform = false
                }
                Resume
            }

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


    private fun create() {
        currentEvent = Lifecycle.Event.ON_CREATE
        handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    private fun resume() {
        if (currentEvent.targetState == Lifecycle.State.CREATED && shouldResume.value) {
            shouldResume.value = false
            currentEvent = Lifecycle.Event.ON_RESUME
            handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
    }

    internal fun pause() {
        if (currentEvent.targetState == Lifecycle.State.RESUMED) {
            handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            currentEvent = Lifecycle.Event.ON_PAUSE
        }
    }

    private fun onDestroy() {
        handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    private fun handleLifecycleEvent(event: Lifecycle.Event) {
        logi("tag", "$this - $event - ${address.path}")
        registry.handleLifecycleEvent(event)
    }

    override fun destroy() {
        isDestroy.value = true
    }
}

