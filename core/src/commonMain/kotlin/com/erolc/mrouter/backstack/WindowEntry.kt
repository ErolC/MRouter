package com.erolc.mrouter.backstack

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.erolc.mrouter.Transforms
import com.erolc.lifecycle.Lifecycle
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.model.WindowOptions
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.route.PageRouter
import com.erolc.mrouter.route.WindowRouter
import com.erolc.mrouter.scope.WindowScope
import com.erolc.mrouter.scope.default
import com.erolc.mrouter.utils.PlatformWindow
import com.erolc.mrouter.utils.log

val LocalWindowScope = staticCompositionLocalOf { WindowScope() }

class WindowEntry(internal var options: WindowOptions) :
    StackEntry(WindowScope().apply { name = options.id }, Address(options.id)) {
    internal lateinit var pageRouter: PageRouter

    internal val isCloseWindow = mutableStateOf(false)

    init {
        getScope().onClose = { close() }
    }

    internal fun getScope() = scope as WindowScope

    fun close(): Boolean {
        getScope().onLifeEvent(Lifecycle.Event.ON_DESTROY)
        val isExit = (pageRouter.parentRouter as WindowRouter).close(this)
        isCloseWindow.value = false
        return isExit
    }

    @Composable
    override fun Content(modifier: Modifier) {
        PlatformWindow(options, this) {
            val backStacks by pageRouter.getBackStack().collectAsState()
            var size by remember { mutableStateOf(0) }
            //是否是後退
            val isBack = remember(backStacks) {
                val stackSize = backStacks.size
                val isBack = size > stackSize
                size = stackSize
                isBack
            }
            val target = remember(backStacks) {
                backStacks.lastOrNull()
            }
            CompositionLocalProvider(LocalWindowScope provides getScope()) {
                Transforms(target, slideInHorizontally(tween()) {
                    if (isBack) -it else it
                } togetherWith slideOutHorizontally(tween()) {
                    if (isBack) it else -1
                })
            }
        }
    }

}