package com.erolc.mrouter.route.transform

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.erolc.mrouter.backstack.entry.LocalWindowScope
import kotlin.math.roundToInt


/**
 * 变换包裹层，在这里可以同时控制当前页面和前一个页面的变化。
 */
abstract class TransformWrap {
    internal var isUseContent = false
    internal var pauseModifierPost = PauseModifierPost { prevPauseModifier() }
    internal var gestureModifier = PauseModifierPost { Modifier }

    private val scope = TransformWrapScope()

    /**
     * 这是内容部分，应当被包裹的部分，必须调用
     */
    private var content: @Composable () -> Unit = {}
        get() {
            isUseContent = true
            return field
        }

    /**
     * 页面内容其中的content就是注册的compose
     */
    @Composable
    fun PageContent(modifier: Modifier) {
        Surface(
            modifier.clickable(MutableInteractionSource(), null) { }.fillMaxSize(),
            color = Color.Transparent
        ) {
            CompositionLocalProvider(LocalTransformWrapScope provides scope) {
                content()
            }
        }
    }

    @Composable
    fun getProgress(draggableProgress: Float): Float {
        var state by scope.progress
        state = draggableProgress
        return state
    }

    /**
     * 用于包裹[PageContent]和手势操作的
     * @param progress 进度，当产生手势操作时务必改变进度，以便更新界面，该进度为关闭页面进度，范围是[0-1]。可参考[NormalTransformWrap]和[ModalTransformWrap]
     */
    @Composable
    abstract fun Wrap(modifier: Modifier, progress: (Float) -> Unit)

    /**
     * 前一个页面在暂停时的modifier,用于控制在跳转过程中，上一个页面的页面变化
     */
    @Composable
    open fun prevPauseModifier(): Modifier = Modifier

    internal fun setContent(content: @Composable () -> Unit) {
        this.content = content
    }

    internal class PauseModifierPost(private val body: @Composable () -> Modifier) {
        @Composable
        fun getModifier(): Modifier = body()
    }

    internal fun updatePauseModifier(pauseModifierPost: PauseModifierPost) {
        this.gestureModifier = pauseModifierPost
    }

    internal fun releasePauseModifier() {
        pauseModifierPost = PauseModifierPost { prevPauseModifier() }
    }
}

/**
 * 由拖拽手势生成的两个modifier，具体可参考[ModalTransformWrap]和[NormalTransformWrap]
 * 该方法有一定的局限性，如果需要不同的手势操作请自行实现
 * @param orientation 方向
 * @param progress 进度
 * @param proportion 比例
 * @param paddingValue padding的值，根据方向的不同，padding将放置在不同的地方，且当该值设置是[proportion]将失效
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransformWrap.rememberDraggableModifier(
    orientation: Orientation = Orientation.Horizontal,
    progress: (Float) -> Unit,
    proportion: Float = 1f,
    paddingValue: Dp? = null
): Pair<Modifier, Modifier> {
    val squareSize = with(LocalWindowScope.current.windowSize.value) {
        if (orientation == Orientation.Horizontal) width.size else height.size
    }
    val padding = paddingValue ?: ((1 - proportion) * squareSize)

    val max = with(LocalDensity.current) {
        squareSize.toPx() - padding.toPx()
    }

    val anchorsDraggableState = rememberAnchoredDraggableState(0f, DraggableAnchors {
        1f at max
        0f at 0f
    })

    val offset = anchorsDraggableState.requireOffset()

    val offsetProgress = getProgress(offset / max) //0-1
    remember(offsetProgress) {
        //1-postExit;0-resume
        progress(offsetProgress)
    }
    val modifier =
        if (orientation == Orientation.Horizontal)
            Modifier.fillMaxHeight().width(15.dp)
        else
            Modifier.fillMaxWidth().height(15.dp)

    return modifier.anchoredDraggable(
        state = anchorsDraggableState,
        orientation = orientation,
    ) to Modifier.padding(
        top = if (orientation == Orientation.Vertical) padding else 0.dp,
        start = if (orientation == Orientation.Horizontal) padding else 0.dp
    ).offset {
        if (orientation == Orientation.Vertical)
            IntOffset(0, (max * offsetProgress).roundToInt())
        else
            IntOffset((max * offsetProgress).roundToInt(), 0)
    }
}


/**
 * @param initialValue 初始值
 * @param animationSpec 动画spec
 * @param positionalThreshold 位置阈值，到哪里放手之后就会自动执行动画
 * @param velocityThreshold 速度阈值，当速度到达每秒/px的时候就会执行动画，就算位置阈值还没达到
 * @param confirmValueChange 根据完成时的值决定是否执行动画，如果是false，那么将复原
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T : Any> rememberAnchoredDraggableState(
    initialValue: T,
    anchors: DraggableAnchors<T>,
    animationSpec: AnimationSpec<Float> = spring(),
    positionalThreshold: (distance: Float) -> Float = { it * 0.5f },
    velocityThreshold: () -> Float = { 10f },
    confirmValueChange: (T) -> Boolean = { true },
): AnchoredDraggableState<T> {
    return rememberSaveable(
        saver = AnchoredDraggableState.Saver(
            animationSpec,
            positionalThreshold,
            velocityThreshold,
            confirmValueChange
        )
    ) {
        AnchoredDraggableState(
            initialValue,
            anchors,
            positionalThreshold,
            velocityThreshold,
            animationSpec,
            confirmValueChange
        )
    }
}


