package com.erolc.mrouter.route.transform


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.*
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.*
import com.erolc.mrouter.route.transform.share.NormalShareTransformWrap
import com.erolc.mrouter.utils.*
import kotlin.math.roundToInt

/**
 * 转换动画，从一个页面到另外一个页面可设置相应的动画效果，其中的[fadeIn]，[fadeOut]等一众方法都与系统提供的一致，可直接使用，无学习成本
 * 并在此之上增加了[Transform]，该类描述了一次转换所需要的动画以及手势
 */

fun modal(scale: Float = 0.96f) = buildTransform {
    enter = slideInVertically { it }
    prevPause = scaleOut(targetScale = scale)
    wrap = ModalTransformWrap(scale + 0.03f)
}

fun normal() = buildTransform {
    enter = slideInHorizontally { it }
    prevPause = slideOutHorizontally { -it / 7 }
    wrap = NormalTransformWrap()
}

fun none() = buildTransform {
    enter = slideInHorizontally { it }
    wrap = NoneTransformWrap()
}

/**
 * 共享元素，当需要使用共享元素动画时，需要使用该transform
 * @param keys 指定参与该次页面切换的共享控件
 * @param animationSpec 页面转换动画使用
 * @param transitionSpec 共享元素变换使用,控制共享元素尺寸的变化
 * @param wrap 变换包装类，可以在这里处理变换过程中两个页面的一些变化，可以通过该类给共享元素变换加上手势退出，可参考[NormalShareTransformWrap]
 * 需要注意的是，如果给[shareAnimationSpec]或[animationSpec]设置[tween]那么请给另一个也加上，并且给予相同的时间，如此两者的进度才是一致的。
 */
fun share(
    vararg keys: String,
    animationSpec: FiniteAnimationSpec<Float> = spring(stiffness = Spring.StiffnessMediumLow),
    shareAnimationSpec: FiniteAnimationSpec<Rect> = spring(visibilityThreshold = Rect.VisibilityThreshold),
    wrap: TransformWrap = NormalShareTransformWrap(shareAnimationSpec, *keys)
) = buildTransform {
    enter = fadeIn(animationSpec)
    this.wrap = wrap
}

@Stable
fun fadeIn(
    animationSpec: FiniteAnimationSpec<Float> = spring(stiffness = Spring.StiffnessMediumLow),
    initialOffset: Float = 0f
): EnterTransition {
    return EnterTransitionImpl(TransformData(fade = Fade(initialOffset, animationSpec)))
}

@Stable
fun fadeOut(
    animationSpec: FiniteAnimationSpec<Float> = spring(stiffness = Spring.StiffnessMediumLow),
    targetAlpha: Float = 0f,
): ExitTransition {
    return ExitTransitionImpl(TransformData(fade = Fade(targetAlpha, animationSpec)))
}

@Stable
fun slideIn(
    animationSpec: FiniteAnimationSpec<IntOffset> =
        spring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        ),
    initialOffset: (fullSize: IntSize) -> IntOffset,
): EnterTransition {
    return EnterTransitionImpl(TransformData(slide = Slide(initialOffset, animationSpec)))
}

@Stable
fun slideOut(
    animationSpec: FiniteAnimationSpec<IntOffset> =
        spring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        ),
    targetOffset: (fullSize: IntSize) -> IntOffset,
): ExitTransition {
    return ExitTransitionImpl(TransformData(slide = Slide(targetOffset, animationSpec)))
}

@Stable
fun scaleIn(
    animationSpec: FiniteAnimationSpec<Float> = spring(stiffness = Spring.StiffnessMediumLow),
    initialScale: Float = 0f,
    transformOrigin: TransformOrigin = TransformOrigin.Center,
): EnterTransition {
    return EnterTransitionImpl(
        TransformData(
            scale = Scale(
                initialScale,
                transformOrigin,
                animationSpec
            )
        )
    )
}

