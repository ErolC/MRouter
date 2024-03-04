package com.erolc.mrouter.route

import androidx.compose.runtime.mutableStateOf
import com.erolc.mrouter.Constants
import com.erolc.mrouter.backstack.entry.StackEntry
import com.erolc.mrouter.backstack.entry.WindowEntry
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.register.Address

/**
 * window的路由器，生命周期比[PageRouter]更长。全局唯一
 * window的路由器管理的是window，对于移动端来说，只有一个window：[Constants.defaultWindow].
 * 而对于桌面端来说，可以有多个窗口。
 * @param addresses 所注册的所有地址
 */
class WindowRouter(addresses: List<Address>) : Router("root", addresses) {

    override fun createEntry(route: Route, address: Address): StackEntry? {
        return if (shouldCreateWindow(route))
            WindowEntry(mutableStateOf(route.windowOptions)).also {
                it.newPageRouter(route, address)
            }
        else null
    }

    private fun shouldCreateWindow(route: Route): Boolean {
        return backStack.isEmpty() || backStack.findEntry(route.windowOptions.id) == null
    }

    override fun dispatchRoute(route: Route): Boolean {
        val isIntercept = parentRouter?.dispatchRoute(route) ?: false
        if (!isIntercept) {
            val address = addresses.find { it.path == route.address }
            require(address != null) {
                "can't find the address with ‘${route.path}’"
            }
            val oldEntry = backStack.findEntry(route.windowOptions.id)
                ?.takeIf { (it as WindowEntry).scope.isCloseWindow.value }
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
        oldEntry.scope.isCloseWindow.value = false
    }

    override fun route(stackEntry: StackEntry) {
        val entry = backStack.findEntry(stackEntry.address.path)
        if (entry == null) super.route(stackEntry)
    }

    override fun backPressedImpl(): Boolean {
        return false
    }
}