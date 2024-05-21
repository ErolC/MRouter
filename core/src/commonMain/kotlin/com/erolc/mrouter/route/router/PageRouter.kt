package com.erolc.mrouter.route.router

import androidx.annotation.RestrictTo
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.erolc.mrouter.MRouter
import com.erolc.mrouter.backstack.BackStack
import com.erolc.mrouter.backstack.entry.PageEntry
import com.erolc.mrouter.backstack.entry.StackEntry
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.model.SingleTop
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.platform.loge
import kotlinx.coroutines.flow.map

/**
 * 页面路由器的实现，将管理一个载体（window/panel）内所有的页面
 * @param addresses 存放着该库所注册的所有地址。
 * @param parentRouter 父路由，对于window内的页面路由来说，[WindowRouter]将是其父路由，同理，对于panel内的页面路由来说[PanelRouter]将是其父路由。
 * 路由器的关系将是[WindowRouter] -> [PageRouter] -> [PanelRouter] -> [PageRouter] -> [PanelRouter]
 */
open class PageRouter(
    name: String,
    private val addresses: List<Address>,
    override val parentRouter: Router
) : Router {
    internal val backStack = BackStack(name)

    internal fun route(stackEntry: StackEntry) {
        stackEntry as PageEntry
        when (stackEntry.address.config.launchMode) {
            is SingleTop -> backStack.findTopEntry()?.let {
                if (it.address.path == stackEntry.address.path) {
                    it as PageEntry
                    it.scope.run {
                        args.value = stackEntry.scope.args.value
                        router = stackEntry.scope.router
                        onResult = stackEntry.scope.onResult
                        name = stackEntry.scope.name
                    }
                } else null
            } ?: backStack.addEntry(stackEntry.apply { create() })
            else -> backStack.addEntry(stackEntry.apply { create() })
        }
    }

    internal var lifecycleOwner: LifecycleOwner? = null

    internal var hostLifecycleState: Lifecycle.State = Lifecycle.State.INITIALIZED
        get() {
            // A LifecycleOwner is not required by PageRouter.
            // In the cases where one is not provided, always keep the host lifecycle at CREATED
            return if (lifecycleOwner == null) {
                Lifecycle.State.CREATED
            } else {
                field
            }
        }

    private val lifecycleObserver: LifecycleObserver = LifecycleEventObserver { _, event ->
        hostLifecycleState = event.targetState
        for (entry in backStack.backStack.value) {
            (entry as PageEntry).handleHostLifecycleEvent(event)
        }
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun setLifecycleOwner(owner: LifecycleOwner) {
        if (owner == lifecycleOwner) {
            return
        }
        lifecycleOwner?.lifecycle?.removeObserver(lifecycleObserver)
        lifecycleOwner = owner
        owner.lifecycle.addObserver(lifecycleObserver)
    }

    /**
     * 获取展示的stack
     */
    internal fun getPlayStack() = backStack.backStack.map {
        it.takeLast(2).map { it as PageEntry }
    }

    private fun backPressedImpl() = backStack.preBack(parentRouter)

    /**
     * 分配路由，将地址分配给不同的路由器并打开
     */
    override fun dispatchRoute(route: Route) {
        if (route.windowOptions.id == route.windowOptions.currentWindowId)
            route(route)
        else
            parentRouter.dispatchRoute(route)
    }

    internal fun route(route: Route) {
        val address = addresses.find { it.path == route.address }
        if (address == null) {
            if (!MRouter.routeToPlatform(route)) {
                loge("MRouter", "not yet register the address：${route.address}")
            }
            return
        }
        val entry = MRouter.createEntry(
            route,
            address,
            PanelRouter(addresses, this, hostLifecycleState = hostLifecycleState),
            hostLifecycleState = hostLifecycleState
        )
        route(entry)
    }

    /**
     * 后退方法，将回退到前一个页面
     * @param notInterceptor 是否不拦截
     */
    override fun backPressed(notInterceptor: () -> Boolean) {
        if (notInterceptor() && !backPressedImpl())
            parentRouter.backPressed(notInterceptor)
    }

    internal fun getBackStack() = backStack.backStack

    fun dispatchOnAddressChange() {
        backStack.backStack.value.forEach {
            (it as PageEntry).lifecycleOwnerDelegate.resetLifecycle()
        }

    }


}