package com.erolc.mrouter.route.router

import com.erolc.mrouter.backstack.BackStack
import com.erolc.mrouter.backstack.entry.StackEntry
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.utils.loge
import com.erolc.mrouter.utils.logi

/**
 * 路由器，用于管理页面的前进和后退,不同的页面对象有不同的路由器:[WindowRouter]、[PageRouter]、[DialogRouter]
 * @param name 路由器的名字
 * @param addresses 地址列表，表示该路由器可以通过path在列表中找到对应的页面
 * @param parentRouter 父路由，页面进行路由或者后退时，都需要经过[parentRouter]具体逻辑可自行查阅[dispatchRoute]和[backPressed]方法。
 */
abstract class RouterWrap(
    val name: String,
    internal val addresses: List<Address>,
    override val parentRouter: Router? = null
) : Router {
    internal val backStack = BackStack(name)

    /**
     * 创建页面对象
     */
    abstract fun createEntry(route: Route, address: Address): StackEntry?

    /**
     * 分配路由，将地址分配给不同的路由器并打开
     */
    override fun dispatchRoute(route: Route): Boolean {
        val isIntercept = parentRouter?.dispatchRoute(route) ?: false
        if (!isIntercept && (route.layoutKey != null && parentRouter is PageRouter || route.layoutKey == null)) {
            val address = addresses.find { it.path == route.address }
            require(address != null) {
                "can't find the address with ‘${route.path}’"
            }
            val entry = createEntry(route, address) ?: return false
            route(entry)
            return true
        }
        return false
    }

    /**
     * 路由方法，将路由到一个新的页面，
     * @param route 路由参数
     */
    open fun route(stackEntry: StackEntry) {
        logi("route", "$this,${stackEntry.address.path}")
        backStack.addEntry(stackEntry)
    }

    /**
     * 后退方法，将回退到前一个页面
     * @param notInterceptor 是否不拦截
     */
    override fun backPressed(notInterceptor: () -> Boolean) {
        if (notInterceptor() && !backPressedImpl())
            parentRouter?.backPressed(notInterceptor)
    }

    internal fun getBackStack() = backStack.backStack

    internal open fun backPressedImpl() = backStack.pop()
}