package com.erolc.mrouter.backstack

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.erolc.mrouter.Transforms
import com.erolc.mrouter.lifecycle.Lifecycle
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.model.WindowOptions
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.route.PageRouter
import com.erolc.mrouter.scope.WindowScope
import com.erolc.mrouter.utils.PlatformWindow


class WindowEntry(val options: WindowOptions, private val pageRouter: PageRouter) :
    StackEntry(WindowScope().apply { name = options.id }, Address(options.id)) {

    override val lifecycle: Lifecycle
        get() = registry

    init {
        pageRouter.windowEntry = this
    }

    /**
     * 在window内通过route获取后退栈条目
     */
    fun pageRoute(route: Route, address: Address) {
        pageRouter.run {
            route(route, address)
        }
    }

    internal fun getScope() = scope as WindowScope

    fun close(): Boolean {
        getScope().onLifeEvent(Lifecycle.Event.ON_DESTROY)
        return pageRouter.windowRouter.close(this)
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
            val (target, dialog) = remember(backStacks) {
                val target = backStacks.lastOrNull()
                if (target is DialogEntry)
                    backStacks.takeLast(2).firstOrNull() to target
                else target to null
            }

            Transforms(target, slideInHorizontally(tween()) {
                if (isBack) -it else it
            } togetherWith slideOutHorizontally(tween()) {
                if (isBack) it else -1
            })

            dialog?.Content(modifier)

//            val toastOptions by remember { getScope().toastOptions }
//            if (toastOptions != null) {
//                Toast(toastOptions!!)
//            }
        }
    }

}