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
import com.erolc.mrouter.model.LaunchMode
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.model.SingleTop
import com.erolc.mrouter.model.Standard
import com.erolc.mrouter.platform.loge
import com.erolc.mrouter.route.ResourcePool.findAddress
import kotlinx.coroutines.flow.map

/**
 * 页面路由器的实现，将管理一个载体（window/panel）内所有的页面
 * @param parentRouter 父路由，对于window内的页面路由来说，[WindowRouter]将是其父路由，同理，对于panel内的页面路由来说[PanelRouter]将是其父路由。
 * 路由器的关系将是[WindowRouter] -> [PageRouter] -> [PanelRouter] -> [PageRouter] -> [PanelRouter]
 */
open class PageRouter(
    name: String,
    override val parentRouter: Router
) : Router {
    internal val backStack = BackStack(name)

    private fun StackEntry.updateEntry(
        stackEntry: PageEntry,
        launchMode: LaunchMode = SingleTop
    ): Unit? {
        val oldEntry = this as PageEntry
        return if (oldEntry.address.path == stackEntry.address.path) {
            oldEntry.scope.run {
                args.value = stackEntry.scope.args.value
                if (launchMode == SingleTop)
                    onResult = stackEntry.scope.onResult
                router = stackEntry.scope.router
                name = stackEntry.scope.name
            }
        } else null
    }

    private fun addEntry(stackEntry: PageEntry) = backStack.addEntry(stackEntry.apply { create() })

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
        findAddress(route)?.let { (it, route) ->
            val entry = MRouter.createEntry(
                route,
                it,
                PanelRouter(this),
                hostLifecycleState = hostLifecycleState
            )
            when (val launchMode = it.config.launchMode) {
                Standard -> addEntry(entry)
                else -> backStack.updateEntry(route, launchMode) ?: addEntry(entry)
            }
        } ?: MRouter.routeToPlatform(route) ?: loge(
            "MRouter",
            "not yet register the address：${route.address}"
        )
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