@Stable
fun scaleOut(
    animationSpec: FiniteAnimationSpec<Float> = spring(stiffness = Spring.StiffnessMediumLow),
    targetScale: Float = 0f,
    transformOrigin: TransformOrigin = TransformOrigin.Center
): ExitTransition {
    return ExitTransitionImpl(
        TransformData(
            scale = Scale(
                targetScale,
                transformOrigin,
                animationSpec
            )
        )
    )
}

@Stable
fun expandIn(
    animationSpec: FiniteAnimationSpec<IntSize> =
        spring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntSize.VisibilityThreshold
        ),
    expandFrom: Alignment = Alignment.BottomEnd,
    clip: Boolean = true,
    initialSize: (fullSize: IntSize) -> IntSize = { IntSize(0, 0) },
): EnterTransition {
    return EnterTransitionImpl(
        TransformData(
            changeSize = ChangeSize(
                expandFrom,
                initialSize,
                animationSpec,
                clip
            )
        )
    )
}

@Stable
fun shrinkOut(
    animationSpec: FiniteAnimationSpec<IntSize> =
        spring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntSize.VisibilityThreshold
        ),
    shrinkTowards: Alignment = Alignment.BottomEnd,
    clip: Boolean = true,
    targetSize: (fullSize: IntSize) -> IntSize = { IntSize(0, 0) },
): ExitTransition {
    return ExitTransitionImpl(
        TransformData(
            changeSize = ChangeSize(
                shrinkTowards,
                targetSize,
                animationSpec,
                clip
            )
        )
    )
}

@Stable
fun expandHorizontally(
    animationSpec: FiniteAnimationSpec<IntSize> =
        spring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntSize.VisibilityThreshold
        ),
    expandFrom: Alignment.Horizontal = Alignment.End,
    clip: Boolean = true,
    initialWidth: (fullWidth: Int) -> Int = { 0 },
): EnterTransition {
    return expandIn(
        animationSpec,
        expandFrom.toAlignment(),
        clip = clip
    ) { IntSize(initialWidth(it.width), it.height) }
}

@Stable
fun expandVertically(
    animationSpec: FiniteAnimationSpec<IntSize> =
        spring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntSize.VisibilityThreshold
        ),
    expandFrom: Alignment.Vertical = Alignment.Bottom,
    clip: Boolean = true,
    initialHeight: (fullHeight: Int) -> Int = { 0 },
): EnterTransition {
    return expandIn(animationSpec, expandFrom.toAlignment(), clip) {
        IntSize(
            it.width,
            initialHeight(it.height)
        )
    }
}

@Stable
fun shrinkHorizontally(
    animationSpec: FiniteAnimationSpec<IntSize> =
        spring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntSize.VisibilityThreshold
        ),
    shrinkTowards: Alignment.Horizontal = Alignment.End,
    clip: Boolean = true,
    targetWidth: (fullWidth: Int) -> Int = { 0 }
): ExitTransition {
    // TODO: Support different animation types
    return shrinkOut(animationSpec, shrinkTowards.toAlignment(), clip) {
        IntSize(targetWidth(it.width), it.height)
    }
}

@Stable
fun shrinkVertically(
    animationSpec: FiniteAnimationSpec<IntSize> =
        spring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntSize.VisibilityThreshold
        ),
    shrinkTowards: Alignment.Vertical = Alignment.Bottom,
    clip: Boolean = true,
    targetHeight: (fullHeight: Int) -> Int = { 0 },
): ExitTransition {
    // TODO: Support different animation types
    return shrinkOut(animationSpec, shrinkTowards.toAlignment(), clip) {
        IntSize(it.width, targetHeight(it.height))
    }
}

@Stable
fun slideInHorizontally(
    animationSpec: FiniteAnimationSpec<IntOffset> =
        spring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        ),
    initialOffsetX: (fullWidth: Int) -> Int = { -it / 2 },
): EnterTransition =
    slideIn(
        initialOffset = { IntOffset(initialOffsetX(it.width), 0) },
        animationSpec = animationSpec
    )

