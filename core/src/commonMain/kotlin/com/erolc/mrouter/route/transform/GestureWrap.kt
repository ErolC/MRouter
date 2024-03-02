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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.erolc.mrouter.backstack.LocalWindowScope
import kotlin.math.roundToInt


/**
 * 手势包裹层，是用于给页面的外部包裹一层手势
 */
abstract class GestureWrap {
    internal var isUseContent = false
    internal var pauseModifierPost = PauseModifierPost { prevPauseModifier() }
    private val scope = GestureWrapScope()

    /**
     * 这是内容部分，应当被包裹的部分，必须调用
     */
    internal var content: @Composable () -> Unit = {}
        private set
        get() {
            isUseContent = true
            return field
        }

    @Composable
    fun PageContent(modifier: Modifier) {
        Surface(modifier.clickable(MutableInteractionSource(), null) { }.fillMaxSize(), color = Color.Transparent) {
            CompositionLocalProvider(LocalGestureWrapScope provides scope) {
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
     * 用于包裹[content]和手势操作的
     * @param progress 进度，当产生手势操作时务必改变进度，以便更新界面，该进度为关闭页面进度，范围是[0-1]。
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
        this.pauseModifierPost = pauseModifierPost
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GestureWrap.rememberDraggableModifier(
    initialValue: Dp,
    orientation: Orientation = Orientation.Horizontal,
    progress: (Float) -> Unit,
    proportion: Float = 1f,
    paddingValue: Dp? = null
): Pair<Modifier, Modifier> {
    val squareSize = with(LocalWindowScope.current.windowSize.value) {
        if (orientation == Orientation.Horizontal) width.size else height.size
    }
    val size = with(LocalDensity.current) { squareSize.toPx() }
    val initial = with(LocalDensity.current) { initialValue.toPx() }
    val paddingTop = paddingValue ?: ((1 - proportion) * squareSize)
    val top = with(LocalDensity.current) { paddingTop.toPx() }
    val max = size - top
    val anchorsDraggableState = rememberAnchoredDraggableState(initial, DraggableAnchors {
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
        if (orientation == Orientation.Horizontal) Modifier.fillMaxHeight().width(15.dp) else Modifier.fillMaxWidth()
            .height(15.dp)
    return modifier
        .anchoredDraggable(
            state = anchorsDraggableState,
            orientation = orientation,
        ) to Modifier.padding(top = paddingTop)
        .offset {
            if (orientation == Orientation.Vertical) IntOffset(
                0,
                (max * offsetProgress).roundToInt()
            ) else IntOffset((max * offsetProgress).roundToInt(), 0)
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


