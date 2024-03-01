package com.erolc.mrouter.route.transform

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import com.erolc.mrouter.scope.LocalPageScope

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
fun <T> createChildTransform(
    label: String = "ChildTransition",
    transformToChildState: @Composable (parentState: TransformState) -> T
): Transition<T> =
    rememberTransform().createChildTransition(label, transformToChildState)


@Composable
fun rememberTransform(): Transition<TransformState> {
    val pageScope = LocalPageScope.current
    return pageScope.rememberTransform() ?: updateTransition(Resume)
}

