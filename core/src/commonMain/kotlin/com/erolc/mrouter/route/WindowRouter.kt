package com.erolc.mrouter.route

import com.erolc.mrouter.Constants
import com.erolc.mrouter.backstack.BackStack
import com.erolc.mrouter.backstack.StackEntry
import com.erolc.mrouter.backstack.WindowEntry
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.register.Address

/**
 * window的路由器，生命周期比[PageRouter]更长。全局唯一
 */
class WindowRouter(addresses: List<Address>) : Router("root", addresses) {

    override fun createEntry(route: Route, address: Address): StackEntry? {
        if (route.windowOptions.id != Constants.defaultWindow || backStack.isEmpty()) {
            return WindowEntry(
                route.windowOptions
            ).also {
                it.pageRouter = PageRouter(this).also { pageRouter ->
                    pageRouter.windowEntry = it
                    pageRouter.route(PageRouter.createPageEntry(route, address, DialogRouter(pageRouter)))
                }
            }
        }
        return null
    }

    override fun backPressed(notInterceptor: () -> Boolean) {}

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