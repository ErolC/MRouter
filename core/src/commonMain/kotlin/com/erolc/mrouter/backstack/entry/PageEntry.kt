package com.erolc.mrouter.backstack.entry

import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import com.erolc.lifecycle.Lifecycle
import com.erolc.lifecycle.LifecycleRegistry
import com.erolc.lifecycle.SystemLifecycle
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.route.ExitImpl
import com.erolc.mrouter.route.NormalFlag
import com.erolc.mrouter.route.RouteFlag
import com.erolc.mrouter.route.SysBackPressed
import com.erolc.mrouter.route.router.PageRouter
import com.erolc.mrouter.route.router.PanelRouter
import com.erolc.mrouter.route.router.WindowRouter
import com.erolc.mrouter.route.shareelement.ShareElementController
import com.erolc.mrouter.route.transform.*
import com.erolc.mrouter.scope.LocalPageScope
import com.erolc.mrouter.scope.PageScope
import com.erolc.mrouter.utils.loge
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

    //路由到当前界面的flag，需要在路由完成时（当前界面完全展示时）执行相应的操作
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
    private val pageRouter: PageRouter get() = scope.router.parentRouter as PageRouter

    @Composable
    override fun Content(modifier: Modifier) {
        scope.windowId = LocalWindowScope.current.id
        CompositionLocalProvider(LocalPageScope provides scope) {
            SysBackPressed { scope.backPressed() }
            Page(modifier)

            SystemLifecycle(::handleLifecycleEvent)
            val state by rememberPrivateInPage(
                "page_transform_state",
                transformState
            ) { transformState }
            if (state == ResumeState)
                start()

            DisposableEffect(this) {
                onDispose {
                    scope.transformTransition = null
                    if (isDestroy.value) {
                        ShareElementController.afterShare(this@PageEntry)
                        handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                    }
                }
            }
        }

    }

    private val wrapScope = TransformWrapScope()

    @OptIn(ExperimentalTransitionApi::class)
    @Composable
    private fun Page(modifier: Modifier) {
        val state = rememberPrivateInPage("page_state") {
            MutableTransitionState(transformState.value)
        }
        var isExit by rememberPrivateInPage("page_exit") { isExit }
        val isIntercept by rememberPrivateInPage("page_intercept") { isIntercept }


        val transition = rememberTransition(state).apply {
            if (enterStart) transformState.value = ResumeState

            if (exitFinished && !pageRouter.backStack.pop())
                if (pageRouter.parentRouter is WindowRouter)
                    isExit = true
                else
                    pageRouter.parentRouter.backPressed()

            if (resume) onResume()

            if (stop) handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        }
        if (scope.transformTransition == null) scope.transformTransition = transition

        val transform by rememberPrivateInPage("page_transform", transform) { transform }
        Box(transition.createModifier(transform, modifier, "Built-in")) {
            transform.gesture.run {
                setContent(address.content)
                val pageModifier = gestureModifier.getModifier().fillMaxSize()
                CompositionLocalProvider(LocalTransformWrapScope provides wrapScope) {
                    Wrap(pageModifier.onGloballyPositioned {
                        wrapScope.setSize(it.boundsInRoot())
                    }) {
                        transformState.value = when (it) {
                            0f -> {
                                ShareElementController.reset()
                                ResumeState
                            }

                            1f -> ExitState
                            else -> {
                                ShareElementController.sharing(1 - it)
                                ExitingState(1 - it)
                            }
                        }
                    }
                }
                check(isUseContent) { "必须在Wrap方法中使用PageContent,请检查 $this 的Wrap方法" }
            }
        }
        state.targetState = transformState.value
        if (isExit && !isIntercept) {
            ExitImpl()
            isExit = false
        }
    }

    @Composable
    private fun onResume() {
        pageRouter.backStack.execute(flag)
        flag = NormalFlag
        isUpdateTransform = false
        resume()
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
            EnterState -> ResumeState
            ExitState -> {
                transform.value.gesture.releasePauseModifier()
                ResumeState
            }

            ResumeState -> updatePrevTransform(entry)
            StopState -> StopState
            else -> StoppingState(1 - state.progress)
        }
    }

    /**
     * 更新上一个页面的transform
     */
    private fun updatePrevTransform(prev: PageEntry): TransformState {
        if (prev.isUpdateTransform) return StopState
        prev.transform.value = prev.transform.value.copy(prevPause = transform.value.prevPause)
        prev.transform.value.gesture.updatePauseModifier(transform.value.gesture.pauseModifierPost)
        prev.isUpdateTransform = true
        return StopState
    }

    private fun upFromEvent(state: Lifecycle.State) {
        if (registry.currentState == state) Lifecycle.Event.upFrom(state)
            ?.let { handleLifecycleEvent(it) }
    }

    private fun downFromEvent(state: Lifecycle.State) {
        if (registry.currentState == state) Lifecycle.Event.downFrom(state)
            ?.let { handleLifecycleEvent(it) }
    }

    internal fun create() = upFromEvent(Lifecycle.State.INITIALIZED)
    internal fun start() = upFromEvent(Lifecycle.State.CREATED)
    internal fun resume() = upFromEvent(Lifecycle.State.STARTED)
    internal fun pause() = downFromEvent(Lifecycle.State.RESUMED)

    /**
     * 这里之所以直接指定事件是因为window在最小化的时候会先调用windowIconified再调用windowLostFocus导致状态会丢失。
     * 这样做的目的是为了忽略windowLostFocus事件。
     */
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

