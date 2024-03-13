package com.erolc.mrouter.route.router

import com.erolc.mrouter.Constants
import com.erolc.mrouter.backstack.BackStack
import com.erolc.mrouter.backstack.entry.DialogEntry
import com.erolc.mrouter.backstack.entry.PanelEntry
import com.erolc.mrouter.backstack.entry.StackEntry
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.utils.logi

/**
 * 融合的路由，在这里会存在dialog的后退栈
 * 对于Panel来说，是必须一个界面的，可以让其选择
 */
class MergeRouter(private val addresses: List<Address>, override val parentRouter: Router?) : Router {
    private val stack = BackStack("dialogBackstack")

    //这里是当前界面中各个面板的回退栈
    private val panelStacks = mutableMapOf<String, PanelEntry>()

    init {
//        panelStacks[Constants.defaultLocal] = BackStack(Constants.defaultLocal)
    }

    private fun createDialogEntry(route: Route, address: Address): StackEntry? {
        if (route.dialogOptions != null) {
            return DialogEntry(
                route.dialogOptions,
                createPageEntry(route, address, EmptyRouter(this))
            )
        }
        return null
    }

    private fun dispatchPanel(route: Route, address: Address) {
        panelStacks[route.layoutKey]?.let {
            it.pageRouter.route(
                createPageEntry(
                    route,
                    address,
                    DialogRouter(it.pageRouter.addresses, it.pageRouter)
                )
            )
        } ?: run {
            panelStacks[route.layoutKey!!] = PanelEntry(Address(route.layoutKey)).also {
                it.newPageRouter(route, address)
            }
        }

    }

    private fun PanelEntry.newPageRouter(route: Route, address: Address) {
        pageRouter = PageRouter("panelBackStack", addresses, this@MergeRouter).also { pageRouter ->
            pageRouter.route(
                createPageEntry(
                    route,
                    address,
                    DialogRouter(pageRouter.addresses, pageRouter)
                )
            )
        }
    }

    override fun dispatchRoute(route: Route): Boolean {
        val isIntercept = parentRouter?.dispatchRoute(route) ?: false
        if (!isIntercept) {
            logi("dispatchRoute", "$this")
            val address = addresses.find { it.path == route.address }
            require(address != null) {
                "can't find the address with ‘${route.path}’"
            }
            if (route.layoutKey != null && route.dialogOptions != null) return false
            if (route.layoutKey != null) {
                dispatchPanel(route, address)
            } else {
                val entry = createDialogEntry(route, address)
                entry?.also { stack.addEntry(it) } ?: return false
            }
        }
        return true
    }

    override fun backPressed(notInterceptor: () -> Boolean) {
        if (notInterceptor() && !backPressedImpl())
            parentRouter?.backPressed(notInterceptor)
    }

    private fun backPressedImpl(): Boolean {
        return stack.pop()
    }

    internal fun getPanel(name: String): PanelEntry {
        return panelStacks[name]!!
    }

    internal fun getDialogBackStack() = stack.backStack

}