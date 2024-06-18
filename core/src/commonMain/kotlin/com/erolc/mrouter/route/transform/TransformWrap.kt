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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.erolc.mrouter.platform.loge
import com.erolc.mrouter.scope.LocalPageScope
import com.erolc.mrouter.utils.rememberInPage


/**
 * 变换包裹层，在这里可以同时控制当前页面和前一个页面的变化。
 */
abstract class TransformWrap {
    internal var isUseContent = false
    internal var pauseModifierPost = PauseModifierPost { prevPageModifier() }
    internal var gestureModifier = PauseModifierPost { Modifier }

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
            content()
        }
    }

    /**
     * 用于包裹[PageContent]和手势操作的
     */
    @Composable
    abstract fun Wrap(modifier: Modifier)

    /**
     * 前一个页面在暂停时的modifier,用于控制在跳转过程中，上一个页面的页面变化
     */
    @Composable
    open fun prevPageModifier(): Modifier = Modifier

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
        pauseModifierPost = PauseModifierPost { prevPageModifier() }
    }
}

/**
 * 由拖拽手势生成的两个modifier，具体可参考[ModalTransformWrap]和[NormalTransformWrap]
 * 该方法有一定的局限性，如果需要不同的手势操作请自行实现
 * @param orientation 方向
 * @param reverseDirection 翻转
 * @return 应用在手势控件的Modifier，
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rememberDraggableModifier(
    orientation: Orientation = Orientation.Horizontal,
    reverseDirection: Boolean = false
): Modifier {
    val scope = LocalTransformWrapScope.current
    val anchoredDraggableState = scope.run {
        rememberDraggableState(progress, orientation)
    }
    val modifier =
        if (orientation == Orientation.Horizontal)
            Modifier.width(15.dp)
        else
            Modifier.fillMaxWidth().height(15.dp)

    val shouldHasGesture = shouldHasGesture()
    return if (shouldHasGesture) modifier
        .nestedScroll(nestScrollConnection(orientation, anchoredDraggableState))
        .anchoredDraggable(
            state = anchoredDraggableState,
            orientation = orientation,
            reverseDirection = reverseDirection
        ) else modifier
}

/**
 * 将anchor的拖拽手势加入到嵌套滑动系统中
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun nestScrollConnection(
    orientation: Orientation = Orientation.Horizontal,
    anchoredDraggableState: AnchoredDraggableState<Float>
): NestedScrollConnection {
    var shouldDraggable by rememberInPage("should_draggable") {
        mutableStateOf(false)
    }
    return rememberInPage("modal_nest_conn") {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val (data, result) = getConsumed(available, orientation)

                if (shouldDraggable) {
                    anchoredDraggableState.dispatchRawDelta(data)
                    return result
                }
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val (data, result) = getConsumed(available, orientation)
                if (data > 0) {
                    anchoredDraggableState.dispatchRawDelta(data)
                    shouldDraggable = true
                    return result
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                anchoredDraggableState.settle(available.y)
                shouldDraggable = false
                return super.onPreFling(available)
            }
        }
    }
}

private fun getConsumed(available: Offset, orientation: Orientation) =
    if (orientation == Orientation.Vertical)
        available.y to Offset(0f, available.y)
    else
        available.x to Offset(available.x, 0f)

/**
 * 是否应该有手势
 */
@Composable
fun shouldHasGesture() = !LocalPageScope.current.router.parentRouter.backStack.isBottom()


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rememberEndDraggableModifier(
    orientation: Orientation = Orientation.Horizontal,
    reverseDirection: Boolean = false
): Modifier {
    val scope = LocalTransformWrapScope.current
    val anchoredDraggableState = scope.run {
        rememberEndDraggableState(progress, orientation)
    }
    val modifier =
        if (orientation == Orientation.Horizontal)
            Modifier.width(15.dp)
        else
            Modifier.fillMaxWidth().height(15.dp)

    return modifier.anchoredDraggable(
        state = anchoredDraggableState,
        orientation = orientation,
        reverseDirection = reverseDirection
    )
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
    any: Any,
    initialValue: T,
    anchors: DraggableAnchors<T>,
    animationSpec: AnimationSpec<Float> = spring(),
    positionalThreshold: (distance: Float) -> Float = { it * 0.5f },
    velocityThreshold: () -> Float = { 10f },
    confirmValueChange: (T) -> Boolean = { true },
): AnchoredDraggableState<T> {
    return rememberSaveable(
        any, anchors,
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