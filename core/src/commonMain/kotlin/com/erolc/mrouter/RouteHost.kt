package com.erolc.mrouter

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.erolc.mrouter.backstack.StackEntry
import com.erolc.mrouter.register.RegisterBuilder

@Composable
fun RouteHost(startRoute: String, builder: RegisterBuilder.() -> Unit) {
    RouteHost(remember(startRoute, builder) {
        MRouter.getMRouter(startRoute, builder)
    })
}

@Composable
fun RouteHost(router: MRouter) {
    val backStack by router.getRootBlackStack()
    backStack.forEach { it.Content() }
}


/**
 * 无手势版的页面切换
 */
@Composable
internal fun Transforms(backStacks: List<StackEntry>) {
    var size by remember { mutableStateOf(0) }
    //是否是後退
    val isBack = remember(backStacks) {
        val stackSize = backStacks.size
        val isBack = size > stackSize
        size = stackSize
        isBack
    }
    val entry = remember(backStacks) {
        backStacks.lastOrNull()
    }
    val transition = updateTransition(targetState = entry)
    //这里还需要做的就是根据isBack 去更改动画效果
    transition.AnimatedContent(transitionSpec = {
        slideInHorizontally(tween()) {
            if (isBack) -it else it
        } togetherWith slideOutHorizontally(tween()) {
            if (isBack) it else -1
        }
    }) {
        it?.Content()
    }


}


