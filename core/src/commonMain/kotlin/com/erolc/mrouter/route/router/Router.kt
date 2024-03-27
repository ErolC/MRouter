package com.erolc.mrouter.route.router

import com.erolc.mrouter.backstack.entry.LocalPageEntry
import com.erolc.mrouter.backstack.entry.PageEntry
import com.erolc.mrouter.backstack.entry.PanelEntry
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.route.ReplaceFlag
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

internal fun createLocalPanelEntry(
    route: Route,
    router: Router,
    entry: PanelEntry,
): LocalPageEntry {
    return LocalPageEntry(
        getScope(),
        entry
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