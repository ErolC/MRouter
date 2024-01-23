package com.erolc.mrouter.route


import com.erolc.mrouter.backstack.DialogEntry
import com.erolc.mrouter.backstack.PageEntry
import com.erolc.mrouter.backstack.StackEntry
import com.erolc.mrouter.backstack.WindowEntry
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.scope.WindowScope
import com.erolc.mrouter.scope.getScope
import com.erolc.mrouter.utils.loge

/**
 * 路由器实现
 * 路由器在路由的时候需要：
 * @param onResult 在后退时将值返回给前一个页面，
 * @param backStack 分析后退栈才能实现某些启动比如：singleTop
 * @param addresses 存放着该库所注册的所有地址。
 */
class PageRouter(
    windowRouter: WindowRouter
) :
    Router("windowBackStack", windowRouter.addresses, windowRouter) {

    internal lateinit var windowEntry: WindowEntry

    override fun route(stackEntry: StackEntry) {
        if (stackEntry.address.config.launchSingleTop)
            backStack.findTopEntry()?.also { entry ->
                entry.scope.run {
                    argsFlow.value = stackEntry.scope.argsFlow.value
                    router = stackEntry.scope.router
                    onResult = stackEntry.scope.onResult
                    name = stackEntry.scope.name
                }
            }
        else
            super.route(stackEntry)
    }

    override fun createEntry(route: Route, address: Address): StackEntry? {
        if (route.dialogOptions == null) return createPageEntry(
            route, address,
            DialogRouter(this)
        )
        return null
    }


    companion object {
        fun createPageEntry(
            route: Route,
            address: Address,
            router: DialogRouter,
        ): PageEntry {
            return PageEntry(
                getScope(),
                address,
                router
            ).apply {
                scope.run {
                    argsFlow.value = route.args
                    onResult = route.onResult
                    this.router = router
                    name = route.address
                }
            }
        }
    }
}