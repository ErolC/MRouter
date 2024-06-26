package com.erolc.mrouter.route.router

import androidx.compose.runtime.mutableStateOf
import com.erolc.mrouter.Constants.DEFAULT_WINDOW
import com.erolc.mrouter.backstack.BackStack
import com.erolc.mrouter.backstack.entry.StackEntry
import com.erolc.mrouter.backstack.entry.WindowEntry
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.model.Address
import com.erolc.mrouter.platform.isDesktop
import com.erolc.mrouter.route.ResourcePool.findAddress


/**
 * window的路由器，生命周期比[PageRouter]更长。全局唯一
 * window的路由器管理的是window，对于移动端来说，只有一个window：[DEFAULT_WINDOW].
 * 而对于桌面端来说，可以有多个窗口。
 */
class WindowRouter : Router {
    internal val backStack = BackStack("root")

    private fun createEntry(route: Route, address: Address): StackEntry {
        return WindowEntry(mutableStateOf(route.windowOptions)).also {
            it.newPageRouter(route, address)
        }
    }

    internal fun start(route: Route) {
        if (backStack.isEmpty()) {
            dispatchRoute(route)
        } else {
            dispatchOnAddressChange()
        }
    }

    private fun dispatchOnAddressChange() {
        backStack.backStack.value.forEach {
            (it as WindowEntry).dispatchOnAddressChange()
        }
    }

    /**
     * 该路由器已经是最高层的路由器，其没有父路由器
     */
    override val parentRouter: Router? = null

    override fun dispatchRoute(route: Route) {
        shouldDispatchRoute(route) {
            val pair = findAddress(route)
            require(pair != null) {
                "can't find the address with ‘${route.path}’"
            }
            val (address, realRoute) = pair
            it?.also { updateEntry(it as WindowEntry, realRoute, address) } ?: createEntry(
                realRoute,
                address
            )
        }
    }

    private fun shouldDispatchRoute(route: Route, block: (StackEntry?) -> StackEntry) {
        val oldEntry = backStack.findEntry(route.windowOptions.id)
        if (oldEntry == null || (oldEntry as WindowEntry).scope.isCloseWindow.value) {
            val entry = block(oldEntry?.takeIf { (it as WindowEntry).scope.isCloseWindow.value })
            route(entry)
        } else if (isDesktop) {
            oldEntry.pageRouter.route(route)
        }
    }


    override fun backPressed(notInterceptor: () -> Boolean) {
        if (notInterceptor()) parentRouter?.backPressed(notInterceptor)
    }

    private fun WindowEntry.newPageRouter(route: Route, address: Address) {
        pageRouter =
            PageRouter("windowBackStack", this@WindowRouter).also { pageRouter ->
                pageRouter.route(route)
            }
    }

    private fun updateEntry(oldEntry: WindowEntry, route: Route, address: Address) {
        oldEntry.options.value = route.windowOptions
        oldEntry.newPageRouter(route, address)
        oldEntry.scope.isCloseWindow.value = false
    }

    private fun route(stackEntry: StackEntry) {
        val entry = backStack.findEntry(stackEntry.address.path)
        if (entry == null) backStack.addEntry(stackEntry)
    }

    internal fun getBackStack() = backStack.backStack

}
