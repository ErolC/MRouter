package com.erolc.mrouter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import com.erolc.mrouter.backstack.entry.StackEntry
import com.erolc.mrouter.backstack.entry.WindowEntry
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.model.WindowOptions
import com.erolc.mrouter.register.RegisterBuilder
import com.erolc.mrouter.route.RouteBuilder
import com.erolc.mrouter.route.routeBuild
import com.erolc.mrouter.route.router.WindowRouter

/**
 * 路由库的本体
 */
object MRouter {

    private var rootRouter: WindowRouter? = null

    private var startRoute: Route? = null
    private var registerBlock: (RegisterBuilder.() -> Unit)? = null

    /**
     * 主后退栈，也是window后退栈，window节点将会保存在这里。
     * 主后退栈一般只有一个值
     */

    internal fun build(
        startTarget: String,
        windowOptions: WindowOptions,
        builder: RegisterBuilder.() -> Unit
    ) {
        val route = routeBuild(startTarget).copy(windowOptions = windowOptions)
        rootRouter = RegisterBuilder().apply {
            registerBlock?.invoke(this)
            builder()
        }.build(route)
        startRoute?.let {
            route(it)
            startRoute = null
        }
    }

    @Composable
    internal fun getRootBlackStack(): State<List<StackEntry>> {
        return rootRouter!!.getBackStack().collectAsState(listOf<WindowEntry>())
    }

    private fun route(route: Route) {
        rootRouter?.run {
            val entry = backStack.findEntry(route.windowOptions.id) as? WindowEntry
            entry?.pageRouter?.dispatchRoute(route) ?: dispatchRoute(route)
        }
    }

    /**
     * 在compose外部（各个平台）使用该方法可路由到对应页面，但无法路由到对应页面的对应面板/局部（panel)中。
     */
    fun route(route: String, block: RouteBuilder.() -> Unit = {}) {
        val routeObj = routeBuild(route,block)
        if (rootRouter == null)
            startRoute = routeObj
        else
            route(routeObj)
    }

    fun registerBuilder(block: RegisterBuilder.() -> Unit) {
        registerBlock = block
    }
}