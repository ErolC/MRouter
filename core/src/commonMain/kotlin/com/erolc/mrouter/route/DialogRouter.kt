package com.erolc.mrouter.route

import com.erolc.mrouter.backstack.DialogEntry
import com.erolc.mrouter.backstack.StackEntry
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.register.Address

class DialogRouter(
    pageRouter: Router
) : Router("dialogRouter", pageRouter.addresses, parentRouter = pageRouter) {

    override fun createEntry(route: Route, address: Address): StackEntry? {
        if (route.dialogOptions != null) {
            return DialogEntry(
                route.dialogOptions,
                PageRouter.createPageEntry(route, address, DialogRouter(this))
            )
        }
        return null
    }

    override fun backPressed(notInterceptor: () -> Boolean) {

    }
}