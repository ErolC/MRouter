package com.erolc.mrouter.backstack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.erolc.mrouter.Transforms
import com.erolc.mrouter.lifecycle.Lifecycle
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.model.WindowOptions
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.route.PageRouter
import com.erolc.mrouter.scope.WindowScope
import com.erolc.mrouter.utils.PlatformWindow


class WindowEntry(val options: WindowOptions, private val pageRouter: PageRouter) :
    StackEntry(WindowScope().apply { name = options.id }, Address(options.id)) {

    override val lifecycle: Lifecycle
        get() = registry

    init {
        pageRouter.windowEntry = this
    }

    /**
     * 在window内通过route获取后退栈条目
     */
    fun pageRoute(route: Route, address: Address) {
        pageRouter.run {
            route(route, address)
        }
    }

    internal fun getScope() = scope as WindowScope

    fun close(): Boolean {
        getScope().lifecycleEvent.value = Lifecycle.Event.ON_DESTROY
        return pageRouter.windowRouter.close(this)
    }

    @Composable
    override fun Content() {
        PlatformWindow(options, this) {
            val backStacks by pageRouter.getBackStack().collectAsState()
            Transforms(backStacks)
//            val toastOptions by remember { getScope().toastOptions }
//            if (toastOptions != null) {
//                Toast(toastOptions!!)
//            }
        }
    }

}