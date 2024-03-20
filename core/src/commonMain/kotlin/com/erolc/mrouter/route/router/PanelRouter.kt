package com.erolc.mrouter.route.router

import com.erolc.mrouter.Constants
import com.erolc.mrouter.backstack.entry.*
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.utils.logi

/**
 * 融合的路由，在这里会存在dialog的后退栈
 * 对于Panel来说，是必须一个界面的，可以让其选择
 */
class PanelRouter(private val addresses: List<Address>, override val parentRouter: PageRouter) : Router {
    //这里是当前界面中各个面板的回退栈
    private val panelStacks = mutableMapOf<String, PanelEntry>()
    private var entry: PageEntry? = null
    private var localPanelShow = false
    private var isRoute = false


    fun createEntry(route: Route, address: Address): PanelEntry {
        return PanelEntry(Address(route.layoutKey!!)).also {
            it.newPageRouter(route, address)
        }
    }

    private fun PanelEntry.newPageRouter(route: Route, address: Address) {
        pageRouter = PageRouter("panelBackStack", addresses, this@PanelRouter).also { pageRouter ->
            pageRouter.route(
                createPageEntry(
                    route,
                    address,
                    EmptyRouter(pageRouter)
                )
            )
        }
    }

    internal fun route(route: Route) {
        panelStacks[route.layoutKey] ?: dispatchRoute(route)
    }

    override fun router(route: Route) {
        isRoute = true
        dispatchRoute(route)
        isRoute = false
    }

    override fun dispatchRoute(route: Route): Boolean {
        val isIntercept = parentRouter.dispatchRoute(route)
        if (!isIntercept) {
            logi("dispatchRoute", "$this")
            val address = addresses.find { it.path == route.address }
            require(address != null) {
                "can't find the address with ‘${route.path}’"
            }
            val panel = panelStacks[route.layoutKey] ?: createEntry(route, address)
            if (!localPanelShow && route.layoutKey == Constants.defaultLocal) {
                entry = createLocalPanelEntry(route, address, this, panel)
                parentRouter.backStack.addEntry(entry!!)
            }
            if (panelStacks.contains(route.layoutKey)) {
                if (isRoute) {
                    panelStacks[route.layoutKey]?.pageRouter?.run {
                        route(createPageEntry(route, address, EmptyRouter(this)))
                    }
                }
                return false
            } else {
                panelStacks[route.layoutKey!!] = panel
            }

        }
        return true
    }

    internal fun showWithLocal() {
        if (localPanelShow) return
        localPanelShow = true
        parentRouter.backStack.pop()
    }

    internal fun hideWithLocal() {
        if (!localPanelShow) return
        localPanelShow = false
    }

    //panel是没有不需要后退的，如果其子路由已经无法后退，将事件交给他，那么他也是将这事件交给他的父路由处理即可
    override fun backPressed(notInterceptor: () -> Boolean) {
        if (notInterceptor())
            parentRouter.backPressed(notInterceptor)
    }

    internal fun getPanel(name: String): PanelEntry {
        return panelStacks[name]!!
    }
}