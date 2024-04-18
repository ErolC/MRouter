package com.erolc.mrouter.backstack.entry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.erolc.mrouter.Constants.defaultWindow
import com.erolc.mrouter.model.WindowOptions
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.route.router.PageRouter
import com.erolc.mrouter.route.router.WindowRouter
import com.erolc.mrouter.route.shareele.ShareEleController
import com.erolc.mrouter.scope.WindowScope
import com.erolc.mrouter.route.transform.Resume
import com.erolc.mrouter.utils.PlatformWindow
import com.erolc.mrouter.utils.loge

/**
 * window域
 */
val LocalWindowScope = staticCompositionLocalOf { WindowScope() }

/**
 * 代表一个窗口，对于ios和android来说，其只有一个window，而对于desktop来说是可以有多个window的。
 * @param options window的一些配置选项
 * @param address window的地址
 */
class WindowEntry(
    val options: MutableState<WindowOptions> = mutableStateOf(WindowOptions(defaultWindow, "")),
    override val address: Address = Address(options.value.id)
) :
    StackEntry {
    //页面路由器，管理当前窗口的所有界面
    internal lateinit var pageRouter: PageRouter

    val scope = WindowScope(address.path)

    fun shouldExit(): Boolean {
        return (pageRouter.parentRouter as WindowRouter)
            .backStack.backStack.value.none { !scope.isCloseWindow.value }
    }

    @Composable
    override fun Content(modifier: Modifier) {
        CompositionLocalProvider(LocalWindowScope provides scope) {
            val options by remember(options) { options }
            PlatformWindow(options, this) {
                Box(modifier.fillMaxSize().background(Color.Black)) {
                    val stack by pageRouter.getPlayStack().collectAsState(pageRouter.getBackStack().value)
                    if (stack.size == 1)
                        (stack.first() as PageEntry).transformState.value = Resume
                    else
                        (stack.last() as PageEntry).shareTransform(stack.first() as PageEntry)

                    stack.forEach { it.Content(Modifier) }

                    if (stack.size == 2)
                        ShareEleController.initShare(stack.first() as PageEntry, stack.last() as PageEntry)

                    ShareEleController.Overlay()
                }
            }
        }
    }

    override fun destroy() {}

}