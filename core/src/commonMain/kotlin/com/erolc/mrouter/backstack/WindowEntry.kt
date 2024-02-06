package com.erolc.mrouter.backstack

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    internal fun getScope() = scope as WindowScope

    fun close(): Boolean {
         (pageRouter.parentRouter as WindowRouter).close(this)
        return shouldExit()
    }

    fun shouldExit(): Boolean {
        return (pageRouter.parentRouter as WindowRouter).backStack.isBottom()
    }

    @Composable
    override fun Content(modifier: Modifier) {
        CompositionLocalProvider(LocalWindowScope provides getScope()) {
            PlatformWindow(options, this) {
                Box(modifier.fillMaxSize().background(Color.Black)){
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
                    Transforms(target, slideInHorizontally(tween()) {
                        if (isBack) -it else it
                    } togetherWith slideOutHorizontally(tween()) {
                        if (isBack) it else -1
                    })
                }
            }
        }
    }

}