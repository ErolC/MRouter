package com.erolc.mrouter.route.router

import com.erolc.mrouter.backstack.entry.PageEntry
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.scope.getScope


interface Router {
    val parentRouter:Router?

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
        transform.value = route.transform
        scope.run {
            argsFlow.value = route.args
            onResult = route.onResult
            this.router = router
            name = route.address
        }
    }
}