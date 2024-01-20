package com.erolc.mrouter.route

import com.erolc.mrouter.backstack.BackStack
import com.erolc.mrouter.backstack.StackEntry
import com.erolc.mrouter.model.Route

typealias RouteResult = (Args) -> Unit

/**
 * 路由器，用于管理页面的前进和后退
 */
abstract class Router(val name: String) {
    internal val backStack = BackStack(name)

    /**
     * 路由方法，将路由到一个新的页面
     */
    abstract fun route(route: Route)

    /**
     * 后退方法，将回退到前一个页面
     * @param notInterceptor 是否不拦截
     */
    abstract fun backPressed(notInterceptor: () -> Boolean = { true })

    internal fun getBackStack() = backStack.backStack

    internal fun backPressedImpl() {
        backStack.pop()
    }

    internal open fun addEntry(entry: StackEntry) {
        backStack.addEntry(entry)
    }
}