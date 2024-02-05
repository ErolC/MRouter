package com.erolc.mrouter.scope

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.erolc.lifecycle.Lifecycle
import com.erolc.lifecycle.addEventObserver
import com.erolc.mrouter.route.*
import com.erolc.mrouter.route.Router
import kotlinx.coroutines.flow.MutableStateFlow

internal val default get() = PageScope()
internal fun getScope() = default

val LocalPageScope = staticCompositionLocalOf { default }

open class PageScope {
    internal val argsFlow = MutableStateFlow(emptyArgs)
    internal var name: String = ""
    private val result = emptyArgs
    private val values = mutableMapOf<String, Any>()
    internal lateinit var router: Router
    internal var onResult: RouteResult = {}
    private val interceptors = mutableListOf<BackInterceptor>()

    var lifecycle: Lifecycle? = null
        internal set(value) {
            if (value != null) initLifeCycle(value)
            field = value
        }

    private fun initLifeCycle(lifecycle: Lifecycle) {
        lifecycle.addEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
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
        router.dispatchRoute(routeObj)
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
            interceptors.filter { it.isEnabled }.map {
                it.onIntercept(BackPressedHandlerImpl {
                    it.isEnabled = false
                })
                it
            }.none { it.isEnabled }
        }
    }


    internal fun <T : Any> getValue(key: String): T? {
        return values[key] as? T
    }

    internal fun saveValue(key: String, value: Any) {
        values[key] = value
    }

    fun addBackInterceptor(interceptor: BackInterceptor) {
        interceptors.add(interceptor)
    }

}

@Composable
fun rememberArgs(): Args {
    val args by LocalPageScope.current.argsFlow.collectAsState()
    return args
}
