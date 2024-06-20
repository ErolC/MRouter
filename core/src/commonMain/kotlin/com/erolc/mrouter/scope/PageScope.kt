package com.erolc.mrouter.scope

import androidx.compose.animation.core.Transition
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.bundle.Bundle
import androidx.core.bundle.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.erolc.mrouter.lifecycle.addEventObserver
import com.erolc.mrouter.route.*
import com.erolc.mrouter.route.router.PanelRouter
import com.erolc.mrouter.route.transform.*
import com.erolc.mrouter.route.transform.EnterState
import com.erolc.mrouter.utils.PageCache
import com.erolc.mrouter.utils.rememberPrivateInPage

internal val default get() = PageScope()
internal fun getScope() = default

val LocalPageScope = compositionLocalOf { default }

/**
 * 页面范围（域），代表页面作用的区域，可以获取该页面相关的一些数据以及操作，比如获取页面的变换状态，监听页面生命周期等。
 */
class PageScope {
    internal val args = mutableStateOf(bundleOf())
    var name: String = ""
        internal set
    internal val result = bundleOf()
    internal var windowId = ""

    internal lateinit var router: PanelRouter
    internal var callBack: ResultCallBack? = null
    lateinit var pageCache: PageCache
        internal set
    private val interceptors = mutableListOf<BackInterceptor>()
    internal val transformState = mutableStateOf<TransformState>(EnterState)
    internal var transformTransition: Transition<TransformState>? = null
    internal val isIntercept = mutableStateOf(false)
    private val preArgs = bundleOf()

    internal fun initLifeCycle(lifecycle: Lifecycle) {
        lifecycle.addEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_DESTROY -> interceptors.clear()
                Lifecycle.Event.ON_RESUME -> {
                    callBack?.onCallResult()
                }

                else -> {}
            }
        }
    }

    /**
     * 路由到下一个页面
     * @param route 路由，其标准格式是: `[key|]`address`[?argKey=arg&argKey1=arg1]`
     * 其中只有address是必须的，?后面接的是参数；而key是[PanelEntry]的某个layout
     */
    fun route(route: String, builder: RouteBuilder.() -> Unit = {}) {
        val routeObj = RouteBuilder(windowId).apply(builder).build(callBack, route).let {
            it.copy(windowOptions = it.windowOptions.copy(currentWindowId = windowId))
        }
        routeObj.args.putAll(preArgs)

        router.dispatchRoute(routeObj)
    }

    fun setArgs(body: Bundle.() -> Unit) {
        preArgs.apply(body)
    }

    /**
     * 设置[backPressed]时返回给上一个页面的数据
     */
    fun setResult(block: Bundle.() -> Unit) {
        result.block()
    }

    /**
     * 设置[backPressed]时返回给上一个页面的数据
     */
    fun setResult(bundle: Bundle) {
        result.putAll(bundle)
    }

    /**
     * 后退
     */
    fun backPressed() {
        router.backPressed {
            val notInterceptor = interceptors.filter { it.isEnabled }.map {
                it.onIntercept(BackPressedHandlerImpl {
                    it.isEnabled = false
                })
                it
            }.none { it.isEnabled }
            isIntercept.value = !notInterceptor
            notInterceptor
        }
    }

    internal fun addBackInterceptor(interceptor: BackInterceptor) {
        interceptors.add(interceptor)
    }

    @Composable
    internal fun getTransformState(): Transition<TransformState>? {
        return transformTransition
    }
}

/**
 * 获取上一个页面传递过来的数据
 */
@Composable
fun rememberArgs(): Bundle {
    val args by LocalPageScope.current.args
    return args
}

/**
 * 添加生命周期事件监听
 */
@Composable
fun LifecycleObserver(key: String = "lifecycle", body: (LifecycleOwner, Lifecycle.Event) -> Unit) {
    val owner = LocalLifecycleOwner.current
    rememberPrivateInPage(key) {
        owner.lifecycle.addEventObserver(body)
    }
}