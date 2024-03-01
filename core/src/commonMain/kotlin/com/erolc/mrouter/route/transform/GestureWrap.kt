package com.erolc.mrouter.route.transform

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.asStateFlow


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
    fun rememberProgress(draggableProgress: Float): Float {
        val state by scope.progress.collectAsState()
        LaunchedEffect(draggableProgress) {
                scope.progress.emit(draggableProgress)
        }
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

    internal class PauseModifierPost(private val body: @Composable () -> Modifier) {
        @Composable
        fun getModifier(): Modifier = body()
    }

    internal fun updatePauseModifier(pauseModifierPost: PauseModifierPost) {
        this.pauseModifierPost = pauseModifierPost
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
    targetValue: T,
    anchors: DraggableAnchors<T>,
    animationSpec: AnimationSpec<Float> = spring(),
    positionalThreshold: (distance: Float) -> Float = { it * 0.5f },
    velocityThreshold: () -> Float = { 10f },
    confirmValueChange: (T) -> Boolean = { true },
): AnchoredDraggableState<T> {
    return rememberSaveable(
        targetValue,
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


