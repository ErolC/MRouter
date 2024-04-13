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
import com.erolc.mrouter.route.shareele.LocalShareEleController
import com.erolc.mrouter.route.shareele.ShareEleController
import com.erolc.mrouter.scope.WindowScope
import com.erolc.mrouter.route.transform.Resume
import com.erolc.mrouter.utils.PlatformWindow
import com.erolc.mrouter.utils.loge

val LocalWindowScope = staticCompositionLocalOf { WindowScope() }

class WindowEntry(
    val options: MutableState<WindowOptions> = mutableStateOf(WindowOptions(defaultWindow, "")),
    override val address: Address = Address(options.value.id)
) :
    StackEntry {
    internal lateinit var pageRouter: PageRouter

    val scope = WindowScope()

    fun shouldExit(): Boolean {
        return (pageRouter.parentRouter as WindowRouter)
            .backStack.backStack.value.none { !scope.isCloseWindow.value }
    }

    @Composable
    override fun Content(modifier: Modifier) {
        val controller = ShareEleController
        CompositionLocalProvider(LocalWindowScope provides scope, LocalShareEleController provides controller) {
            val options by remember(options) { options }
            PlatformWindow(options, this) {
                Box(modifier.fillMaxSize().background(Color.Black)) {
                    val stack by pageRouter.getPlayStack().collectAsState(pageRouter.getBackStack().value)
                    if (stack.size == 1)
                        (stack.first() as PageEntry).transformState.value = Resume
                    else
                        (stack.last() as PageEntry).shareTransform(stack.first() as PageEntry)

                    stack.forEach { stackEntry ->
                        stackEntry.Content(Modifier)
                    }
                    if (stack.size == 2)
                    ShareEleController.initShare(stack.first() as PageEntry, stack.last() as PageEntry)
                    controller.Overlay()
                }
            }
        }
    }

    override fun destroy() {}

}