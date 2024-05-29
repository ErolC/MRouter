package com.erolc.mrouter.backstack.entry

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.lifecycle.Lifecycle
import com.erolc.mrouter.platform.loge
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.route.router.PageRouter
import com.erolc.mrouter.route.shareelement.ShareElementController
import com.erolc.mrouter.route.transform.ResumeState
import com.erolc.mrouter.scope.HostScope

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
            Box(modifier.fillMaxSize().onGloballyPositioned {
                hostScope.size.value = it.boundsInRoot().size
            }) {
                val stack by pageRouter.getPlayStack()
                    .collectAsState(pageRouter.getBackStack().value.map { it as PageEntry })

                if (stack.size == 1)
                    stack.first().run {
                        transformState.value = ResumeState
                        shareTransform(null)
                    }
                else
                    stack.last().shareTransform(stack.first())

                stack.forEach { it.Content(Modifier) }

                if (stack.size == 2)
                    ShareElementController.initShare(stack.first(), stack.last())

            }

        }
    }

    /**
     * 处理生命周期事件
     */
    internal fun handleLifecycleEvent(event: Lifecycle.Event) {
        pageRouter.backStack.backStack.value.forEach {
            it as PageEntry
            it.handleHostLifecycleEvent(event)
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
}