package com.erolc.mrouter.route.router

import com.erolc.lifecycle.Lifecycle
import com.erolc.mrouter.Constants
import com.erolc.mrouter.backstack.entry.*
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.route.transform.NoneGestureWrap
import com.erolc.mrouter.route.transform.Transform
import com.erolc.mrouter.route.transform.none
import com.erolc.mrouter.utils.loge
import com.erolc.mrouter.utils.logi

/**
 * 融合的路由，在这里会存在dialog的后退栈
 * 对于Panel来说，是必须一个界面的，可以让其选择
 */
class PanelRouter(
    private val addresses: List<Address>,
    override val parentRouter: PageRouter,
    panelEntry: PanelEntry? = null
) : Router {
    //这里是当前界面中各个面板的回退栈
    private val panelStacks = mutableMapOf<String, PanelEntry>()
    private var localPanelShow = false
    private var isRoute = false

    init {
        if (panelEntry != null)
            panelStacks[Constants.defaultLocal] = panelEntry
    }

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
        panelStacks[route.layoutKey] ?: initPanel(route)
    }

    private fun initPanel(route: Route) {
        val address = addresses.find { it.path == route.address }
        require(address != null) {
            "can't find the address with ‘${route.path}’"
        }
        panelStacks[route.layoutKey!!] = createEntry(route, address)

    }

    override fun router(route: Route) {
        isRoute = true
        dispatchRoute(route)
        isRoute = false
    }

    override fun dispatchRoute(route: Route): Boolean {
        val isIntercept = parentRouter.dispatchRoute(route)
        val layoutKey = route.layoutKey
        //如果是内部的路由产生的路由事件，那么将交由内部处理。
        if (!isIntercept && isRoute) {
            val address = addresses.find { it.path == route.address }
            require(address != null) {
                "can't find the address with ‘${route.path}’"
            }
            val isLocal = layoutKey == Constants.defaultLocal

            val panel = panelStacks[route.layoutKey]
            // 如果这个时候，panel还是空，那么证明该panel就没有实现
            if (panel == null) {
                val newRoute = route.copy(layoutKey = null)
                parentRouter.route(newRoute)
                return true
            }else panel.pageRouter.run {
                route(createPageEntry(route, address, EmptyRouter(this), true))
            }
            if (isLocal && !localPanelShow) {
                val entry = createLocalPanelEntry(route, PanelRouter(addresses, parentRouter, panel))
                parentRouter.route(entry)
            }
            return true
        }
        return false
    }


    internal fun showWithLocal() {
        if (localPanelShow) return
        localPanelShow = true
        if (parentRouter.backStack.findTopEntry() is LocalPageEntry)
            parentRouter.backStack.pop(false)
        panelStacks.forEach { it.value.handleLifecycleEvent(Lifecycle.Event.ON_RESUME) }
    }

    internal fun hideWithLocal() {
        if (!localPanelShow) return
        localPanelShow = false
        panelStacks.forEach { it.value.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE) }
    }

    internal fun handleLifecycleEvent(event: Lifecycle.Event) {
        if (localPanelShow)
            panelStacks.forEach { it.value.handleLifecycleEvent(event) }
    }

    override fun backPressed(notInterceptor: () -> Boolean) {
        //panel是不需要后退的，如果其子路由已经无法后退，将事件交给他，那么他也是将这事件交给他的父路由处理即可
        if (notInterceptor())
            parentRouter.backPressed(notInterceptor)
    }

    internal fun getPanel(name: String): PanelEntry {
        return panelStacks[name]!!
    }
}