@Stable
fun slideInVertically(
    animationSpec: FiniteAnimationSpec<IntOffset> =
        spring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        ),
    initialOffsetY: (fullHeight: Int) -> Int = { -it / 2 },
): EnterTransition =
    slideIn(
        initialOffset = { IntOffset(0, initialOffsetY(it.height)) },
        animationSpec = animationSpec
    )

@Stable
fun slideOutHorizontally(
    animationSpec: FiniteAnimationSpec<IntOffset> =
        spring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        ),
    targetOffsetX: (fullWidth: Int) -> Int = { -it / 2 },
): ExitTransition =
    slideOut(
        targetOffset = { IntOffset(targetOffsetX(it.width), 0) },
        animationSpec = animationSpec
    )

@Stable
fun slideOutVertically(
    animationSpec: FiniteAnimationSpec<IntOffset> =
        spring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        ),
    targetOffsetY: (fullHeight: Int) -> Int = { -it / 2 },
): ExitTransition =
    slideOut(
        targetOffset = { IntOffset(0, targetOffsetY(it.height)) },
        animationSpec = animationSpec
    )

/*********************** Below are internal classes and methods ******************/
private fun Alignment.Horizontal.toAlignment() =
    when (this) {
        Alignment.Start -> Alignment.CenterStart
        Alignment.End -> Alignment.CenterEnd
        else -> Alignment.Center
    }

private fun Alignment.Vertical.toAlignment() =
    when (this) {
        Alignment.Top -> Alignment.TopCenter
        Alignment.Bottom -> Alignment.BottomCenter
        else -> Alignment.Center
    }

class TransformBuilder {
    var enter: EnterTransition = EnterTransition.None
    var exit: ExitTransition = ExitTransition.None
    var prevPause: ExitTransition = slideOutHorizontally { 0 }
    var wrap: TransformWrap = NoneTransformWrap()
    internal fun build(): Transform {
        return Transform(enter, exit, prevPause, wrap)
    }
}

fun buildTransform(body: TransformBuilder.() -> Unit = {}): Transform {
    return TransformBuilder().apply(body).build()
}

/**
 * 一个transform代表的是一次页面变换
 *
 * @param enter 本页面进入的动画
 * @param _exit 本页面退出的动画,如果为空，那么退出时将会使用enter做逆向变换
 * @param prevPause 上一个页面在本次变换中的动画
 * @param wrap 包装，页面切换包装，可以在其中实现一些功能，比如手势操作
 */
@Immutable
data class Transform internal constructor(
    internal val enter: EnterTransition = EnterTransition.None,
    private val _exit: ExitTransition = ExitTransition.None,
    internal val prevPause: ExitTransition = slideOutHorizontally { 0 },
    internal val wrap: TransformWrap = NoneTransformWrap()
) {

    companion object {
        val None = Transform()
    }

    val exit get() = if (_exit == ExitTransition.None) ExitTransitionImpl(enter.data) else _exit


    internal fun trackActive(transition: Transition<TransformState>): TransformData {
        return with(transition.segment) {
            when {
                EnterState isTransitioningTo EnterState -> enter.data
                EnterState isTransitioningTo ResumeState || ResumeState isTransitioningTo ResumeState -> enter.data
                ResumeState isTransitioningTo ExitState -> exit.data
                ResumeState isTransitioningTo StopState
                        || StopState isTransitioningTo ResumeState
                        || StopState isTransitioningTo StopState -> prevPause.data

                targetState is StoppingState || initialState is StoppingState -> prevPause.data
                targetState is ExitingState || initialState is ExitingState -> exit.data
                else -> TransformData.None
            }
        }
    }
}

@Immutable
internal data class TransformData(
    val fade: Fade? = null,
    val slide: Slide? = null,
    val changeSize: ChangeSize? = null,
    val scale: Scale? = null
) {
    companion object {
        val None = TransformData()
    }
}

@Immutable
internal data class Fade(val alpha: Float, val animationSpec: FiniteAnimationSpec<Float>)

