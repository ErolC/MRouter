package com.erolc.mrouter.route.router

import com.erolc.mrouter.backstack.entry.LocalPageEntry
import com.erolc.mrouter.backstack.entry.PageEntry
import com.erolc.mrouter.backstack.entry.PanelEntry
import com.erolc.mrouter.backstack.entry.StackEntry
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.route.transform.none
import com.erolc.mrouter.scope.getScope


interface Router {
    val parentRouter: Router?

    fun router(route: Route) {}

    fun dispatchRoute(route: Route): Boolean
    fun backPressed(notInterceptor: () -> Boolean = { true })
}

/**
 * 创建一个pageEntry
 */
internal fun createPageEntry(
    route: Route,
    address: Address,
    router: Router,
): PageEntry {
    return PageEntry(
        getScope(),
        address
    ).apply {
        flag = route.flag
        transform.value = route.transform
        scope.run {
            argsFlow.value = route.args
            onResult = route.onResult
            this.router = router
            name = route.address
        }
    }
}

internal fun createLocalPanelEntry(
    route: Route,
    address: Address,
    router: Router,
    entry: PanelEntry,
): LocalPageEntry {
    return LocalPageEntry(
        getScope(),
        address, entry
    ).apply {
        transform.value = route.transform
        scope.run {
            argsFlow.value = route.args
            onResult = route.onResult
            this.router = router
            name = route.address
        }
    }
}