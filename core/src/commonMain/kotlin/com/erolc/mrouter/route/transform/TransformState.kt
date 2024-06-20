package com.erolc.mrouter.route.transform

import androidx.compose.animation.VectorConverter
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
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
data object StopState : TransformState(0f)

/**
 * 从[ResumeState]到[ExitState]的中间态，用于手势过程，代表正在退出
 */
@Immutable
data class ExitingState(override val progress: Float) : TransformState(progress)

/**
 * 从[StopState]到[ResumeState]的中间态,用于手势过程，代表正在暂停
 */
@Immutable
data class StoppingState(override val progress: Float) : TransformState(progress)

/**
 * 获取变换的transition
 */
@Composable
fun rememberTransformState(): Transition<TransformState> {
    val pageScope = LocalPageScope.current
    return pageScope.getTransformState() ?: updateTransition(ResumeState)
}

/**
 * 该方法用于计算页面状态变化时各种数值的流转变化
 * @param resume resume时的数值
 * @param exit exit时的数值
 * @param enter enter时的数值
 * @param pause pause时的数值
 */
fun TransformState.between(
    resume: Float,
    exit: Float,
    enter: Float = exit,
    pause: Float = resume
): Float {
    return when (this) {
        EnterState -> enter
        ResumeState -> resume
        ExitState -> exit
        StopState -> pause
        is ExitingState -> exit - (exit - resume) * progress
        is StoppingState -> pause - (pause - resume) * progress
    }
}


fun TransformState.between(
    resume: Int,
    exit: Int,
    enter: Int = exit,
    pause: Int = resume
) = between(resume.toFloat(), exit.toFloat(), enter.toFloat(), pause.toFloat()).toInt()

fun TransformState.between(resume: Dp, exit: Dp, enter: Dp = exit, pause: Dp = resume) =
    between(resume.value, exit.value, enter.value, pause.value).dp

fun TransformState.between(resume: Rect, exit: Rect, enter: Rect = exit, pause: Rect = resume) =
    Rect(
        between(resume.left, exit.left, enter.left, pause.left),
        between(resume.top, exit.top, enter.top, pause.top),
        between(resume.right, exit.right, enter.right, pause.right),
        between(resume.bottom, exit.bottom, enter.bottom, pause.bottom)
    )

fun TransformState.between(
    resume: Offset,
    exit: Offset,
    enter: Offset = exit,
    pause: Offset = resume
) = Offset(
    between(resume.x, exit.x, enter.x, pause.x),
    between(resume.y, exit.y, enter.y, pause.y),
)


fun TransformState.between(
    resume: IntOffset,
    exit: IntOffset,
    enter: IntOffset = exit,
    pause: IntOffset = resume
) = IntOffset(
    between(resume.x, exit.x, enter.x, pause.x),
    between(resume.y, exit.y, enter.y, pause.y),
)

fun TransformState.between(
    resume: DpOffset,
    exit: DpOffset,
    enter: DpOffset = exit,
    pause: DpOffset = resume
) = DpOffset(
    between(resume.x, exit.x, enter.x, pause.x),
    between(resume.y, exit.y, enter.y, pause.y),
)

fun TransformState.between(resume: Size, exit: Size, enter: Size = exit, pause: Size = resume) =
    Size(
        between(resume.width, exit.width, enter.width, pause.width),
        between(resume.height, exit.height, enter.height, pause.height)
    )


fun TransformState.between(
    resume: IntSize,
    exit: IntSize,
    enter: IntSize = exit,
    pause: IntSize = resume
) = IntSize(
    between(resume.width, exit.width, enter.width, pause.width),
    between(resume.height, exit.height, enter.height, pause.height)
)

fun TransformState.between(
    resume: DpSize,
    exit: DpSize,
    enter: DpSize = exit,
    pause: DpSize = resume
) = DpSize(
    between(resume.width, exit.width, enter.width, pause.width),
    between(resume.height, exit.height, enter.height, pause.height)
)


fun TransformState.between(resume: Color, exit: Color, enter: Color = exit, pause: Color = resume) =
    run {
        val converter = Color.VectorConverter(resume.colorSpace)
        val vector = converter.convertToVector
        val resume4D = vector.invoke(resume)
        val exit4D = vector.invoke(exit)
        val enter4D = vector.invoke(enter)
        val pause4D = vector.invoke(pause)
        val target = AnimationVector4D(
            between(resume4D.v1, exit4D.v1, enter4D.v1, pause4D.v1),
            between(resume4D.v2, exit4D.v2, enter4D.v2, pause4D.v2),
            between(resume4D.v3, exit4D.v3, enter4D.v3, pause4D.v3),
            between(resume4D.v4, exit4D.v4, enter4D.v4, pause4D.v4)
        )
        converter.convertFromVector.invoke(target)
    }