@Immutable
internal data class Slide(
    val slideOffset: (fullSize: IntSize) -> IntOffset,
    val animationSpec: FiniteAnimationSpec<IntOffset>
)

@Immutable
internal data class ChangeSize(
    val alignment: Alignment,
    val size: (fullSize: IntSize) -> IntSize = { IntSize(0, 0) },
    val animationSpec: FiniteAnimationSpec<IntSize>,
    val clip: Boolean = true
)

@Immutable
internal data class Scale(
    val scale: Float,
    val transformOrigin: TransformOrigin,
    val animationSpec: FiniteAnimationSpec<Float>
)

@Immutable
sealed class EnterTransition {
    internal abstract val data: TransformData

    /**
     * Combines different enter transitions. The order of the [EnterTransition]s being combined
     * does not matter, as these [EnterTransition]s will start simultaneously. The order of
     * applying transforms from these enter transitions (if defined) is: alpha and scale first,
     * shrink or expand, then slide.
     *
     * @sample androidx.compose.animation.samples.FullyLoadedTransition
     *
     * @param enter another [EnterTransition] to be combined
     */
    @Stable
    operator fun plus(enter: EnterTransition): EnterTransition {
        return EnterTransitionImpl(
            TransformData(
                fade = data.fade ?: enter.data.fade,
                slide = data.slide ?: enter.data.slide,
                changeSize = data.changeSize ?: enter.data.changeSize,
                scale = data.scale ?: enter.data.scale
            )
        )
    }

    override fun toString(): String =
        if (this == None) {
            "EnterTransition.None"
        } else {
            data.run {
                "EnterTransition: \n" + "Fade - " + fade?.toString() + ",\nSlide - " +
                        slide?.toString() + ",\nShrink - " + changeSize?.toString() +
                        ",\nScale - " + scale?.toString()
            }
        }

    override fun equals(other: Any?): Boolean {
        return other is EnterTransition && other.data == data
    }

    override fun hashCode(): Int = data.hashCode()

    companion object {
        /**
         * This can be used when no enter transition is desired. It can be useful in cases where
         * there are other forms of enter animation defined indirectly for an
         * [AnimatedVisibility]. e.g.The children of the [AnimatedVisibility] have all defined
         * their own [EnterTransition], or when the parent is fading in, etc.
         *
         * @see [ExitTransition.None]
         */
        val None: EnterTransition = EnterTransitionImpl(TransformData.None)
    }
}


@Immutable
sealed class ExitTransition {
    internal abstract val data: TransformData

    /**
     * Combines different exit transitions. The order of the [ExitTransition]s being combined
     * does not matter, as these [ExitTransition]s will start simultaneously. The order of
     * applying transforms from these exit transitions (if defined) is: alpha and scale first,
     * shrink or expand, then slide.
     *
     * @sample androidx.compose.animation.samples.FullyLoadedTransition
     *
     * @param exit another [ExitTransition] to be combined.
     */
    @Stable
    operator fun plus(exit: ExitTransition): ExitTransition {
        return ExitTransitionImpl(
            TransformData(
                fade = data.fade ?: exit.data.fade,
                slide = data.slide ?: exit.data.slide,
                changeSize = data.changeSize ?: exit.data.changeSize,
                scale = data.scale ?: exit.data.scale
            )
        )
    }

    override fun equals(other: Any?): Boolean {
        return other is ExitTransition && other.data == data
    }

    override fun toString(): String =
        if (this == None) {
            "ExitTransition.None"
        } else {
            data.run {
                "ExitTransition: \n" + "Fade - " + fade?.toString() + ",\nSlide - " +
                        slide?.toString() + ",\nShrink - " + changeSize?.toString() +
                        ",\nScale - " + scale?.toString()
            }
        }

    override fun hashCode(): Int = data.hashCode()

