package com.erolc.mrouter.route.router

import com.erolc.lifecycle.Lifecycle
import com.erolc.mrouter.Constants
import com.erolc.mrouter.backstack.entry.*
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.backstack.BackStack
import com.erolc.mrouter.isLocalPanelEntry
import com.erolc.mrouter.route.routeBuild
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
    private var localPanelShow = false
    private var isRoute = false

    init {
        if (panelEntry != null)
            panelStacks[Constants.defaultLocal] = panelEntry
    }

    private fun createEntry(route: Route, address: Address): PanelEntry {
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
                    PanelRouter(addresses,pageRouter)
                )
            )
        }
    }

    internal fun route(route: Route) {
        panelStacks[route.layoutKey] ?: initPanel(route)
    }

    private fun initPanel(route: Route) {
        val address = addresses.find { it.path == route.address }
        if (address == null) {
            loge("MRouter", "not yet register the address：${route.address}")
            return
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
//        val layoutKey = route.layoutKey
        //如果是内部的路由产生的路由事件，那么将交由内部处理。
        if (!isIntercept && isRoute) {
            val address = addresses.find { it.path == route.address }
            if (address == null) {
                loge("MRouter", "not yet register the address：${route.address}")
                return true
            }
//            val isLocal = layoutKey == Constants.defaultLocal

            val panel = panelStacks[route.layoutKey]
            // 如果这个时候，panel还是空，那么证明该panel就没有实现
            if (panel == null || !localPanelShow) {
                val newRoute = route.copy(layoutKey = null)
                parentRouter.route(newRoute)
                return true
            } else panel.pageRouter.let {
                val temp = if (localPanelShow) address else address.let { address ->
                    address.copy(config = address.config.copy(launchSingleTop = true))
                }
                it.route(createPageEntry(route, temp, PanelRouter(addresses,it), true))
            }
//            if (isLocal && !localPanelShow) {
//                val routeBuild = routeBuild(Constants.defaultPrivateLocal).copy(flag = route.flag, windowOptions = route.windowOptions, transform = route.transform)
//                val entry = createLocalPanelEntry(routeBuild,this)
////                val localAddress = addresses.find { it.path == routeBuild.address }
////                val entry = createPageEntry(routeBuild, localAddress!!, this)
//                parentRouter.route(entry)
//            }
            return true
        }
        return false
    }


    internal fun showWithLocal() {
        if (localPanelShow) return
        localPanelShow = true
        if (parentRouter.backStack.findTopEntry()?.isLocalPanelEntry() == true)
            parentRouter.backStack.pop(false)
        panelStacks.forEach { it.value.handleLifecycleEvent(Lifecycle.Event.ON_RESUME) }
    }

    internal fun hideWithLocal() {
        if (!localPanelShow) return
        localPanelShow = false
        panelStacks.forEach { it.value.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE) }
    }

    internal fun handleLifecycleEvent(event: Lifecycle.Event) {
        if (localPanelShow) panelStacks.forEach { it.value.handleLifecycleEvent(event) }
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