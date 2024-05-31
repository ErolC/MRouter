package com.erolc.mrouter

import androidx.annotation.RestrictTo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.*
import com.erolc.mrouter.backstack.entry.StackEntry
import com.erolc.mrouter.backstack.entry.WindowEntry
import com.erolc.mrouter.lifecycle.MRouterControllerViewModel
import com.erolc.mrouter.model.PlatformRoute
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.model.WindowOptions
import com.erolc.mrouter.platform.route
import com.erolc.mrouter.model.Address
import com.erolc.mrouter.platform.loge
import com.erolc.mrouter.register.Register
import com.erolc.mrouter.route.ResourcePool
import com.erolc.mrouter.route.RouteBuilder
import com.erolc.mrouter.route.routeBuild
import com.erolc.mrouter.route.router.PanelRouter
import com.erolc.mrouter.route.router.Router
import com.erolc.mrouter.route.router.WindowRouter
import com.erolc.mrouter.route.router.createPageEntry

/**
 * 路由库的本体
 */
object MRouter {

    private var rootRouter: WindowRouter = WindowRouter()
    private var startRoute: Route? = null
    private var registerBlock: (Register.() -> Unit)? = null
    private var viewModel: MRouterControllerViewModel? = null

    internal fun build(
        startTarget: String,
        windowOptions: WindowOptions,
        builder: Register.() -> Unit
    ) {
        val route = routeBuild(startTarget).copy(windowOptions = windowOptions)
        Register().apply {
            registerBlock?.invoke(this)
            builder()
        }.register()
        rootRouter.start(route)
        startRoute?.let {
            route(it)
            startRoute = null
        }
    }

    @Composable
    internal fun getRootBlackStack(): State<List<StackEntry>> {
        return rootRouter.getBackStack().collectAsState(listOf<WindowEntry>())
    }

    private fun route(route: Route) {
        rootRouter.run {
            val entry = backStack.findEntry(route.windowOptions.id) as? WindowEntry
            entry?.pageRouter?.dispatchRoute(route) ?: dispatchRoute(route)
        }
    }

    /**
     * 在compose外部（各个平台）使用该方法可路由到对应页面，但无法路由到对应页面的对应面板/局部（panel)中。
     */
    fun route(route: String, block: RouteBuilder.() -> Unit = {}) {
        val routeObj = routeBuild(route, block)
        if (ResourcePool.isEmpty())
            startRoute = routeObj
        else
            route(routeObj)
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    internal fun setViewModelStore(viewModelStore: ViewModelStore) {
        if (viewModel == MRouterControllerViewModel.getInstance(viewModelStore)) {
            return
        }
        viewModel = MRouterControllerViewModel.getInstance(viewModelStore)
    }

    /**
     * 注册
     */
    fun register(block: Register.() -> Unit) {
        registerBlock = block
    }

    internal fun clear(entryId: String) {
        viewModel?.clear(entryId)
    }

    internal fun setPlatformRes(key: String, value: Any) {
        ResourcePool.addPlatformRes(key to value)
    }

    internal fun createEntry(
        route: Route,
        address: Address,
        router: PanelRouter,
        isReplace: Boolean = false,
        hostLifecycleState: Lifecycle.State = Lifecycle.State.CREATED
    ) = createPageEntry(route, address, router, isReplace, hostLifecycleState, viewModel)

    internal fun routeToPlatform(route: Route): Unit? {
        val platformRoute = ResourcePool.getPlatformRes()[route.address] as? PlatformRoute
        return platformRoute?.let {
            rootRouter.route(it, route.args, route.callback)
        }
    }
}