    companion object {
        /**
         * This can be used when no built-in [ExitTransition] (i.e. fade/slide, etc) is desired for
         * the [AnimatedVisibility], but rather the children are defining their own exit
         * animation using the [Transition] scope.
         *
         * __Note:__ If [None] is used, and nothing is animating in the Transition<EnterExitState>
         * scope that [AnimatedVisibility] provided, the content will be removed from
         * [AnimatedVisibility] right away.
         *
         * @sample androidx.compose.animation.samples.AVScopeAnimateEnterExit
         */
        val None: ExitTransition = ExitTransitionImpl(TransformData())
    }
}

@Immutable
private class ExitTransitionImpl(override val data: TransformData) : ExitTransition()

@Immutable
private class EnterTransitionImpl(override val data: TransformData) : EnterTransition()

internal val Transition<TransformState>.exitFinished
    get() = currentState == ExitState && targetState == ExitState

internal val Transition<TransformState>.resume
    get() = currentState == ResumeState && targetState == ResumeState


internal val Transition<TransformState>.enterStart
    get() = currentState == EnterState && targetState == EnterState

internal val Transition<TransformState>.stop
    get() = currentState == StopState && targetState == StopState

@OptIn(InternalAnimationApi::class)
@Composable
internal fun Transition<TransformState>.createModifier(
    transform: Transform,
    modifier: Modifier,
    label: String
): Modifier {
    val transformData = transform.trackActive(this)

    val shouldAnimateSlide = transformData.slide != null
    val shouldAnimateSizeChange = transformData.changeSize != null
    val slideAnimation = if (shouldAnimateSlide) {
        createDeferredAnimation(IntOffset.VectorConverter, remember { "$label slide" })
    } else {
        null
    }
    val sizeAnimation = if (shouldAnimateSizeChange) {
        createDeferredAnimation(IntSize.VectorConverter, remember { "$label shrink/expand" })
    } else null

    val offsetAnimation = if (shouldAnimateSizeChange) {
        createDeferredAnimation(
            IntOffset.VectorConverter,
            remember { "$label InterruptionHandlingOffset" }
        )
    } else null

    val graphicsLayerBlock =
        createGraphicsLayerBlock(transform, transformData, label)

    val disableClip = transformData.changeSize?.clip == false || !shouldAnimateSizeChange

    return modifier.graphicsLayer(clip = !disableClip) then TransformElement(
        this,
        transformData,
        sizeAnimation,
        offsetAnimation,
        slideAnimation,
        graphicsLayerBlock, label
    )
}

private data class TransformElement @OptIn(InternalAnimationApi::class) constructor(
    val transition: Transition<TransformState>,
    val transformData: TransformData,
    val sizeAnimation: Transition<TransformState>.DeferredAnimation<IntSize, AnimationVector2D>?,
    val offsetAnimation: Transition<TransformState>.DeferredAnimation<IntOffset, AnimationVector2D>?,
    val slideAnimation: Transition<TransformState>.DeferredAnimation<IntOffset, AnimationVector2D>?,
    val graphicsLayerBlock: GraphicsLayerBlockForTransform,
    val label: String
) : ModifierNodeElement<TransformModifierNode>() {

    @OptIn(InternalAnimationApi::class)
    override fun create(): TransformModifierNode {
        return TransformModifierNode(
            transition,
            transformData,
            sizeAnimation,
            offsetAnimation,
            slideAnimation,
            graphicsLayerBlock, label
        )
    }


    @OptIn(InternalAnimationApi::class)
    override fun update(node: TransformModifierNode) {
        node.transition = transition
        node.sizeAnimation = sizeAnimation
        node.offsetAnimation = offsetAnimation
        node.slideAnimation = slideAnimation
        node.transformData = transformData
        node.graphicsLayerBlock = graphicsLayerBlock
        node.label = label
    }

    @OptIn(InternalAnimationApi::class)
    override fun InspectorInfo.inspectableProperties() {
        name = "transform"
        properties["transition"] = transition
        properties["sizeAnimation"] = sizeAnimation
        properties["offsetAnimation"] = offsetAnimation
        properties["slideAnimation"] = slideAnimation
        properties["transformData"] = transformData
        properties["graphicsLayerBlock"] = graphicsLayerBlock
    }
}

