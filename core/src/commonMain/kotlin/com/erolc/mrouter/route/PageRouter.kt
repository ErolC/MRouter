package com.erolc.mrouter.route


import com.erolc.mrouter.backstack.DialogEntry
import com.erolc.mrouter.backstack.PageEntry
import com.erolc.mrouter.backstack.StackEntry
import com.erolc.mrouter.backstack.WindowEntry
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.register.GroupAddress
import com.erolc.mrouter.scope.PageScope
import com.erolc.mrouter.scope.getScope

/**
 * 路由器实现
 * 路由器在路由的时候需要：
 * @param onResult 在后退时将值返回给前一个页面，
 * @param backStack 分析后退栈才能实现某些启动比如：singleTop
 * @param addresses 存放着该库所注册的所有地址。
 */
class PageRouter(
    internal val windowRouter: WindowRouter,
    val parentScope: PageScope?,
) :
    Router("windowBackStack") {

    internal var addresses: List<Address> = listOf()
    internal lateinit var windowEntry: WindowEntry


    /**
     * 在window内通过route获取后退栈条目
     */
    internal fun route(
        route: Route,
        address: Address,
        target: StackEntry = createEntry(getScope(parentScope), address)
    ) {
        if (target is DialogEntry) {
            bindRoute(target, route, true)
        } else
            if (address.config.launchSingleTop) backStack.findTopEntry()?.also { entry ->
                bindRoute(entry, route)
            } ?: bindRoute(
                target, route, false
            )
            else bindRoute(
                target, route, true
            )
    }

    /**
     * 创建一个条目
     */
    private fun createEntry(scope: PageScope, address: Address): StackEntry {
        return when (address) {
            is GroupAddress -> {
                PageEntry(scope, address)
            }

            else -> {
                PageEntry(scope, address)
            }
        }
    }

    /**
     * 将路由上的数据绑定到下一个[entry]上。
     */
    private fun bindRoute(entry: StackEntry, route: Route, shouldAdd: Boolean = false) {
        if (entry is DialogEntry)
            entry.buildScope(route, this)
        else
            entry.scope.run {
                argsFlow.value = route.args
                router = this@PageRouter
                onResult = route.onResult
                name = route.address
            }
        if (shouldAdd) addEntry(entry)
    }


    override fun route(route: Route) {
        val address = addresses.find { it.path == route.address }
        require(address != null) {
            "can't find the address with ‘${route.path}’"
        }
        when {
            route.dialogOptions != null -> {
                route(
                    route,
                    address,
                    DialogEntry(route.dialogOptions, createEntry(getScope(parentScope), address))
                )
            }

            route.windowOptions.id == windowEntry.address.path -> route(
                route,
                address
            )

            else -> windowRouter.route(route, address)
        }
    }


    override fun backPressed(notInterceptor: () -> Boolean) {
        if (notInterceptor()){
            val entry = backStack.findTopEntry()
            if (entry is DialogEntry){
                entry.dismiss()
            }else{
                backPressedImpl()
            }
        }
    }

    override fun addEntry(entry: StackEntry) {
        if (entry is DialogEntry)
            entry.buildWindowScope(windowEntry.getScope())
        else
            entry.scope.windowScope = windowEntry.getScope()
        super.addEntry(entry)
    }
}