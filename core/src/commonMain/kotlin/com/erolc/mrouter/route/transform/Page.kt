package com.erolc.mrouter.route.transform

import androidx.compose.animation.core.*
import androidx.compose.ui.Modifier
import com.erolc.mrouter.backstack.PageEntry
import com.erolc.mrouter.backstack.StackEntry
import androidx.compose.runtime.*
import com.erolc.mrouter.route.PageRouter
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


@OptIn(ExperimentalTransitionApi::class)
@Composable
fun <T> rememberTransformWithChild(
    label: String,
    transformToChildState: @Composable (parentState: TransformState) -> T
): Transition<T> {
    val pageScope = LocalPageScope.current
    return pageScope.rememberTransform().createChildTransition(label, transformToChildState)
}

@Composable
fun rememberTransform(): Transition<TransformState> {
    val pageScope = LocalPageScope.current
    return pageScope.rememberTransform()
}