private class TransformModifierNode @OptIn(InternalAnimationApi::class) constructor(
    var transition: Transition<TransformState>,
    var transformData: TransformData,
    var sizeAnimation: Transition<TransformState>.DeferredAnimation<IntSize, AnimationVector2D>?,
    var offsetAnimation:
    Transition<TransformState>.DeferredAnimation<IntOffset, AnimationVector2D>?,
    var slideAnimation: Transition<TransformState>.DeferredAnimation<IntOffset, AnimationVector2D>?,
    var graphicsLayerBlock: GraphicsLayerBlockForTransform,
    var label: String
) : LayoutModifierNodeWithPassThroughIntrinsics() {

    private var lookaheadConstraintsAvailable = false
    private var lookaheadSize: IntSize = InvalidSize
    private var lookaheadConstraints: Constraints = Constraints()
        set(value) {
            lookaheadConstraintsAvailable = true
            field = value
        }

    var currentAlignment: Alignment? = null
    val alignment: Alignment? get() = transformData.getAlignment()

    val sizeTransitionSpec: Transition.Segment<TransformState>.() -> FiniteAnimationSpec<IntSize> =
        {
            transformData.changeSize?.animationSpec ?: DefaultSizeAnimationSpec
        }

    fun sizeByState(targetState: TransformState, fullSize: IntSize): IntSize =
        when (targetState) {
            ResumeState -> fullSize
            EnterState, ExitState, StopState -> transformData.changeSize?.size?.invoke(fullSize)
                ?: fullSize

            is StoppingState -> {
                val startValue = transformData.changeSize?.size?.invoke(fullSize) ?: fullSize
                startValue.compute(targetState.progress, fullSize)
            }

            is ExitingState -> {
                val startValue = transformData.changeSize?.size?.invoke(fullSize) ?: fullSize
                var newWidth = startValue.width * 1.0f
                var newHeight = startValue.height * 1.0f
                if (startValue.height == fullSize.height) {
                    newWidth =
                        targetState.progress * (fullSize.width - startValue.width) + startValue.width
                }
                if (startValue.width == fullSize.width) {
                    newHeight =
                        targetState.progress * (fullSize.height - startValue.height) + startValue.height

                }
                IntSize(newWidth.roundToInt(), newHeight.roundToInt())
            }

            else -> IntSize(
                (fullSize.width * targetState.progress).roundToInt(),
                (fullSize.height * targetState.progress).roundToInt()
            )
        }

    override fun onAttach() {
        super.onAttach()
        lookaheadConstraintsAvailable = false
        lookaheadSize = InvalidSize
    }


    fun slideTargetValueByState(targetState: TransformState, fullSize: IntSize): IntOffset {
        return when (targetState) {
            ResumeState -> IntOffset.Zero
            EnterState -> transformData.slide?.slideOffset?.invoke(fullSize) ?: IntOffset.Zero
            ExitState -> transformData.slide?.slideOffset?.invoke(fullSize) ?: IntOffset.Zero
            StopState -> transformData.slide?.slideOffset?.invoke(fullSize) ?: IntOffset.Zero
            is StoppingState -> {
                val start = transformData.slide?.slideOffset?.invoke(fullSize) ?: IntOffset.Zero
                start.compute(targetState.progress)
            }

            is ExitingState -> {
                val start = transformData.slide?.slideOffset?.invoke(fullSize) ?: IntOffset.Zero
                start.compute(targetState.progress)
            }

            else -> IntOffset.Zero
        }
    }

    private fun IntOffset.compute(progress: Float): IntOffset {
        val newX = progress * (0 - x) + x
        val newY = progress * (0 - y) + y
        return IntOffset(newX.roundToInt(), newY.roundToInt())
    }

    private fun IntSize.compute(progress: Float, fullSize: IntSize): IntSize {
        val newWidth = progress * (fullSize.width - width) + width
        val newHeight = progress * (fullSize.height - height) + height
        return IntSize(newWidth.roundToInt(), newHeight.roundToInt())
    }

    fun targetOffsetByState(targetState: TransformState, fullSize: IntSize): IntOffset =
        when {
            currentAlignment == null -> IntOffset.Zero
            alignment == null -> IntOffset.Zero
            currentAlignment == alignment -> IntOffset.Zero
            else -> when (targetState) {
                ResumeState -> IntOffset.Zero
                EnterState -> IntOffset.Zero
                ExitState, StopState -> transformData.changeSize?.let {
                    val endSize = it.size(fullSize)
                    val targetOffset = alignment!!.align(
                        fullSize,
                        endSize,
                        LayoutDirection.Ltr
                    )
                    val currentOffset = currentAlignment!!.align(
                        fullSize,
                        endSize,
                        LayoutDirection.Ltr
                    )
                    targetOffset - currentOffset
                } ?: IntOffset.Zero

                else -> IntOffset.Zero

            }
        }


    @OptIn(InternalAnimationApi::class)
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        if (transition.currentState == transition.targetState) {
            currentAlignment = null
        } else if (currentAlignment == null) {
            currentAlignment = alignment ?: Alignment.TopStart
        }
        if (isLookingAhead) {
            val placeable = measurable.measure(constraints)
            val measuredSize = IntSize(placeable.width, placeable.height)
            lookaheadSize = measuredSize
            lookaheadConstraints = constraints
            return layout(measuredSize.width, measuredSize.height) {
                placeable.place(0, 0)
            }
        } else {
            val layerBlock = graphicsLayerBlock.init()
            // Measure the content based on the current constraints passed down from parent.
            // AnimatedContent will measure outgoing children with a cached constraints to avoid
            // re-layout the outgoing content. At the animateEnterExit() level, it's not best not
            // to make assumptions, which is why we use constraints from parent.
            val placeable = measurable.measure(constraints)
            val measuredSize = IntSize(placeable.width, placeable.height)
            val target = if (lookaheadSize != InvalidSize) lookaheadSize else measuredSize
            val animSize = sizeAnimation?.animate(sizeTransitionSpec) {
                val size = sizeByState(it, target)
                size
            }
            // Since we measure with lookahead constraints when available, the size needs to
            // be constrained by incoming constraints so that we know how to position content
            // in the constrained rect based on alignment.
            val currentSize = constraints.constrain(animSize?.value ?: measuredSize)
            val offsetDelta = offsetAnimation?.animate({ DefaultOffsetAnimationSpec }) {
                targetOffsetByState(it, target)
            }?.value ?: IntOffset.Zero
            val slideOffset = slideAnimation?.animate(slideSpec) {
                val slide = slideTargetValueByState(it, target)
                slide
            }?.value ?: IntOffset.Zero
            val offset = (currentAlignment?.align(target, currentSize, LayoutDirection.Ltr)
                ?: IntOffset.Zero) + slideOffset
            return layout(currentSize.width, currentSize.height) {
                placeable.placeWithLayer(
                    offset.x + offsetDelta.x, offset.y + offsetDelta.y, 0f, layerBlock
                )
            }
        }
    }

    val slideSpec: Transition.Segment<TransformState>.() -> FiniteAnimationSpec<IntOffset> = {
        transformData.slide?.animationSpec ?: DefaultOffsetAnimationSpec
    }


}

