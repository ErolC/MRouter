package com.erolc.mrouter

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.erolc.mrouter.backstack.PageEntry
import com.erolc.mrouter.backstack.StackEntry
import com.erolc.mrouter.backstack.WindowEntry
import com.erolc.mrouter.model.WindowOptions
import com.erolc.mrouter.register.RegisterBuilder
import com.erolc.mrouter.utils.log
import com.erolc.mrouter.window.WindowOptionsBuilder

/**
 * 路由起点
 * @param startRoute 是路由的第一个页面
 * @param windowOptions 是window的配置，只有桌面端才有用
 * @param builder 用于注册的构建方法。[startRoute]对应的页面需要也在其中
 */
@Composable
fun RouteHost(
    startRoute: String,
    windowOptions: WindowOptions = WindowOptions(Constants.defaultWindow, ""),
    builder: RegisterBuilder.() -> Unit
) {
    RouteHost(remember(startRoute, builder) {
        MRouter.getMRouter(startRoute, windowOptions, builder)
    })
}

@Composable
fun RouteHost(router: MRouter) {
    val backStack by router.getRootBlackStack()
    backStack.forEach {
        (it as? WindowEntry)?.Content(Modifier)
    }
}


/**
 * 无手势版的页面切换
 */
@Composable
internal fun WindowEntry.Transforms() {
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
    val transition = updateTransition(targetState = target)
    transition.AnimatedContent(transitionSpec = {
        slideInHorizontally(tween()) {
            if (isBack) -it else it
        } togetherWith slideOutHorizontally(tween()) {
            if (isBack) it else -1
        }
    }) {
        (it as? PageEntry)?.Content(Modifier)
    }

}


