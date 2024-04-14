package com.erolc.mrouter.scope

import androidx.compose.animation.core.Transition
import androidx.compose.runtime.*
import com.erolc.lifecycle.Lifecycle
import com.erolc.lifecycle.LifecycleOwner
import com.erolc.lifecycle.addEventObserver
import com.erolc.mrouter.route.*
import com.erolc.mrouter.route.router.EmptyRouter
import com.erolc.mrouter.route.router.PanelRouter
import com.erolc.mrouter.route.router.Router
import com.erolc.mrouter.route.transform.*
import com.erolc.mrouter.route.transform.PreEnter
import com.erolc.mrouter.utils.PageCache
import com.erolc.mrouter.utils.loge
import com.erolc.mrouter.utils.rememberInPage
import kotlinx.coroutines.flow.MutableStateFlow

internal val default get() = PageScope()
internal fun getScope() = default

val LocalPageScope = staticCompositionLocalOf { default }

/**
 * 页面范围（域），代表页面作用的区域，可以获取该页面相关的一些数据以及操作，比如获取页面的变换状态，监听页面生命周期等。
 */
open class PageScope {
    internal val argsFlow = MutableStateFlow(emptyArgs)
    internal var name: String = ""
    private val result = emptyArgs

    //当前页面范围是否是LocalPageEntry
    internal var isLocalPageEntry = false

    //这个router存在两种可能，一种是panelRouter，一种是EmptyRouter
    internal lateinit var router: Router
    var pageCache = PageCache()
    private set
    internal var onResult: RouteResult = {}
    private val interceptors = mutableListOf<BackInterceptor>()
    private lateinit var _lifecycle: Lifecycle
    internal val transformState = mutableStateOf<TransformState>(PreEnter)
    internal var transformTransition: Transition<TransformState>? = null
    internal val isIntercept = mutableStateOf(false)

    //生命周期
    var lifecycle: Lifecycle
        internal set(value) {
            initLifeCycle(value)
            _lifecycle = value
        }
        get() = _lifecycle

    private fun initLifeCycle(lifecycle: Lifecycle) {
        lifecycle.addEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY && !isLocalPageEntry) {
                onResult(result)
                interceptors.clear()
            }
        }
    }


    /**
     * 路由到下一个页面
     * @param route 路由，其标准格式是: `[key/]`address`[?argKey=arg&argKey1=arg1]`
     * 其中只有address是必须的，?后面接的是参数；而key是[GroupScope]的某个layout
     */
    open fun route(route: String, builder: RouteBuilder.() -> Unit = {}) {
        val routeObj = routeBuild(route, builder)
        router.router(routeObj)
    }


    /**
     * 设置[backPressed]时返回给上一个页面的数据
     */
    fun setResult(args: Args) {
        result += args
    }

    /**
     * 设置[backPressed]时返回给上一个页面的数据
     */
    fun setResult(arg: Arg) {
        result += arg
    }

    /**
     * 设置[backPressed]时返回给上一个页面的数据
     */
    fun setResult(pair: Pair<String, Any>) {
        result += pair.toArg()
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
    internal fun rememberTransform(): Transition<TransformState>? {
        return transformTransition
    }
}

/**
 * 获取上一个页面传递过来的数据
 */
@Composable
fun rememberArgs(): Args {
    val args by LocalPageScope.current.argsFlow.collectAsState()
    return args
}

/**
 * 添加生命周期事件监听
 */
@Composable
fun addEventObserver(body: (LifecycleOwner, Lifecycle.Event) -> Unit) {
    val scope = LocalPageScope.current
    rememberInPage("page_event") {
        if (!scope.isLocalPageEntry)
            scope.lifecycle.addEventObserver(body)
    }
}