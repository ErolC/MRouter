package com.erolc.mrouter

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.erolc.mrouter.backstack.DialogEntry
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
    backStack.forEach { it.Content(Modifier) }
}


/**
 * 无手势版的页面切换
 */
@Composable
internal fun Transforms(target:StackEntry?,transform:ContentTransform) {


    val transition = updateTransition(targetState = target)
    //这里还需要做的就是根据isBack 去更改动画效果
    transition.AnimatedContent(transitionSpec = { transform }) {
        it?.Content()
    }



}


