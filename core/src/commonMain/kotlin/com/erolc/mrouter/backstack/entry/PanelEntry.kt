package com.erolc.mrouter.backstack.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import com.erolc.mrouter.model.Address
import com.erolc.mrouter.route.router.PageRouter
import com.erolc.mrouter.scope.HostScope
import com.erolc.mrouter.utils.HostContent

/**
 * 局部界面的元素
 * 局部界面的后退需要注意的是，如果局部路由已经是最后一个后退点，那么将无法再后退。其后退事件将穿透到承载他的页面。
 *
 * @param address 该页面的地址
 */
class PanelEntry(override val address: Address) : StackEntry {
    //管理该面板的页面路由器
    internal lateinit var pageRouter: PageRouter
    internal val hostScope = HostScope()

    @Composable
    override fun Content(modifier: Modifier) {
        CompositionLocalProvider(LocalHostScope provides hostScope) {
            val lifecycleOwner = LocalLifecycleOwner.current
            pageRouter.HostContent(modifier, hostScope, lifecycleOwner)
        }
    }

    internal fun maxLifecycle(state: Lifecycle.State) {
        (pageRouter.backStack.backStack.value.lastOrNull() as? PageEntry)?.lifecycleOwnerDelegate?.maxLifecycle =
            state
    }

    override fun destroy() {
        (pageRouter.backStack.findTopEntry() as PageEntry).handleHostLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        pageRouter.backStack.pop()
    }

    fun dispatchOnAddressChange() {
        pageRouter.dispatchOnAddressChange()
    }
}