package com.erolc.mrouter.route.router

import androidx.compose.runtime.mutableStateOf
import com.erolc.mrouter.Constants
import com.erolc.mrouter.backstack.BackStack
import com.erolc.mrouter.backstack.entry.*
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.route.routeBuild
import com.erolc.mrouter.utils.logi

/**
 * 融合的路由，在这里会存在dialog的后退栈
 * 对于Panel来说，是必须一个界面的，可以让其选择
 */
class MergeRouter(private val addresses: List<Address>, override val parentRouter: Router?) : Router {
    private val stack = BackStack("dialogBackstack")
    private var localShow = false

    //这里是当前界面中各个面板的回退栈
    private val panelStacks = mutableMapOf<String, PanelEntry>()
    private var entry: PageEntry? = null

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
            val router = it.pageRouter
            router.run {
                route(
                    createPageEntry(
                        route,
                        address,
                        DialogRouter(addresses, this)
                    )
                )
            }
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

    internal fun route(route: Route) {
        panelStacks[route.layoutKey]?.let {
//            val address = addresses.find { it.path == route.address }
//            require(address != null) {
//                "can't find the address with ‘${route.path}’"
//            }
//            val entry = it.pageRouter.createEntry(route,address)
//            it.pageRouter.backStack.addEntryWithFirst(entry!!)
        } ?: dispatchRoute(route)
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
                if (!localShow && route.layoutKey == Constants.defaultLocal) {
                    entry = createLocalPanelEntry(route, address, this, getPanel(route.layoutKey))
                    (parentRouter as PageRouter).backStack.addEntry(entry!!)
                }
            } else {
                val entry = createDialogEntry(route, address)
                entry?.also { stack.addEntry(it) } ?: return false
            }
        }
        return true
    }

    internal fun showWithLocal() {
        if (localShow) return
        localShow = true
//        getPanel(Constants.defaultLocal).shouldDestroy = false
        (parentRouter as PageRouter).backStack.pop()
    }

    internal fun hideWithLocal() {
        if (!localShow) return
        localShow = false
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