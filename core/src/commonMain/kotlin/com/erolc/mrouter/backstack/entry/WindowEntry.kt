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
import com.erolc.mrouter.route.PageRouter
import com.erolc.mrouter.route.WindowRouter
import com.erolc.mrouter.scope.WindowScope
import com.erolc.mrouter.route.transform.Resume
import com.erolc.mrouter.utils.PlatformWindow

val LocalWindowScope = staticCompositionLocalOf { WindowScope() }

class WindowEntry(val options:MutableState<WindowOptions> = mutableStateOf(WindowOptions(defaultWindow,""))) :
    StackEntry(WindowScope().apply { name = options.value.id }, Address(options.value.id)) {
    internal lateinit var pageRouter: PageRouter

    internal fun getScope() = scope as WindowScope

    fun shouldExit(): Boolean {
        return (pageRouter.parentRouter as WindowRouter)
            .backStack.backStack.value.none { !(it.scope as WindowScope).isCloseWindow.value }
    }

    @Composable
    override fun Content(modifier: Modifier) {
        CompositionLocalProvider(LocalWindowScope provides getScope()) {
            val options by remember(options) { options }
            PlatformWindow(options, this) {
                Box(modifier.fillMaxSize().background(Color.Black)) {
                    val stack by pageRouter.getPlayStack().collectAsState(pageRouter.getBackStack().value)
                    if (stack.size == 1) {
                        (stack.first() as PageEntry).transformState.value = Resume
                    } else
                        (stack.last() as PageEntry).shareTransform(stack.first() as PageEntry)

                    stack.forEachIndexed { index, stackEntry ->
                        (stackEntry as PageEntry).run {
                            if (index == 0 && stack.size == 2) pause(true)
                            else {
                                isSecond.value = false
                            }
                            Content(Modifier)
                        }
                    }
                }
            }
        }
    }

}