package com.erolc.mrouter.route.shareele

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import com.erolc.mrouter.backstack.entry.PageEntry
import com.erolc.mrouter.model.ShareElement
import com.erolc.mrouter.model.ShareElementGroup
import com.erolc.mrouter.model.ShareEntry
import com.erolc.mrouter.route.transform.*
import com.erolc.mrouter.utils.loge
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlin.math.roundToInt


internal val LocalShareEleController = staticCompositionLocalOf { ShareEleController }

/**
 * 共享控制器
 * 控制共享的状态顺序为 init - preShare - exitShare - init。如此往复。
 *
 */
internal object ShareEleController {
    /**
     * 用于保存所有已产生的共享元素
     */
    internal val elements = mutableSetOf<ShareElement>()
    var transition: MutableState<Transition<TransformState>?> = mutableStateOf(null)
        internal set


    /**
     * 共享状态
     */
    internal val shareState: MutableStateFlow<ShareState> = MutableStateFlow(Init)

    /**
     * 当进行共享时，将会在[elements]中提取成对的共享元素组装成[ShareEntry]并添加到堆栈中
     */
    private val shareStack = MutableStateFlow(listOf<ShareEntry>())

    /**
     * 初始化共享过程
     */
    fun initShare(entry: PageEntry, endEntry: PageEntry) {
        transition.value = entry.scope.transformTransition
        val gesture = endEntry.transform.value.gesture
        //到这里时，end的元素尚未加入
        if (gesture is ShareGestureWrap) {
            val groups = gesture.keys.mapNotNull {
                val startTag = "${entry.address.path}_$it"
                val endTag = "${endEntry.address.path}_$it"
                val startEle = elements.find { it.tag == startTag }
                val endEle = elements.find { it.tag == endTag }
                startEle?.let { start -> endEle?.let { end -> ShareElementGroup(start, end) } }
            }
            shareStack.value += ShareEntry(groups)
            shareState.value = BeforeShare.apply { isForward = true }
        }
    }

    internal fun exitShare() {
        shareState.value = BeforeShare.apply { isForward = false }
    }

    /**
     * 共享之后
     */
    fun afterShare(entry: PageEntry) {
        shareState.value = Init
        val last = shareStack.value.lastOrNull()
        last?.let {
            if (it.groups.first().end.address == entry.address.path && shareStack.value.isNotEmpty())
                shareStack.value -= it
        }
    }

    private fun getShareStack() = shareStack.map { it.takeLast(1) }

    @Composable
    fun Overlay() {
        val state by shareState.asStateFlow().collectAsState()
        val transition = updateTransition(state)
        if (state != Init) {
            val entry by getShareStack().collectAsState(emptyList())
            entry.lastOrNull()?.groups?.forEach {
                transition.ShareElement(it)
            }
            loge("tag", "share_______${transition.currentState}")
            transition.segment.apply {
                when {
                    Init isTransitioningTo BeforeShare -> (targetState as BeforeShare).run {
                        if (isForward) shareState.value = PreShare else shareState.value = ExitShare
                    }

                    BeforeShare isTransitioningTo PreShare -> shareState.value = ExitShare
                    BeforeShare isTransitioningTo ExitShare -> shareState.value = PreShare
                }
            }
        }
    }

    @Composable
    fun Transition<ShareState>.ShareElement(group: ShareElementGroup) {
        val density = LocalDensity.current
        val startPosition by group.start.position.collectAsState()
        val endPosition by group.end.position.collectAsState()
        val rect by animateRect(label = "") {
            if (it == PreShare || it == BeforeShare) startPosition else endPosition
        }
        var target by remember { mutableStateOf<ShareElement?>(group.start) }
        if (targetState is BeforeShare) {
            target = if ((targetState as BeforeShare).isForward) group.start else group.end
        }

        Box(
            androidx.compose.ui.Modifier
                .size(with(density) { rect.size.toDpSize() })
                .offset {
                    IntOffset(rect.topLeft.x.roundToInt(), rect.topLeft.y.roundToInt())
                }
        ) {
            target?.content?.invoke()
        }
    }

}


/**
 * 定义共享控件的状态
 */
sealed interface ShareState

/**
 * 无状态，显示原本的控件
 */
data object Init : ShareState

/**
 * 共享之前，在共享之前，需要显示共享控件，但是原本的控件也不能隐藏
 */
object BeforeShare : ShareState {
    var isForward: Boolean = false
}

/**
 *开始共享
 */
data object PreShare : ShareState
//
///**
// * 共享中
// * @param progress 共享进度
// */
//data class Sharing(override val progress: Float = 0f) : ShareState(progress)

/**
 * 共享结束
 */
data object ExitShare : ShareState

/**
 * 共享之后。需要同时显示共享控件和原本的控件
 */
data object AfterShare : ShareState