package com.erolc.mrouter.route.router

import com.erolc.mrouter.backstack.entry.DialogEntry
import com.erolc.mrouter.backstack.entry.StackEntry
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.register.Address

class DialogRouter(
    addresses:List<Address>,
    pageRouter: Router
) : RouterWrap("dialogRouter", addresses, parentRouter = pageRouter) {

    init {
        backStack.threshold = 0
    }

    override fun createEntry(route: Route, address: Address): StackEntry? {
        if (route.dialogOptions != null) {
            return DialogEntry(
                route.dialogOptions,
                createPageEntry(route, address, EmptyRouter(this))
            )
        }
        return null
    }

    override fun backPressed(notInterceptor: () -> Boolean) {
        if (notInterceptor()) {
            (backStack.findTopEntry() as? DialogEntry)?.dismiss() ?: parentRouter?.backPressed(
                notInterceptor
            )
        }

    }
}