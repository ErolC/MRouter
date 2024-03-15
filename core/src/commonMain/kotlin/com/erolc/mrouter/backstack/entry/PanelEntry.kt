package com.erolc.mrouter.backstack.entry

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.route.router.PageRouter
import com.erolc.mrouter.route.transform.Resume
import com.erolc.mrouter.utils.loge

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
        PanelContent(modifier)
    }

    @Composable
    fun PanelContent(modifier: Modifier = Modifier) {
        Box(modifier.fillMaxSize()) {
            val stack by pageRouter.getPlayStack().collectAsState(pageRouter.getBackStack().value)
            if (stack.size == 1) {
                (stack.first() as PageEntry).transformState.value = Resume
            } else
                (stack.last() as PageEntry).shareTransform(stack.first() as PageEntry)

            stack.forEachIndexed { index, stackEntry ->
                (stackEntry as PageEntry).run {
                    if (index == 0 && stack.size == 2) pause(true)
                    Content(Modifier)
                }
            }
        }
    }

    override fun destroy() {
        pageRouter.backStack.pop()
    }
}