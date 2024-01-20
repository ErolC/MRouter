package com.erolc.mrouter.route

import com.erolc.mrouter.backstack.BackStack
import com.erolc.mrouter.backstack.WindowEntry
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.register.Address

/**
 * window的路由器，生命周期比[PageRouter]更长。全局唯一
 */
class WindowRouter(
    private val addresses: List<Address>
) : Router("root") {
    /**
     * 路由到一个新的window上
     */
    private fun createWindow(route: Route, address: Address) {
        val entry = WindowEntry(
            route.windowOptions,
            PageRouter(this, null).apply {
                addresses = this@WindowRouter.addresses
            })

        backStack.addEntry(entry)

        entry.pageRoute(route, address)
    }


    override fun route(route: Route) {
        val address = addresses.find { it.path == route.address }
        require(address != null) {
            "can't find the address with ‘${route.path}’"
        }
        route(route, address)
    }

    override fun backPressed(body: () -> Boolean) {

    }

    internal fun route(route: Route, address: Address) {
        val windowEntry = backStack.findEntry(route.windowOptions.id) as? WindowEntry
        windowEntry?.run {
            pageRoute(route, address)
        } ?: createWindow(route, address)
    }

    /**
     * @return 是否需要退出应用
     */
    internal fun close(entry: WindowEntry): Boolean {
        return if (backStack.isBottom())
            true
        else {
            backStack.remove(entry.address.path)
            false
        }

    }


}