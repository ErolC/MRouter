package com.erolc.mrouter.route.transform

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import com.erolc.mrouter.scope.LocalPageScope

/**
 * 描述界面变换的状态，以及进度。需要注意的是[TransformState]类簇仅描述界面变换，不代表页面生命周期。
 */
@Immutable
sealed class TransformState(open val progress: Float)

/**
 * 进入状态，表示页面即将进入，是Transform的开始
 */
@Immutable
data object EnterState : TransformState(0f)

/**
 * 退出状态/结束状态；transform的结束
 */
@Immutable
data object ExitState : TransformState(0f)

/**
 * 显示状态
 */
@Immutable
data object ResumeState : TransformState(1f)

/**
 * 暂停状态；代表后一个页面已经显示了，当前界面已经变为前一个页面，该页面暂停
 */
@Immutable
data object PauseState : TransformState(0f)

/**
 * 从[ResumeState]到[ExitState]的中间态，用于手势过程，代表正在退出
 */
@Immutable
data class ExitingState(override val progress: Float) : TransformState(progress)

/**
 * 从[PauseState]到[ResumeState]的中间态,用于手势过程，代表正在暂停
 */
@Immutable
data class PausingState(override val progress: Float) : TransformState(progress)

/**
 * 获取变换的transition
 */
@Composable
fun rememberTransformTransition(): Transition<TransformState> {
    val pageScope = LocalPageScope.current
    return pageScope.rememberTransform() ?: updateTransition(ResumeState)
}

/**
 * 该方法用于计算resume - exit和resume - pause之间的变化关系。
 */
fun TransformState.between(resume: Float, exit: Float, pause: Float = exit): Float {
    return when (this) {
        ResumeState -> resume
        ExitState -> exit
        PauseState -> pause
        is ExitingState -> exit - (exit - resume) * progress
        is PausingState -> pause - (pause - resume) * progress
        else -> 0f // enterStat
    }
}
