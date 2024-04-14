package com.erolc.mrouter.route.router

import com.erolc.mrouter.backstack.entry.LocalPageEntry
import com.erolc.mrouter.backstack.entry.PageEntry
import com.erolc.mrouter.backstack.entry.PanelEntry
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.route.ReplaceFlag
import com.erolc.mrouter.scope.getScope

/**
 * 路由器，负责管理该路由的元素，包括器路由的分配以及回退
 */
interface Router {
    val parentRouter: Router?

    /**
     * 路由的起点
     */
    fun router(route: Route) {}

    /**
     * 分配路由
     */
    fun dispatchRoute(route: Route): Boolean

    /**
     * 回退
     * @param notInterceptor 是否不拦截
     */
    fun backPressed(notInterceptor: () -> Boolean = { true })
}

/**
 * 创建一个pageEntry
 */
internal fun createPageEntry(
    route: Route,
    address: Address,
    router: Router,
    isReplace:Boolean = false
): PageEntry {
    return PageEntry(
        getScope(),
        address
    ).apply {
        flag = if (isReplace) route.flag + ReplaceFlag else route.flag
        transform.value = route.transform
        scope.run {
            argsFlow.value = route.args
            onResult = route.onResult
            this.router = router
            name = route.address
        }
    }
}

/**
 * 创建一个面板的载体页面
 */
internal fun createLocalPanelEntry(
    route: Route,
    router: Router,
): LocalPageEntry {
    return LocalPageEntry(getScope()).apply {
        flag = route.flag
        transform.value = route.transform
        scope.run {
            argsFlow.value = route.args
            onResult = route.onResult
            this.router = router
            name = "localPanelEntry"
        }
    }
}