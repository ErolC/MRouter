package com.erolc.mrouter.backstack.entry

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.erolc.lifecycle.Lifecycle
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.route.router.PageRouter
import com.erolc.mrouter.route.router.PanelRouter
import com.erolc.mrouter.route.shareele.ShareEleController
import com.erolc.mrouter.route.transform.PauseState
import com.erolc.mrouter.route.transform.Resume
import com.erolc.mrouter.scope.LocalPageScope
import com.erolc.mrouter.scope.default
import com.erolc.mrouter.utils.loge
import com.erolc.mrouter.utils.logi

/**
 * 局部界面的元素
 * 局部界面的后退需要注意的是，如果局部路由已经是最后一个后退点，那么将无法再后退。其后退事件将穿透到承载他的页面。
 *
 * @param address 该页面的地址
 */
class PanelEntry(override val address: Address) : StackEntry {
    //管理该面板的页面路由器
    internal lateinit var pageRouter: PageRouter
    internal var isLocalPageEntry = false

    @Composable
    override fun Content(modifier: Modifier) {
        Box(modifier.fillMaxSize()) {
            val stack by pageRouter.getPlayStack().collectAsState(pageRouter.getBackStack().value)
            val last = (stack.last() as PageEntry).also {
                if (isLocalPageEntry) it.transformState.value = Resume
            }

            if (stack.size == 1)
                (stack.first() as PageEntry).transformState.value = Resume
            else
                last.shareTransform(stack.first() as PageEntry)
            stack.forEach { it.Content(Modifier) }

            if (stack.size == 2)
                ShareEleController.initShare(stack.first() as PageEntry, stack.last() as PageEntry)

        }
    }

    /**
     * 处理生命周期事件
     */
    internal fun handleLifecycleEvent(event: Lifecycle.Event) {
        val pageEntry = (pageRouter.backStack.findTopEntry() as PageEntry)
        when (event) {
            Lifecycle.Event.ON_START -> pageEntry.start()
            Lifecycle.Event.ON_RESUME -> pageEntry.resume()
            Lifecycle.Event.ON_PAUSE -> pageEntry.pause()
            Lifecycle.Event.ON_STOP -> pageEntry.stop()
            Lifecycle.Event.ON_DESTROY -> pageEntry.destroy()
            else -> {}
        }

    }

    override fun destroy() {
        (pageRouter.backStack.findTopEntry() as PageEntry).handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        pageRouter.backStack.pop()
    }
}