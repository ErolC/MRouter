package com.erolc.mrouter.utils

import androidx.compose.animation.core.*
import androidx.compose.ui.Modifier
import com.erolc.mrouter.backstack.PageEntry
import com.erolc.mrouter.backstack.StackEntry
import androidx.compose.runtime.*
import com.erolc.mrouter.route.PageRouter
import com.erolc.mrouter.route.transform.exitFinished
import com.erolc.mrouter.scope.LocalPageScope


fun List<StackEntry>.isBack(current: List<StackEntry>): Boolean {
    if (isEmpty()) return false
    return if (current.size < this.size) true
    else {
        val last = current.last()
        val first = first()
        last.address.path == first.address.path
    }
}

fun List<StackEntry>.equalsWith(current: List<StackEntry>): Boolean {
    return firstOrNull()?.address?.path == current.firstOrNull()?.address?.path && size == current.size
}

fun List<StackEntry>.isInit(current: List<StackEntry>): Boolean {
    return equalsWith(current)
}

enum class PageState {
    Close,
    Init,
    Open,
}

/**
 * 描述界面变换的状态，以及进度。
 */
sealed class TransformState(open val progress: Float)

/**
 * 入场前
 */
internal data object PreEnter : TransformState(0f)

/**
 * 退出状态/结束状态；代表一个页面已经消亡，即将脱离compose树。
 */
internal data object PostExit : TransformState(0f)

/**
 * 显示状态，visibility
 */
internal data object Resume : TransformState(1f)

/**
 * 暂停状态；代表后一个页面已经显示了，当前界面已经变为前一个页面，该页面暂停
 */
internal data object PauseState : TransformState(0f)

/**
 * 过渡状态，上述三种状态的中间态，该数据类的使用一般在在于手势。
 */
internal data class TransitionState(override val progress: Float) : TransformState(progress)

/**
 * 只能用在页面切换中，这里之所以使用List<StackEntry>是由于我希望的是在一次显示中不单单只有当前界面，还会有当前页面的前一个页面，哪怕前一个页面完全被遮挡，
 * 因为有些场景下，前一个页面不会被完全遮挡掉的，比如modal，比如加上了手势操作之后的返回。在手势操作的返回下，就必须存在两个页面，否则拉开当前界面时，下面就是window的背景。
 */
@Composable
fun Transition<List<StackEntry>>.PageTransform(pageRouter: PageRouter, modifier: Modifier = Modifier) {

    val pageState = remember(targetState, currentState) {
        if (currentState.isInit(targetState)) PageState.Init
        else if (currentState.isBack(targetState)) PageState.Close else PageState.Open
    }

    when (pageState) {
        PageState.Init -> {
            val resume = currentState.last() as PageEntry
            if (currentState.size == 2) {
                val current = currentState.first() as PageEntry
                val state by remember { resume.transformState }
                current.run {
                    Content(modifier)
                    when (state) {
                        Resume -> transformState.value = PauseState
                        is TransitionState -> transformState.value = TransitionState(1f - state.progress)
                        PostExit -> transformState.value = Resume
                        else -> {}
                    }
                }
            }

            resume.run {
                //如果只有一个页面,且是退出状态，那么就是初始界面
                if (currentState.size == 1 && transformState.value == PreEnter)
                    transformState.value = Resume
                scope.rememberTransition().apply {
                    if (exitFinished)
                        pageRouter.backStack.pop()
                }
                Content(modifier)
                //先显示后设置状态是为了产生动画
                if (transformState.value == PreEnter)
                    transformState.value = Resume
            }
        }

        PageState.Close -> currentState.first().Content(modifier)
        PageState.Open -> {
            val current = targetState.first() as PageEntry
            val resume = targetState.last() as PageEntry
            current.transform.value = current.transform.value.copy(prev = resume.transform.value.prev)
            current.Content(modifier)
            resume.Content(modifier)
        }
    }
}


@OptIn(ExperimentalTransitionApi::class)
@Composable
fun <T> rememberTransformWithChild(
    label: String,
    transformToChildState: @Composable (parentState: TransformState) -> T
): Transition<T> {
    val pageScope = LocalPageScope.current
    return pageScope.rememberTransition().createChildTransition(label, transformToChildState)
}

@Composable
fun rememberTransform(): Transition<TransformState> {
    val pageScope = LocalPageScope.current
    return pageScope.rememberTransition()
}

