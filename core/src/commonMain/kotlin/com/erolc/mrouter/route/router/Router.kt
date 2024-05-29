package com.erolc.mrouter.route.router

import androidx.lifecycle.Lifecycle
import com.erolc.mrouter.backstack.entry.PageEntry
import com.erolc.mrouter.lifecycle.LifecycleOwnerDelegate
import com.erolc.mrouter.lifecycle.MRouterViewModelStoreProvider
import com.erolc.mrouter.lifecycle.createLifecycleOwnerDelegate
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
    fun dispatchRoute(route: Route)

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
    isReplace: Boolean = false,
    hostLifecycleState: Lifecycle.State = Lifecycle.State.CREATED,
    viewModelStoreProvider: MRouterViewModelStoreProvider?
): PageEntry {
    return PageEntry(
        getScope(),
        address,
        createLifecycleOwnerDelegate(viewModelStoreProvider, hostLifecycleState, route.args)
    ).apply {
        flag = if (isReplace) route.flag + ReplaceFlag else route.flag
        transform.value = route.transform
        scope.run {
            onResult = route.onResult
            this.router = router
            name = route.address
        }
    }
}