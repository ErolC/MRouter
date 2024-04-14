package com.erolc.mrouter.route.shareele

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import com.erolc.mrouter.backstack.entry.PageEntry
import com.erolc.mrouter.model.ShareElement
import com.erolc.mrouter.model.ShareElementGroup
import com.erolc.mrouter.model.ShareEntry
import com.erolc.mrouter.route.transform.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlin.math.roundToInt


/**
 * 共享控制器
 * 控制共享元素的变动。
 *
 */
internal object ShareEleController {
    /**
     * 用于保存所有已产生的共享元素
     */
    private val elements = mutableMapOf<String,ShareElement>()

    /**
     * 共享状态
     */
    private val shareState: MutableStateFlow<ShareState> = MutableStateFlow(Init)

    /**
     * 当进行共享时，将会在[elements]中提取成对的共享元素组装成[ShareEntry]并添加到堆栈中
     */
    private val shareStack = MutableStateFlow(listOf<ShareEntry>())

    internal fun addElement(element: ShareElement) {
        elements[element.tag]=element
    }

    @Composable
    internal fun rememberShareState() = shareState.asStateFlow().collectAsState()

    /**
     * 初始化共享
     */
    fun initShare(entry: PageEntry, endEntry: PageEntry) {
        val gesture = endEntry.transform.value.gesture
        if (gesture is ShareGestureWrap) {
            val groups = gesture.keys.mapNotNull {
                val startTag = "${entry.address.path}_$it"
                val endTag = "${endEntry.address.path}_$it"
                val startEle = elements[startTag]
                val endEle = elements[endTag]
                startEle?.let { start -> endEle?.let { end -> ShareElementGroup(start, end) } }
            }
            if (groups.isNotEmpty()) {
                shareStack.value += ShareEntry(groups, gesture.transitionSpec)
                shareState.value = BeforeShare.apply { isForward = true }
            }
        }
    }

    /**
     * 后退共享过程
     */
    internal fun exitShare() {
        if (shareStack.value.isNotEmpty())
            shareState.value = BeforeShare.apply { isForward = false }
    }

    /**
     * 共享结束之后
     */
    fun afterShare(entry: PageEntry) {
        shareState.value = Init
        val last = shareStack.value.lastOrNull()
        last?.let {
            if (it.groups.firstOrNull()?.end?.address == entry.address.path && shareStack.value.isNotEmpty())
                shareStack.value -= it
        }
    }

    private fun getShareStack() = shareStack.map { it.takeLast(1) }

    @Composable
    internal fun Overlay() {
        val state by shareState.asStateFlow().collectAsState()
        val transition = updateTransition(state)
        if (state != Init) {
            val entry by getShareStack().collectAsState(emptyList())
            entry.lastOrNull()?.run {
                groups.forEach {
                    transition.ShareElement(it, transitionSpec)
                }
            }
        }
        var resetState by remember { mutableStateOf<ShareState>(PreShare) }
        transition.segment.apply {
            when {
                Init isTransitioningTo BeforeShare -> (targetState as BeforeShare).run {
                    if (isForward) {
                        resetState = ExitShare
                        shareState.value = PreShare
                    } else {
                        resetState = PreShare
                        shareState.value = ExitShare
                    }
                }

                BeforeShare isTransitioningTo PreShare -> shareState.value = ExitShare
                BeforeShare isTransitioningTo ExitShare -> shareState.value = PreShare
            }
        }
        if (transition.currentState == resetState && transition.targetState == resetState) {
            shareState.value = Init
        }
    }

    @Composable
    fun Transition<ShareState>.ShareElement(
        group: ShareElementGroup,
        transitionSpec: @Composable Transition.Segment<ShareState>.() -> FiniteAnimationSpec<Rect>
    ) {
        val density = LocalDensity.current
        val startPosition by group.start.position.collectAsState()
        val endPosition by group.end.position.collectAsState()
        val rect by animateRect(label = "", transitionSpec = transitionSpec) {
            if (it == PreShare || it == BeforeShare && (it as BeforeShare).isForward) startPosition else endPosition
        }
        var target by remember { mutableStateOf<ShareElement?>(group.start) }
        if (currentState is BeforeShare) {
            target = if ((currentState as BeforeShare).isForward) group.start else group.end
        }
        Box(
            Modifier
                .size(with(density) { rect.size.toDpSize() })
                .offset {
                    IntOffset(rect.topLeft.x.roundToInt(), rect.topLeft.y.roundToInt())
                }
        ) {
            target?.content?.invoke(this@ShareElement)
        }
    }

}


/**
 * 定义共享控件的状态
 */
sealed interface ShareState {
    infix fun <T> T.startTransform(target: T): T {
        return if (preShare || this@ShareState == Init) this else target
    }

    infix fun <T> T.endTransform(target: T): T {
        return if (preShare) this else target
    }
}

/**
 * 无状态，显示原本的控件
 */
data object Init : ShareState

/**
 * 共享之前，在共享之前，需要显示共享控件，但是原本的控件也不能隐藏
 */
data object BeforeShare : ShareState {
    var isForward: Boolean = false
}

/**
 *开始共享
 */
data object PreShare : ShareState

/**
 * 共享结束
 */
data object ExitShare : ShareState

/**
 * 共享之后。需要同时显示共享控件和原本的控件
 */
data object AfterShare : ShareState

val ShareState.preShare get() = this is PreShare || (this is BeforeShare && this.isForward)
val ShareState.exitShare get() = this is ExitShare || (this is BeforeShare && !this.isForward)


