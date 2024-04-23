package com.erolc.mrouter.route.router

import com.erolc.mrouter.backstack.BackStack
import com.erolc.mrouter.backstack.entry.PageEntry
import com.erolc.mrouter.backstack.entry.StackEntry
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.utils.loge
import kotlinx.coroutines.flow.map

/**
 * 页面路由器的实现，将管理一个载体（window/panel）内所有的页面
 * @param addresses 存放着该库所注册的所有地址。
 * @param parentRouter 父路由，对于window内的页面路由来说，[WindowRouter]将是其父路由，同理，对于panel内的页面路由来说[PanelRouter]将是其父路由。
 * 路由器的关系将是[WindowRouter] -> [PageRouter] -> [PanelRouter] -> [PageRouter] -> [PanelRouter]
 */
open class PageRouter(name: String, private val addresses: List<Address>, override val parentRouter: Router) : Router {
    internal val backStack = BackStack(name)

    internal fun route(stackEntry: StackEntry) {
        stackEntry as PageEntry
        if (stackEntry.address.config.launchSingleTop)
            backStack.findTopEntry()?.let {
                if (it.address.path == stackEntry.address.path) {
                    it as PageEntry
                    it.scope.run {
                        args.value = stackEntry.scope.args.value
                        router = stackEntry.scope.router
                        onResult = stackEntry.scope.onResult
                        name = stackEntry.scope.name
                    }
                } else null
            } ?: backStack.addEntry(stackEntry.apply { create() })
        else
            backStack.addEntry(stackEntry.apply { create() })
    }

    /**
     * 获取展示的stack
     */
    internal fun getPlayStack() = backStack.backStack.map {
        it.takeLast(2).map { it as PageEntry }
    }

    private fun backPressedImpl() = backStack.preBack(parentRouter)

    /**
     * 分配路由，将地址分配给不同的路由器并打开
     */
    override fun dispatchRoute(route: Route) {
        if (route.windowOptions.id == route.windowOptions.currentWindowId)
            route(route)
        else
            parentRouter.dispatchRoute(route)
    }

    internal fun route(route: Route) {
        val address = addresses.find { it.path == route.address }
        if (address == null) {
            loge("MRouter", "not yet register the address：${route.address}")
            return
        }
        val entry = createPageEntry(route, address, PanelRouter(addresses, this))
        route(entry)
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