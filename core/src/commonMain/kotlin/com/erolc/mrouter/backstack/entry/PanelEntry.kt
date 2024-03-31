package com.erolc.mrouter.backstack.entry

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.erolc.lifecycle.Lifecycle
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.route.router.PageRouter
import com.erolc.mrouter.route.router.PanelRouter
import com.erolc.mrouter.route.transform.PauseState
import com.erolc.mrouter.route.transform.Resume
import com.erolc.mrouter.scope.LocalPageScope
import com.erolc.mrouter.scope.default
import com.erolc.mrouter.utils.loge
import com.erolc.mrouter.utils.logi

/**
 * 局部界面的元素
 * 局部界面的后退需要注意的是，如果局部路由已经是最后一个后退点，那么将无法再后退。
 * 这里将出现两种情况：开放用户设置可在无法后退时退出当前界面，2，不开放直接可后退。
 * 还需要考虑面板在界面中的显示比例问题。需要做到当面板消失时，剩余部分可以沾满整个界面，可能需要自定义layout
 */
class PanelEntry(override val address: Address) : StackEntry {
    lateinit var pageRouter: PageRouter

    @Composable
    override fun Content(modifier: Modifier) {
        Box(modifier.fillMaxSize()) {
            val stack by pageRouter.getPlayStack().collectAsState(pageRouter.getBackStack().value)
            if (stack.size == 1) {
                (stack.first() as PageEntry).transformState.value = Resume
            } else
                (stack.last() as PageEntry).shareTransform(stack.first() as PageEntry)

            stack.forEach { stackEntry ->
                stackEntry.Content(Modifier)
            }
        }
    }

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