package com.erolc.mrouter.route.transform


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.*
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.*
import com.erolc.mrouter.utils.*
import kotlin.math.roundToInt

fun modal() = Transform()
fun normal() = Transform()

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
    var prev: ExitTransition = ExitTransition.None
    var gesture: GestureWrap = GestureWrap.None
    internal fun build(): Transform {
        return Transform(enter, exit, prev, gesture)
    }
}

/**
 * 一个transform代表的是一次页面变换
 *
 * @param enter 本页面进入的变换
 * @param exit 本页面退出的变换,如果为空，那么退出时将会使用enter做逆向变换
 * @param prev 上一个页面在本次变换中的细微变换，对于该动画来说，其中的具体数值是没有作用的，
 * 比如说fade的alpha是没有作用的，该参数只是给框架提供动画形式和方向，具体变化细节无法干涉。是内部实现的。
 * @param gesture 手势，可以自定义手势
 */
@Immutable
data class Transform internal constructor(
    internal val enter: EnterTransition = EnterTransition.None,
    private val _exit: ExitTransition = ExitTransition.None,
    internal val prev: ExitTransition = ExitTransition.None,
    internal val gesture: GestureWrap = GestureWrap.None
) {

    companion object {
        val None = Transform()
    }

    val exit get() = if (_exit == ExitTransition.None) ExitTransitionImpl(enter.data) else _exit


    internal fun trackActive(transition: Transition<TransformState>): TransformData {
        return with(transition.segment) {
            when {
                PreEnter isTransitioningTo PreEnter -> enter.data
                PreEnter isTransitioningTo Resume || Resume isTransitioningTo Resume -> enter.data
                Resume isTransitioningTo PostExit -> exit.data
                Resume isTransitioningTo PauseState
                        || PauseState isTransitioningTo Resume
                        || PauseState isTransitioningTo PauseState -> prev.data

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
    get() = currentState == PostExit && targetState == PostExit

internal val Transition<TransformState>.enterStart
    get() = currentState == PreEnter && targetState == PreEnter

@OptIn(InternalAnimationApi::class)
@Composable
internal fun Transition<TransformState>.createModifier(
    page: String,
    transform: Transform,
    modifier: Modifier,
    label: String
): Modifier {

    val activeEnter = trackActiveEnter(enter = transform.enter)
    val activeExit = trackActiveExit(exit = transform.exit, PostExit)
    val activePause = trackActiveExit(exit = transform.prev, PauseState)


    val shouldAnimateSlide =
        activeEnter.data.slide != null || activeExit.data.slide != null || activePause.data.slide != null
    val shouldAnimateSizeChange =
        activeEnter.data.changeSize != null || activeExit.data.changeSize != null || activePause.data.changeSize != null

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
        createGraphicsLayerBlock(page, transform, activeEnter, activeExit, activePause, label)

    val disableClip = (activeEnter.data.changeSize?.clip == false ||
            activeExit.data.changeSize?.clip == false) || !shouldAnimateSizeChange

    return modifier.graphicsLayer(clip = disableClip) then TransformElement(
        this,
        transform,
        activeEnter,
        activeExit,
        activePause,
        sizeAnimation,
        slideAnimation,
        offsetAnimation,
        graphicsLayerBlock, label
    )
}


@OptIn(InternalAnimationApi::class)
@Composable
internal fun Transition<TransformState>.trackActiveEnter(enter: EnterTransition): EnterTransition {
    var activeEnter by remember(this) { mutableStateOf(enter) }
    if (currentState == targetState && currentState == Resume) {
        if (isSeeking) {
            activeEnter = enter
        } else {
            activeEnter = EnterTransition.None
        }
    } else if (targetState == Resume) {
        activeEnter += enter
    }
    return activeEnter
}

@OptIn(InternalAnimationApi::class)
@Composable
internal fun Transition<TransformState>.trackActiveExit(
    exit: ExitTransition,
    state: TransformState
): ExitTransition {
    // Active enter & active exit reference the enter and exit transition that is currently being
    // used. It is important to preserve the active enter/exit that was previously used before
    // changing target state, such that if the previous enter/exit is interrupted, we still hold
    // reference to the enter/exit that define those animations and therefore could recover.
    var activeExit by remember(this) { mutableStateOf(exit) }
    if (currentState == targetState && currentState == state) {
        if (isSeeking) {
            // When seeking, the timing is different and there's no need to handle interruptions.
            activeExit = exit
        } else {
            activeExit = ExitTransition.None
        }
    } else if (targetState == state) {
        activeExit += exit
    }
    return activeExit
}

private data class TransformElement @OptIn(InternalAnimationApi::class) constructor(
    val transition: Transition<TransformState>,
    val transform: Transform,
    val enter: EnterTransition,
    val exit: ExitTransition,
    val pause: ExitTransition,
    var sizeAnimation: Transition<TransformState>.DeferredAnimation<IntSize, AnimationVector2D>?,
    var offsetAnimation:
    Transition<TransformState>.DeferredAnimation<IntOffset, AnimationVector2D>?,
    var slideAnimation: Transition<TransformState>.DeferredAnimation<IntOffset, AnimationVector2D>?,
    var graphicsLayerBlock: GraphicsLayerBlockForTransform,
    val label: String
) : ModifierNodeElement<TransformModifierNode>() {

    @OptIn(InternalAnimationApi::class)
    override fun create(): TransformModifierNode =
        TransformModifierNode(
            transition,
            transform,
            enter,
            exit,
            pause,
            sizeAnimation,
            offsetAnimation,
            slideAnimation,
            graphicsLayerBlock, label
        )

    @OptIn(InternalAnimationApi::class)
    override fun update(node: TransformModifierNode) {
        node.transition = transition
        node.transform = transform
        node.sizeAnimation = sizeAnimation
        node.offsetAnimation = offsetAnimation
        node.slideAnimation = slideAnimation
        node.enter = enter
        node.exit = exit
        node.pause = pause
        node.graphicsLayerBlock = graphicsLayerBlock
        node.label = label
    }

    @OptIn(InternalAnimationApi::class)
    override fun InspectorInfo.inspectableProperties() {
        name = "transform"
        properties["transition"] = transition
        properties["transform"] = transform
        properties["sizeAnimation"] = sizeAnimation
        properties["offsetAnimation"] = offsetAnimation
        properties["slideAnimation"] = slideAnimation
        properties["enter"] = enter
        properties["exit"] = exit
        properties["pause"] = pause
        properties["graphicsLayerBlock"] = graphicsLayerBlock
    }
}

private class TransformModifierNode @OptIn(InternalAnimationApi::class) constructor(
    var transition: Transition<TransformState>,
    var transform: Transform,
    var enter: EnterTransition,
    var exit: ExitTransition,
    var pause: ExitTransition,
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
    val alignment: Alignment?
        get() = with(transition.segment) {
            when {
                PreEnter isTransitioningTo Resume -> enter.data.changeSize?.alignment
                Resume isTransitioningTo PostExit -> exit.getAlignment()
                Resume isTransitioningTo PauseState -> pause.data.changeSize?.alignment
                PauseState isTransitioningTo Resume -> pause.data.changeSize?.alignment
                else -> transform.exit.data.changeSize?.alignment
            }
        }

    val sizeTransitionSpec: Transition.Segment<TransformState>.() -> FiniteAnimationSpec<IntSize> =
        {
            when {
                PreEnter isTransitioningTo Resume -> enter.data.changeSize?.animationSpec
                    ?: DefaultSizeAnimationSpec

                Resume isTransitioningTo PostExit -> exit.data.changeSize?.animationSpec
                    ?: DefaultSizeAnimationSpec

                Resume isTransitioningTo PauseState -> pause.data.changeSize?.animationSpec
                    ?: DefaultSizeAnimationSpec

                PauseState isTransitioningTo Resume -> pause.data.changeSize?.animationSpec
                    ?: DefaultSizeAnimationSpec

                else -> DefaultSizeAnimationSpec
            }
        }

    fun sizeByState(targetState: TransformState, fullSize: IntSize): IntSize = when (targetState) {
        Resume -> fullSize
        PreEnter -> enter.data.changeSize?.size?.invoke(fullSize) ?: fullSize
        PostExit -> exit.data.changeSize?.size?.invoke(fullSize) ?: fullSize
        PauseState -> fullSize
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

    fun targetOffsetByState(targetState: TransformState, fullSize: IntSize): IntOffset =
        when {
            currentAlignment == null -> IntOffset.Zero
            alignment == null -> IntOffset.Zero
            currentAlignment == alignment -> IntOffset.Zero
            else -> when (targetState) {
                Resume -> IntOffset.Zero
                PreEnter -> IntOffset.Zero
                PostExit -> exit.data.changeSize?.let {
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

                PauseState -> IntOffset.Zero
                is TransitionState -> IntOffset.Zero
            }
        }

    fun getTransitionWithSize(fullSize: IntSize): IntSize {
        val progress = transition.targetState.progress
        return IntSize((fullSize.width * progress).toInt(), (fullSize.height * progress).toInt())
    }

    fun getTransitionWithOffset(fullSize: IntSize): IntOffset {
        val progress = transition.targetState.progress
        return IntOffset((fullSize.width * progress).toInt(), (fullSize.height * progress).toInt())
    }

    @OptIn(InternalAnimationApi::class)
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        if (transition.currentState == transition.targetState && !transition.enterStart) {
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
                slideTargetValueByState(it, target)
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
        when {
            PreEnter isTransitioningTo Resume -> enter.data.slide?.animationSpec
                ?: DefaultOffsetAnimationSpec

            Resume isTransitioningTo PostExit -> exit.data.slide?.animationSpec
                ?: DefaultOffsetAnimationSpec

            Resume isTransitioningTo PauseState -> pause.data.slide?.animationSpec
                ?: DefaultOffsetAnimationSpec

            PauseState isTransitioningTo Resume -> pause.data.slide?.animationSpec
                ?: DefaultOffsetAnimationSpec

            else -> exit.data.slide?.animationSpec ?: DefaultOffsetAnimationSpec
        }
    }

    fun slideTargetValueByState(targetState: TransformState, fullSize: IntSize): IntOffset {
        val preEnter = enter.data.slide?.slideOffset?.invoke(fullSize) ?: IntOffset.Zero
        val postExit = exit.data.slide?.slideOffset?.invoke(fullSize) ?: IntOffset.Zero
        val pause = pause.data.slide?.slideOffset?.invoke(fullSize) ?: IntOffset.Zero
        return when (targetState) {
            Resume -> preEnter
            PreEnter -> preEnter
            PostExit -> postExit
            PauseState -> pause
            else -> IntOffset.Zero
        }
    }

}

internal val InvalidSize = IntSize(Int.MIN_VALUE, Int.MIN_VALUE)

internal fun ExitTransition.getAlignment() = data.changeSize?.alignment
internal fun TransformData.getAlignment() = changeSize?.alignment

internal fun interface GraphicsLayerBlockForTransform {
    fun init(): GraphicsLayerScope.() -> Unit
}

@OptIn(InternalAnimationApi::class)
@Composable
private fun Transition<TransformState>.createGraphicsLayerBlock(
    page: String,
    transform: Transform,
    enter: EnterTransition,
    exit: ExitTransition,
    pause: ExitTransition,
    label: String
): GraphicsLayerBlockForTransform {

//    val shouldAnimateAlpha =
//        enter.data.fade != null || exit.data.fade != null || pause.data.fade != null
//    val shouldAnimateScale =
//        enter.data.scale != null || exit.data.scale != null || pause.data.scale != null

    val transformData = remember(transform) { transform.trackActive(this) }

    val shouldAnimateAlpha = transformData.fade != null
    val shouldAnimateScale = transformData.scale != null


    var progressAlpha by remember { mutableStateOf(1f) }
    var progressScale by remember { mutableStateOf(1f) }

    val alphaAnimation = if (shouldAnimateAlpha) {
        createDeferredAnimation(typeConverter = Float.VectorConverter,
            label = remember { "$label alpha" }
        )
    } else {
        progressAlpha = transform.prev.data.fade?.run { alpha * targetState.progress } ?: 1f
        null
    }

    val scaleAnimation = if (shouldAnimateScale) {
        createDeferredAnimation(typeConverter = Float.VectorConverter,
            label = remember { "$label scale" }
        )
    } else {
        progressScale = enter.data.scale?.run { scale * targetState.progress } ?: 1f
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
                when {
                    PreEnter isTransitioningTo Resume -> enter.data.fade?.animationSpec
                        ?: DefaultAlphaAndScaleSpring

                    Resume isTransitioningTo PostExit -> exit.data.fade?.animationSpec
                        ?: DefaultAlphaAndScaleSpring

                    Resume isTransitioningTo PauseState -> pause.data.fade?.animationSpec
                        ?: DefaultAlphaAndScaleSpring

                    PauseState isTransitioningTo Resume -> pause.data.fade?.animationSpec
                        ?: DefaultAlphaAndScaleSpring

                    else -> exit.data.fade?.animationSpec ?: DefaultAlphaAndScaleSpring
                }
            },
        ) {
            when (it) {
                Resume -> 1f
                PreEnter -> enter.data.fade?.alpha ?: 1f
                PostExit -> exit.data.fade?.alpha ?: 1f
                else -> it.progress
            }
        }

        val scale = scaleAnimation?.animate(
            transitionSpec = {
                when {
                    PreEnter isTransitioningTo Resume -> enter.data.scale?.animationSpec
                        ?: DefaultAlphaAndScaleSpring

                    Resume isTransitioningTo PostExit -> exit.data.scale?.animationSpec
                        ?: DefaultAlphaAndScaleSpring

                    Resume isTransitioningTo PauseState -> pause.data.scale?.animationSpec
                        ?: DefaultAlphaAndScaleSpring

                    PauseState isTransitioningTo Resume -> pause.data.scale?.animationSpec
                        ?: DefaultAlphaAndScaleSpring

                    else -> DefaultAlphaAndScaleSpring
                }
            }
        ) {
            when (it) {
                Resume -> 1f
                PreEnter -> enter.data.scale?.scale ?: 1f
                PostExit -> exit.data.scale?.scale ?: 1f
                else -> it.progress
            }
        }

        val transformOriginWhenVisible =
            if (currentState == PreEnter) {
                enter.data.scale?.transformOrigin ?: exit.data.scale?.transformOrigin
            } else if (targetState == PauseState) {
                pause.data.scale?.transformOrigin ?: enter.data.scale?.transformOrigin
            } else {
                exit.data.scale?.transformOrigin ?: enter.data.scale?.transformOrigin
            }

        // Animate transform origin if there's any change. If scale is only defined for enter or
        // exit, use the same transform origin for both.
        val transformOrigin = transformOriginAnimation?.animate({ spring() }) {
            when (it) {
                Resume -> transformOriginWhenVisible
                PreEnter -> enter.data.scale?.transformOrigin ?: exit.data.scale?.transformOrigin
                PostExit -> exit.data.scale?.transformOrigin ?: enter.data.scale?.transformOrigin
                PauseState -> pause.data.scale?.transformOrigin ?: enter.data.scale?.transformOrigin
                else -> null
            } ?: TransformOrigin.Center
        }


        val block: GraphicsLayerScope.() -> Unit = {
            this.alpha = alpha?.value ?: progressAlpha
            loge("tag", "$page alpha:${alpha?.value} $currentState $targetState")
            this.scaleX = scale?.value ?: progressScale
            this.scaleY = scale?.value ?: progressScale
            this.transformOrigin =
                transformOrigin?.value ?: transform.exit.data.scale?.transformOrigin
                        ?: TransformOrigin.Center
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