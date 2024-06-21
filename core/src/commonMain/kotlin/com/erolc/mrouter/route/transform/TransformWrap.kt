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
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.erolc.mrouter.platform.GestureContent
import com.erolc.mrouter.scope.LocalPageScope
import com.erolc.mrouter.utils.rememberInPage


/**
 * 变换包裹层，在这里可以同时控制当前页面和前一个页面的变化。
 */
abstract class TransformWrap(private val gestureModel: GestureModel = GestureModel.Local) {
    internal var isUseContent = false
    internal var pauseModifierPost = PauseModifierPost { prevPageModifier() }
    internal var gestureModifier = PauseModifierPost { Modifier }

    fun matchModifier(modifier: Modifier, gestureModifier: Modifier) = when (gestureModel) {
        GestureModel.Local, GestureModel.None -> modifier
        else -> modifier then gestureModifier

    }

    @Composable
    fun BoxScope.Gesture(gestureModifier: Modifier) {
        when (gestureModel) {
            GestureModel.Full, GestureModel.None -> {}
            else -> GestureContent(gestureModifier)
        }
    }

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
private fun nestScrollConnection(
    orientation: Orientation = Orientation.Horizontal,
    anchoredDraggableState: AnchoredDraggableState<Float>
): NestedScrollConnection {
    return rememberInPage("modal_nest_conn") {
        object : NestedScrollConnection {
            var canDigestion = false

            //计算我能消费多少，然后返回给子控件的是我消费了多少
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                //这里并不是计算我能消费多少，而是我优先消费，我如果不消费则是让子控件消费
                if (source != NestedScrollSource.Drag)
                    return Offset.Zero
                val myFirst =
                    if (orientation == Orientation.Vertical) available.y < 0 else available.x < 0
                return if (canDigestion && myFirst)
                    getConsumed(orientation, anchoredDraggableState, available)
                else Offset.Zero
            }

            //子控件已经消化完了，这时我再计算我能消费多少，然后返回给子控件的是我消费了多少
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (source != NestedScrollSource.Drag)
                    return Offset.Zero
                canDigestion = consumed != Offset.Zero
                return if (canDigestion) {
                    getConsumed(orientation, anchoredDraggableState, available)
                } else {
                    super.onPostScroll(consumed, available, source)
                }
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                anchoredDraggableState.settle(if (orientation == Orientation.Vertical) available.y else available.x)
                return Velocity.Zero
            }

            @OptIn(ExperimentalFoundationApi::class)
            fun getConsumed(
                orientation: Orientation,
                anchoredDraggableState: AnchoredDraggableState<Float>,
                available: Offset
            ) =
                when (orientation) {
                    Orientation.Vertical -> {
                        val y = anchoredDraggableState.dispatchRawDelta(available.y)
                        Offset(available.x, y)
                    }

                    else -> {
                        val x = anchoredDraggableState.dispatchRawDelta(available.x)
                        Offset(x, available.y)
                    }
                }
        }
    }
}


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


sealed interface GestureModel {

    /**
     * 全面手势，在页面任何地方都可使用手势，请页面内元素做好滑动冲突
     */
    data object Full : GestureModel

    /**
     * 局部手势，在特定区域才能使用的手势，一般来说向下滑动的那么手势将在页面上方高占位15dp，向右滑动的则在页面左侧
     */
    data object Local : GestureModel

    /**
     * 两者兼容，该模式下上述的两个区域同时都可使用
     */
    data object Both : GestureModel

    /**
     * 无手势，该模式下将无任何手势
     */
    data object None : GestureModel
}

