package com.erolc.mrouter.route.transform

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import com.erolc.mrouter.scope.LocalPageScope

/**
 * 描述界面变换的状态，以及进度。
 */
@Immutable
sealed class TransformState(open val progress: Float)

/**
 * 入场前
 */
@Immutable
internal data object PreEnter : TransformState(0f)

/**
 * 退出状态/结束状态；代表一个页面已经消亡，即将脱离compose树。
 */
@Immutable
internal data object PostExit : TransformState(0f)

/**
 * 显示状态，visibility
 */
@Immutable
internal data object Resume : TransformState(1f)

/**
 * 暂停状态；代表后一个页面已经显示了，当前界面已经变为前一个页面，该页面暂停
 */
@Immutable
internal data object PauseState : TransformState(0f)

/**
 * 过渡状态，从[Resume]到[PostExit]
 */
@Immutable
internal data class TransitionState(override val progress: Float) : TransformState(progress)
/**
 * 逆向过渡状态，从[PauseState]到[Resume],所谓逆向是相对于[TransitionState]而言
 */
@Immutable
internal data class Reverse(override val progress: Float) : TransformState(progress)

/**
 * 获取变换的transition
 */
@Composable
fun rememberTransformTransition(): Transition<TransformState> {
    val pageScope = LocalPageScope.current
    return pageScope.rememberTransform() ?: updateTransition(Resume)
}

