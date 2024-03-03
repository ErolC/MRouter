package com.erolc.mrouter.route

import androidx.compose.runtime.mutableStateOf
import com.erolc.mrouter.Constants
import com.erolc.mrouter.backstack.StackEntry
import com.erolc.mrouter.backstack.WindowEntry
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.scope.WindowScope
import com.erolc.mrouter.scope.getScope
import com.erolc.mrouter.utils.logi

/**
 * window的路由器，生命周期比[PageRouter]更长。全局唯一
 */
class WindowRouter(addresses: List<Address>) : Router("root", addresses) {

    override fun createEntry(route: Route, address: Address): StackEntry? {
        if (route.windowOptions.id != Constants.defaultWindow || backStack.isEmpty()) {
            return WindowEntry(mutableStateOf(route.windowOptions)).also {
                it.newPageRouter(route, address)
            }
        }
        return null
    }

    override fun dispatchRoute(route: Route): Boolean {
        val isIntercept = parentRouter?.dispatchRoute(route) ?: false
        if (!isIntercept) {
            logi("dispatchRoute", "$this")
            val address = addresses.find { it.path == route.address }
            require(address != null) {
                "can't find the address with ‘${route.path}’"
            }
            val oldEntry = backStack.findEntry(route.windowOptions.id)
            val entry =
                oldEntry?.also { updateEntry(it as WindowEntry, route, address) } ?: createEntry(route, address)
                ?: return false
            route(entry)
        }
        return true
    }

    private fun WindowEntry.newPageRouter(route: Route, address: Address) {
        pageRouter = PageRouter(this@WindowRouter).also { pageRouter ->
            pageRouter.route(
                PageRouter.createPageEntry(
                    route,
                    address,
                    DialogRouter(pageRouter)
                )
            )
        }
    }

    private fun updateEntry(oldEntry: WindowEntry, route: Route, address: Address) {
        oldEntry.options.value = route.windowOptions
        oldEntry.newPageRouter(route, address)
        oldEntry.getScope().isCloseWindow.value = false
    }

    override fun route(stackEntry: StackEntry) {
        val entry = backStack.findEntry(stackEntry.address.path)
        if (entry == null) super.route(stackEntry)
    }

    override fun backPressedImpl(): Boolean {
        return false
    }
}