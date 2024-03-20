package com.erolc.mrouter.route.router

import com.erolc.mrouter.backstack.BackStack
import com.erolc.mrouter.backstack.entry.PageEntry
import com.erolc.mrouter.backstack.entry.StackEntry
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.register.Address
import kotlinx.coroutines.flow.map

/**
 * 路由器实现
 * 路由器在路由的时候需要：
 * @param onResult 在后退时将值返回给前一个页面，
 * @param backStack 分析后退栈才能实现某些启动比如：singleTop
 * @param addresses 存放着该库所注册的所有地址。
 */
open class PageRouter(name: String, private val addresses: List<Address>, override val parentRouter: Router) : Router {
    internal val backStack = BackStack(name)

    fun route(stackEntry: StackEntry) {
        stackEntry as PageEntry
        if (stackEntry.address.config.launchSingleTop)
            backStack.findTopEntry()?.also { entry ->
                entry as PageEntry
                entry.scope.run {
                    argsFlow.value = stackEntry.scope.argsFlow.value
                    router = stackEntry.scope.router
                    onResult = stackEntry.scope.onResult
                    name = stackEntry.scope.name
                }
            }
        else
            backStack.addEntry(stackEntry)
    }

    fun createEntry(route: Route, address: Address): StackEntry {
        return createPageEntry(route, address, PanelRouter(addresses, this))
        //需要在这里将局部页面当做单页面的条件有两个：route的localKey是local；当前的页面情况已经小于能够显示该局部的尺寸
        //创建的方式是获取当前界面，并在当前界面中的mergeRouter中提取出local的回退栈并构建一个特殊的页面作为承载即可。
    }

    /**
     * 获取展示的stack
     */
    fun getPlayStack() = backStack.backStack.map {
        it.takeLast(2)
    }

    fun backPressedImpl(): Boolean {
        return backStack.preBack()
    }

    private fun shouldLoadPage(route: Route): Boolean {
        return (route.layoutKey == null && parentRouter is WindowRouter) || (route.layoutKey != null && parentRouter is PanelRouter)
    }

    /**
     * 分配路由，将地址分配给不同的路由器并打开
     */
    override fun dispatchRoute(route: Route): Boolean {
        val isIntercept = parentRouter.dispatchRoute(route)
        if (!isIntercept && shouldLoadPage(route)) {
            val address = addresses.find { it.path == route.address }
            require(address != null) {
                "can't find the address with ‘${route.path}’"
            }
            val entry = createEntry(route, address)
            route(entry)
            return true
        }
        return false
    }


    /**
     * 后退方法，将回退到前一个页面
     * @param notInterceptor 是否不拦截
     */
    override fun backPressed(notInterceptor: () -> Boolean) {
        if (notInterceptor() && !backPressedImpl())
            parentRouter.backPressed(notInterceptor)
    }

    internal fun getBackStack() = backStack.backStack

}