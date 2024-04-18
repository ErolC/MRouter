package com.erolc.mrouter.route.router

import com.erolc.lifecycle.Lifecycle
import com.erolc.mrouter.Constants
import com.erolc.mrouter.backstack.entry.*
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.backstack.BackStack
import com.erolc.mrouter.utils.loge

/**
 * 面板路由/局部路由。将管理一个页面中所有的面板，这些面板不存在上下级关系，所以并不会以[BackStack]作为其存储工具。
 *
 */
class PanelRouter(
    private val addresses: List<Address>,
    override val parentRouter: PageRouter,
    panelEntry: PanelEntry? = null
) : Router {
    //这里是当前界面中各个面板的回退栈
    private val panelStacks = mutableMapOf<String, PanelEntry>()
    private var showPanels = mutableListOf<String>()

    init {
        if (panelEntry != null)
            panelStacks[Constants.defaultLocal] = panelEntry
    }

    private fun createEntry(route: Route, address: Address): PanelEntry {
        return PanelEntry(Address(route.panelKey!!)).also {
            it.newPageRouter(route, address)
        }
    }

    private fun PanelEntry.newPageRouter(route: Route, address: Address) {
        pageRouter = PageRouter("panelBackStack", addresses, this@PanelRouter).also { pageRouter ->
            pageRouter.route(
                createPageEntry(
                    route,
                    address,
                    PanelRouter(addresses, pageRouter)
                )
            )
        }
    }

    internal fun route(key: String, route: Route) {

        panelStacks[key] ?: initPanel(route.copy(panelKey = key))
    }

    private fun initPanel(route: Route) {
        val address = addresses.find { it.path == route.address }
        if (address == null) {
            loge("MRouter", "not yet register the address：${route.address}")
            return
        }
        panelStacks[route.panelKey!!] = createEntry(route, address)

    }

    override fun router(route: Route) {
        dispatchRoute(route)
    }

    override fun dispatchRoute(route: Route) {
        val panel = panelStacks[route.panelKey]
        if (panel == null || !showPanels.contains(route.panelKey))
            parentRouter.dispatchRoute(route)
        else panel.pageRouter.let {
            val address = addresses.find { it.path == route.address }
            if (address == null) {
                loge("MRouter", "not yet register the address：${route.address}")
                return
            }
            it.route(createPageEntry(route, address, PanelRouter(addresses, it), true))
        }

    }


    internal fun showPanel(panelKey: String) {
        if (!showPanels.contains(panelKey)) {
            showPanels.add(panelKey)
            panelStacks.forEach { it.value.handleLifecycleEvent(Lifecycle.Event.ON_RESUME) }
        }
    }

    internal fun hidePanel(panelKey: String) {
        if (showPanels.remove(panelKey))
            panelStacks.forEach { it.value.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE) }
    }

    internal fun handleLifecycleEvent(event: Lifecycle.Event) {
        panelStacks.forEach { (key, panelEntry) ->
            if (showPanels.contains(key))
                panelEntry.handleLifecycleEvent(event)
        }
    }

    override fun backPressed(notInterceptor: () -> Boolean) {
        //panel是不需要后退的，如果其子路由已经无法后退，将事件交给他，那么他也是将这事件交给他的父路由处理即可
        if (notInterceptor()) parentRouter.backPressed(notInterceptor)
    }

    internal fun getPanel(name: String): PanelEntry {
        return panelStacks[name]
            ?: throw RuntimeException("由于没有找不到起始页面而无法初始化panel")
    }
}