package com.erolc.mrouter.backstack.entry

import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.core.bundle.Bundle
import androidx.core.bundle.bundleOf
import androidx.lifecycle.Lifecycle
import com.erolc.mrouter.MRouter
import com.erolc.mrouter.lifecycle.LifecycleOwnerDelegate
import com.erolc.mrouter.lifecycle.LocalOwnersProvider
import com.erolc.mrouter.lifecycle.addEventObserver
import com.erolc.mrouter.model.Address
import com.erolc.mrouter.platform.loge
import com.erolc.mrouter.route.ExitImpl
import com.erolc.mrouter.route.NormalFlag
import com.erolc.mrouter.route.ResultCallBack
import com.erolc.mrouter.route.RouteFlag
import com.erolc.mrouter.route.RouteResult
import com.erolc.mrouter.route.SysBackPressed
import com.erolc.mrouter.route.router.PageRouter
import com.erolc.mrouter.route.router.PanelRouter
import com.erolc.mrouter.route.router.WindowRouter
import com.erolc.mrouter.route.shareelement.ShareElementController
import com.erolc.mrouter.route.transform.*
import com.erolc.mrouter.scope.PageScope
import com.erolc.mrouter.utils.PageCache
import com.erolc.mrouter.utils.rememberPrivateInPage

/**
 * 页面载体，代表一个页面。
 */
class PageEntry internal constructor(
    val scope: PageScope,
    override val address: Address,
    internal val lifecycleOwnerDelegate: LifecycleOwnerDelegate
) : StackEntry {

    private val pageCache = PageCache()
    internal var callBack: ResultCallBack? = null
    private lateinit var original:Transform


    init {
        scope.initLifeCycle(lifecycleOwnerDelegate.lifecycle)
        scope.pageCache = pageCache
        scope.args.value = lifecycleOwnerDelegate.arguments ?: bundleOf()
        scope.callBack = ResultCallBack(lifecycleOwnerDelegate)
    }

    internal constructor(entry: PageEntry, argument: Bundle) : this(
        entry.scope,
        entry.address,
        LifecycleOwnerDelegate(entry.lifecycleOwnerDelegate, argument)
    ) {
        callBack = entry.callBack
        isUpdateTransform = entry.isUpdateTransform
        transform.value = entry.transform.value
        flag = entry.flag
    }

    /**
     * 页面唯一标识
     */
    val id get() = lifecycleOwnerDelegate.id

    //是否已冻结,冻结的entry的lifecycle是不会发生变化的
    internal var isFrozen = false

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
    private val pageRouter: PageRouter get() = scope.router.parentRouter

    internal fun setTransform(transform: Transform){
        original = transform
        this.transform.value = original
    }

    @Composable
    override fun Content(modifier: Modifier) {
        scope.windowId = LocalWindowScope.current.id
        val saveableStateHolder = rememberSaveableStateHolder()
        lifecycleOwnerDelegate.LocalOwnersProvider(saveableStateHolder, scope) {
            SysBackPressed { scope.backPressed() }
            Page(modifier)

            DisposableEffect(this) {
                onDispose {
                    scope.transformTransition = null
                    if (isDestroy.value) {
                        MRouter.clear(id)
                        ShareElementController.afterShare(this@PageEntry)
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
        val transition = rememberTransition(state)
        val newState by remember {
            derivedStateOf {
                transition.apply {
                    if (enterStart) transformState.value = ResumeState
                    if (exitFinished && !pageRouter.backStack.pop())
                        if (pageRouter.parentRouter is WindowRouter)
                            isExit = true
                        else
                            pageRouter.parentRouter.backPressed()

                    if (resume) onResume()
                }
            }
        }
        //这里必须如此
        if (scope.transformTransition.toString() == "null") scope.transformTransition = newState

        val transform by rememberPrivateInPage("page_transform", transform) { transform }
        Box(newState.createModifier(transform, modifier, "Built-in")) {
            transform.wrap.run {
                setContent(address.content)
                val pageModifier = gestureModifier.getModifier().fillMaxSize()
                CompositionLocalProvider(LocalTransformWrapScope provides wrapScope) {
                    wrapScope.progress = {
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
                    wrapScope.wrap = this
                    Wrap(pageModifier)
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

    private fun onResume() {
        pageRouter.backStack.execute(flag)
        flag = NormalFlag
        isUpdateTransform = false
    }

    /**
     * 和上一个页面共享同一个变换过程
     */
    @Composable
    internal fun shareTransform(entry: PageEntry?) {
        val state by remember(transformState) {
            transformState
        }
        updateState(this, state)

//        LocalHostScope.current.panelState?.pageState?.value = state
        entry?.also {
            val transformState = when (state) {
                EnterState -> ResumeState
                ExitState -> {
                    transform.value.wrap.releasePauseModifier()
                    ResumeState
                }

                ResumeState -> updatePrevTransform(entry)
                StopState -> StopState
                else -> StoppingState(1 - state.progress)
            }
            if (transformState == StopState)
                updateState(entry, transformState)
            entry.transformState.value = transformState
        }
    }

    @Composable
    private fun updateState(entry: PageEntry, state: TransformState) {
        DisposableEffect(state) {
            when (state) {
                EnterState -> entry.updateMaxState(Lifecycle.State.STARTED)
                ResumeState -> entry.updateMaxState(Lifecycle.State.RESUMED)
                StopState -> entry.updateMaxState(Lifecycle.State.CREATED)
                else -> {}
            }
            onDispose {}
        }
    }

    /**
     * 更新上一个页面的transform
     */
    private fun updatePrevTransform(prev: PageEntry): TransformState {
        if (prev.isUpdateTransform) return StopState
        transform.value = original
        prev.transform.value = prev.transform.value.copy(exit = transform.value.exit)
        prev.transform.value.wrap.updatePauseModifier(transform.value.wrap.pauseModifierPost)
        prev.isUpdateTransform = true
        return StopState
    }

    private fun updateMaxState(state: Lifecycle.State) {
        if (!isFrozen)
            lifecycleOwnerDelegate.maxLifecycle = state
    }

    internal fun create() = updateMaxState(Lifecycle.State.CREATED)


    override fun destroy() {
        isDestroy.value = true
        callBack?.setResult(scope.result)
        updateMaxState(Lifecycle.State.DESTROYED)
    }

    internal fun handleHostLifecycleEvent(event: Lifecycle.Event) {
        if (!isFrozen)
            lifecycleOwnerDelegate.handleLifecycleEvent(event)
    }
}