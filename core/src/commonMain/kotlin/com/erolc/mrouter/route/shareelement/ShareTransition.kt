package com.erolc.mrouter.route.shareelement

import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.animateIntOffset
import androidx.compose.animation.core.animateIntSize
import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.animateRect
import androidx.compose.animation.core.animateSize
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.erolc.mrouter.model.ShareElement
import com.erolc.mrouter.utils.ShareAnim
import com.erolc.mrouter.utils.ShareAnimBody
import com.erolc.mrouter.utils.ShareState
import com.erolc.mrouter.utils.Sharing
import com.erolc.mrouter.utils.updateValue

/**
 * 共享过渡，用于描述共享过程中共享元素里的元素的状态变化
 * @param startElement 开始的元素
 * @param endElement 结束的元素
 * @param targetElement 目标的元素，指代用于显示共享过程的元素，是前面两者其中之一
 * @param transition 共享元素过渡状态
 */
class ShareTransition(
    internal val startElement: ShareElement,
    internal val endElement: ShareElement,
    internal var targetElement: ShareElement,
    internal val transition: Transition<ShareState>
) {

    /**
     * 获取样式，从两个共享元素的样式列表中获取指定[index]的样式数据，结合共享过程生成连续且平滑的样式。
     * @param index 共享元素样式列表的index
     * @param sharing 共享过程是可以使用手势的，该方法将描述手势进度和该样式的关系。对于普通的数据类型，可以直接使用::sharing即可。
     * @param anim 动画，样式变化的过程。使用对应的animate方法即可
     */
    @Composable
    fun <T> getStyle(
        index: Int,
        sharing: Sharing.(preValue: T, currentValue: T) -> T = { _, current -> current },
    ) = ShareTransitionJoin(this, index, sharing)

    @Composable
    fun <T> getStyle(
        index: Int,
        sharing: Sharing.(preValue: T, currentValue: T) -> T = { _, current -> current },
        animate: ShareAnimBody<T>,
    ) = styleCore(index, sharing, animate)


    @Composable
    internal fun <T> styleCore(
        index: Int,
        sharing: Sharing.(preValue: T, currentValue: T) -> T = { _, current -> current },
        anim: ShareAnimBody<T>
    ): State<T> {
        val current = targetElement.styles.getOrNull(index) as? T
        require(current != null) {
            "请检查${targetElement.tag}的样式列表，无法在位置${index}找到值"
        }
        if (startElement == targetElement && endElement == targetElement) {
            return mutableStateOf(current)
        }
        val isStart = startElement == targetElement
        val (pre, next) = if (isStart)
            current to endElement.styles.getOrNull(index) as? T
        else
            startElement.styles.getOrNull(index) as? T to current
        return updateValue(pre ?: current, current, next ?: current, sharing) {
            anim(it)
        }
    }

    /**
     * dp的动画过程，用于[getStyle]方法
     */
    @Composable
    fun animateDp(
        transitionSpec: @Composable Transition.Segment<ShareState>.() -> FiniteAnimationSpec<Dp> = {
            spring(visibilityThreshold = Dp.VisibilityThreshold)
        }
    ): ShareAnimBody<Dp> {
        return {
            transition.animateDp(transitionSpec) { this(it) }
        }
    }

    @Composable
    fun animateFloat(
        transitionSpec:
        @Composable Transition.Segment<ShareState>.() -> FiniteAnimationSpec<Float> = { spring() }
    ): ShareAnimBody<Float> = { transition.animateFloat(transitionSpec) { this(it) } }


    @Composable
    fun animateInt(
        transitionSpec: @Composable Transition.Segment<ShareState>.() -> FiniteAnimationSpec<Int> = {
            spring(visibilityThreshold = Int.VisibilityThreshold)
        }
    ): ShareAnimBody<Int> = { transition.animateInt(transitionSpec) { this(it) } }

    @Composable
    fun animateRect(
        transitionSpec: @Composable Transition.Segment<ShareState>.() -> FiniteAnimationSpec<Rect> = {
            spring(visibilityThreshold = Rect.VisibilityThreshold)
        }
    ): ShareAnimBody<Rect> = { transition.animateRect(transitionSpec) { this(it) } }


    @Composable
    fun animateOffset(
        transitionSpec: @Composable Transition.Segment<ShareState>.() -> FiniteAnimationSpec<Offset> = {
            spring(visibilityThreshold = Offset.VisibilityThreshold)
        }
    ): ShareAnimBody<Offset> = { transition.animateOffset(transitionSpec) { this(it) } }

    @Composable
    fun animateIntOffset(
        transitionSpec: @Composable Transition.Segment<ShareState>.() -> FiniteAnimationSpec<IntOffset> = {
            spring(visibilityThreshold = IntOffset.VisibilityThreshold)
        }
    ): ShareAnimBody<IntOffset> = { transition.animateIntOffset(transitionSpec) { this(it) } }

    @Composable
    fun animateSize(
        transitionSpec: @Composable Transition.Segment<ShareState>.() -> FiniteAnimationSpec<Size> = {
            spring(visibilityThreshold = Size.VisibilityThreshold)
        }
    ): ShareAnimBody<Size> = { transition.animateSize(transitionSpec) { this(it) } }

    @Composable
    fun animateIntSize(
        transitionSpec: @Composable Transition.Segment<ShareState>.() -> FiniteAnimationSpec<IntSize> = {
            spring(visibilityThreshold = IntSize.VisibilityThreshold)
        }
    ): ShareAnimBody<IntSize> = { transition.animateIntSize(transitionSpec) { this(it) } }

    @Composable
    fun <T, V : AnimationVector> animateValue(
        typeConverter: TwoWayConverter<T, V>,
        transitionSpec: @Composable Transition.Segment<ShareState>.() -> FiniteAnimationSpec<T> = {
            spring()
        }
    ): ShareAnimBody<T> = { transition.animateValue(typeConverter, transitionSpec) { this(it) } }

}