internal val InvalidSize = IntSize(Int.MIN_VALUE, Int.MIN_VALUE)
internal fun TransformData.getAlignment() = changeSize?.alignment

internal fun interface GraphicsLayerBlockForTransform {
    fun init(): GraphicsLayerScope.() -> Unit
}

@OptIn(InternalAnimationApi::class)
@Composable
private fun Transition<TransformState>.createGraphicsLayerBlock(
    transform: Transform,
    transformData: TransformData,
    label: String
): GraphicsLayerBlockForTransform {

    val shouldAnimateAlpha = transformData.fade != null
    val shouldAnimateScale = transformData.scale != null

    var progressAlpha by remember { mutableStateOf(1f) }
    var progressScale by remember { mutableStateOf(1f) }

    val alphaAnimation = if (shouldAnimateAlpha)
        createDeferredAnimation(typeConverter = Float.VectorConverter,
            label = remember { "$label alpha" }
        )
    else {
        progressAlpha = transform.prevPause.data.fade?.run { alpha * targetState.progress } ?: 1f
        null
    }

    val scaleAnimation = if (shouldAnimateScale) {
        createDeferredAnimation(typeConverter = Float.VectorConverter,
            label = remember { "$label scale" }
        )
    } else {
        progressScale = transformData.scale?.run { scale * targetState.progress } ?: 1f
        null
    }

    val transformOriginAnimation = if (shouldAnimateScale) {
        createDeferredAnimation(
            TransformOriginVectorConverter,
            label = "TransformOriginInterruptionHandling"
        )
    } else null



    return GraphicsLayerBlockForTransform {

        val alpha = alphaAnimation?.animate(
            transitionSpec = {
                transformData.fade?.animationSpec ?: DefaultAlphaAndScaleSpring
            },
        ) {
            when (it) {
                ResumeState -> 1f
                EnterState, ExitState, StopState -> transformData.fade?.alpha ?: 1f
                is ExitingState -> it.progress
                else -> {
                    val startValue = transformData.fade?.alpha ?: 1f
                    it.progress * (1 - startValue) + startValue
                }
            }
        }

        val scale = scaleAnimation?.animate(
            transitionSpec = {
                transformData.scale?.animationSpec ?: DefaultAlphaAndScaleSpring
            }
        ) {
            when (it) {
                ResumeState -> 1f
                EnterState, ExitState, StopState -> transformData.scale?.scale ?: 1f
                is ExitingState -> it.progress
                else -> {
                    val startValue = transformData.scale?.scale ?: 1f
                    it.progress * (1 - startValue) + startValue
                }
            }
        }

        val transformOriginWhenVisible = transformData.scale?.transformOrigin

        // Animate transform origin if there's any change. If scale is only defined for enter or
        // exit, use the same transform origin for both.
        val transformOrigin = transformOriginAnimation?.animate({ spring() }) {
            when (it) {
                ResumeState -> transformOriginWhenVisible
                EnterState, ExitState, StopState -> transformData.scale?.transformOrigin
                else -> null
            } ?: TransformOrigin.Center
        }


        val block: GraphicsLayerScope.() -> Unit = {
            this.alpha = alpha?.value ?: progressAlpha
            this.scaleX = scale?.value ?: progressScale
            this.scaleY = scale?.value ?: progressScale
            this.transformOrigin =
                transformOrigin?.value ?: TransformOrigin.Center
        }
        block
    }
}

private val TransformOriginVectorConverter =
    TwoWayConverter<TransformOrigin, AnimationVector2D>(
        convertToVector = { AnimationVector2D(it.pivotFractionX, it.pivotFractionY) },
        convertFromVector = { TransformOrigin(it.v1, it.v2) }
    )

private val DefaultAlphaAndScaleSpring = spring<Float>(stiffness = Spring.StiffnessMediumLow)

private val DefaultOffsetAnimationSpec = spring(
    stiffness = Spring.StiffnessMediumLow, visibilityThreshold = IntOffset.VisibilityThreshold
)

private val DefaultSizeAnimationSpec = spring(
    stiffness = Spring.StiffnessMediumLow, visibilityThreshold = IntSize.VisibilityThreshold
)


internal abstract class LayoutModifierNodeWithPassThroughIntrinsics :
    LayoutModifierNode, Modifier.Node() {
    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ) = measurable.minIntrinsicWidth(height)

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ) = measurable.minIntrinsicHeight(width)

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ) = measurable.maxIntrinsicWidth(height)

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ) = measurable.maxIntrinsicHeight(width)
}