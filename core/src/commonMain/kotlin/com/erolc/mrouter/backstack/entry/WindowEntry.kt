package com.erolc.mrouter.backstack.entry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.erolc.mrouter.Constants.DEFAULT_WINDOW
import com.erolc.mrouter.model.WindowOptions
import com.erolc.mrouter.model.Address
import com.erolc.mrouter.route.router.PageRouter
import com.erolc.mrouter.route.router.WindowRouter
import com.erolc.mrouter.route.shareelement.Overlay
import com.erolc.mrouter.route.shareelement.ShareElementController
import com.erolc.mrouter.scope.WindowScope
import com.erolc.mrouter.route.transform.ResumeState
import com.erolc.mrouter.platform.PlatformWindow
import com.erolc.mrouter.scope.HostScope
import com.erolc.mrouter.utils.HostContent

/**
 * window域
 */
val LocalWindowScope = staticCompositionLocalOf { WindowScope() }

internal val LocalHostScope = compositionLocalOf { HostScope() }

/**
 * 代表一个窗口，对于ios和android来说，其只有一个window，而对于desktop来说是可以有多个window的。
 * @param options window的一些配置选项
 * @param address window的地址
 */
class WindowEntry(
    val options: MutableState<WindowOptions> = mutableStateOf(WindowOptions(DEFAULT_WINDOW, "")),
    override val address: Address = Address(options.value.id)
) :
    StackEntry {
    //页面路由器，管理当前窗口的所有界面
    internal lateinit var pageRouter: PageRouter

    val scope = WindowScope(address.path)
    internal val hostScope = HostScope()

    fun shouldExit(): Boolean {
        return (pageRouter.parentRouter as WindowRouter)
            .backStack.backStack.value.none {
                it as WindowEntry
                val isClose by it.scope.isCloseWindow
                !isClose
            }
    }

    fun dispatchOnAddressChange() {
        pageRouter.dispatchOnAddressChange()
    }

    @Composable
    override fun Content(modifier: Modifier) {
        CompositionLocalProvider(
            LocalWindowScope provides scope,
            LocalHostScope provides hostScope
        ) {
            val options by remember(options) { options }
            PlatformWindow(options, this) {
                val lifecycleOwner = LocalLifecycleOwner.current
                pageRouter.HostContent(modifier.background(Color.Black), hostScope, lifecycleOwner) {
                    ShareElementController.Overlay()
                }
            }
        }
    }

    override fun destroy() {}

}