package com.erolc.mrouter.backstack.entry

import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.erolc.lifecycle.Lifecycle
import com.erolc.lifecycle.LifecycleRegistry
import com.erolc.lifecycle.SystemLifecycle
import com.erolc.mrouter.isLocalPanelEntry
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.route.ExitImpl
import com.erolc.mrouter.route.NormalFlag
import com.erolc.mrouter.route.RouteFlag
import com.erolc.mrouter.route.SysBackPressed
import com.erolc.mrouter.route.router.EmptyRouter
import com.erolc.mrouter.route.router.PageRouter
import com.erolc.mrouter.route.router.PanelRouter
import com.erolc.mrouter.route.shareele.ShareEleController
import com.erolc.mrouter.route.transform.*
import com.erolc.mrouter.scope.LifecycleEventListener
import com.erolc.mrouter.scope.LocalPageScope
import com.erolc.mrouter.scope.PageScope
import com.erolc.mrouter.utils.rememberPrivateInPage

/**
 * 页面载体，承载着页面的生命周期，代表一个页面。
 */
open class PageEntry internal constructor(
    val scope: PageScope,
    override val address: Address
) : StackEntry {
    private val registry: LifecycleRegistry = LifecycleRegistry(scope)

    init {
        scope.lifecycle = registry
    }

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

    //是否需要退出
    internal val isExit = mutableStateOf(false)

    //是否拦截
    private val isIntercept get() = scope.isIntercept

    // 管理当前页面的路由器
    private val pageRouter: PageRouter
        get() {
            val router = scope.router
            return if (router is PanelRouter) router.parentRouter else router.parentRouter as PageRouter
        }

    //生命周期事件监听器
    private val listener = object : LifecycleEventListener {
        override fun call(event: Lifecycle.Event) {
            onEventCall(event)
        }
    }

    // 生命周期事件处理
    private fun onEventCall(event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_START -> start()
            Lifecycle.Event.ON_RESUME -> resume()
            Lifecycle.Event.ON_PAUSE -> pause()
            Lifecycle.Event.ON_STOP -> stop()
            Lifecycle.Event.ON_DESTROY -> destroy()
            else -> {}
        }
    }

    @Composable
    override fun Content(modifier: Modifier) {
        CompositionLocalProvider(LocalPageScope provides scope) {
            SysBackPressed { scope.backPressed() }
            Page(modifier)

            val windowScope = LocalWindowScope.current
            SystemLifecycle(::onEventCall)
            val state by rememberPrivateInPage("page_transform_state", transformState) { transformState }
            if (state == Resume)
                start()

            DisposableEffect(this) {
                if (scope.router is PanelRouter)
                    windowScope.addLifecycleEventListener(listener)
                onDispose {
                    scope.transformTransition = null
                    if (isDestroy.value) {
                        ShareEleController.afterShare(this@PageEntry)
                        if (scope.router is PanelRouter)
                            windowScope.removeLifeCycleEventListener(listener)
                        downFromEvent(Lifecycle.State.CREATED)
                    }
                }
            }
        }

    }

    @OptIn(ExperimentalTransitionApi::class)
    @Composable
    private fun Page(modifier: Modifier) {
        val state = rememberPrivateInPage("page_state") {
            MutableTransitionState(transformState.value)
        }
        var isExit by rememberPrivateInPage("page_exit") { isExit }
        val isIntercept by rememberPrivateInPage("page_intercept") { isIntercept }


        val transition = rememberTransition(state).apply {
            if (enterStart) transformState.value = Resume
            if (exitFinished && !pageRouter.backStack.pop()) isExit = true
            if (resume) onResume()
            if (pause) {
                pause()
                stop()
            }
        }
        if (scope.transformTransition == null) scope.transformTransition = transition

        val transform by rememberPrivateInPage("page_transform", this, transform) { transform }
        Box(transition.createModifier(transform, modifier, "Built-in")) {
            transform.gesture.run {
                rememberPrivateInPage("page_content") {
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
        state.targetState = transformState.value
        if (isExit && !isIntercept && scope.router !is EmptyRouter) ExitImpl()
    }

    @Composable
    private fun onResume() {
        pageRouter.backStack.execute(flag)
        flag = NormalFlag
        isUpdateTransform = false
        resume()
    }


    open fun RealContent(): @Composable () -> Unit {
        return address.content
    }

    /**
     * 和上一个页面共享同一个变换过程
     */
    @Composable
    internal fun shareTransform(entry: PageEntry) {
        val state by rememberPrivateInPage("page_share_transform_state", transformState) {
            transformState
        }
        entry.transformState.value = when (state) {
            PreEnter -> Resume
            PostExit -> {
                transform.value.gesture.releasePauseModifier()
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

    private fun upFromEvent(state: Lifecycle.State) {
        if (registry.currentState == state) Lifecycle.Event.upFrom(state)?.let { handleLifecycleEvent(it) }
    }

    private fun downFromEvent(state: Lifecycle.State) {
        if (registry.currentState == state) Lifecycle.Event.downFrom(state)?.let { handleLifecycleEvent(it) }
    }

    internal fun create() = upFromEvent(Lifecycle.State.INITIALIZED)
    internal fun start() = upFromEvent(Lifecycle.State.CREATED)
    internal fun resume() = upFromEvent(Lifecycle.State.STARTED)
    internal fun pause() = downFromEvent(Lifecycle.State.RESUMED)
    internal fun stop() = downFromEvent(Lifecycle.State.STARTED)
    override fun destroy() {
        isDestroy.value = true
    }


    internal open fun handleLifecycleEvent(event: Lifecycle.Event) {
        registry.handleLifecycleEvent(event)
        if (event != Lifecycle.Event.ON_CREATE)
            (scope.router as? PanelRouter)?.handleLifecycleEvent(event)
    }


}

