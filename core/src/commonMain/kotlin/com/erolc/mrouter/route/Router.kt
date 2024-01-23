package com.erolc.mrouter.route

import com.erolc.mrouter.backstack.BackStack
import com.erolc.mrouter.backstack.DialogEntry
import com.erolc.mrouter.backstack.StackEntry
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.utils.loge

typealias RouteResult = (Args) -> Unit

/**
 * 路由器，用于管理页面的前进和后退,不同的页面对象有不同的路由器:[WindowRouter]、[PageRouter]、[DialogRouter]
 */
abstract class Router(
    val name: String,
    internal val addresses: List<Address>,
    internal val parentRouter: Router? = null
) {
    internal val backStack = BackStack(name)

    /**
     * 创建页面对象
     */
    abstract fun createEntry(route: Route, address: Address): StackEntry?

    open fun dispatchRoute(route: Route): Boolean {
        val isIntercept = parentRouter?.dispatchRoute(route) ?: false
        loge("dispatchRoute", "$this $isIntercept")
        if (!isIntercept) {
            val address = addresses.find { it.path == route.address }
            require(address != null) {
                "can't find the address with ‘${route.path}’"
            }
            val entry = createEntry(route, address) ?: return false
            route(entry)
        }
        return true
    }

    /**
     * 路由方法，将路由到一个新的页面，
     * @param route 路由参数
     * @return 是否拦截该路由，true-拦截，false-不拦截
     */
    open fun route(stackEntry: StackEntry) {
        loge("route", "$this,${stackEntry.address.path}")
        backStack.addEntry(stackEntry)
    }

    /**
     * 后退方法，将回退到前一个页面
     * @param notInterceptor 是否不拦截
     */
    open fun backPressed(notInterceptor: () -> Boolean = { true }) {
        if (notInterceptor()) {
            if (!backStack.pop()) parentRouter?.backPressed(notInterceptor)
        }
    }

    internal fun getBackStack() = backStack.backStack
}