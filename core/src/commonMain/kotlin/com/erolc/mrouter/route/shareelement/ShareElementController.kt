package com.erolc.mrouter.route.shareelement

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import com.erolc.mrouter.platform.loge
import com.erolc.mrouter.route.transform.share.ShareTransformWrap
import com.erolc.mrouter.utils.*
import kotlinx.coroutines.flow.*
import kotlin.math.roundToInt


/**
 * 共享元素控制器
 * 控制共享元素的变动。
 *
 */
internal object ShareElementController {
    /**
     * 用于保存所有已产生的共享元素
     */
    private val elements = mutableMapOf<String, ShareElement>()

    /**
     * 共享状态
     */
    val shareState: MutableStateFlow<ShareState> = MutableStateFlow(Init)

    /**
     * 当进行共享时，将会在[elements]中提取成对的共享元素组装成[ShareEntry]并添加到堆栈中
     */
    val shareStack = MutableStateFlow(listOf<ShareEntry>())

    private val onUploadElements = mutableMapOf<String, UpdateElementListener>()


    val updateKeys = mutableMapOf<String, MutableList<Any>>()
    var updateEntry: ShareEntry? = null

    /**
     * 添加元素
     */
    internal fun addElement(element: ShareElement) {
        elements[element.tag] = element
        updateEntry(element)
    }

    /**
     * 缺失待更新的元素时，需要检查每一个添加进来的元素，是否符合该次共享，如果符合，将更新共享条目
     */
    private fun updateEntry(element: ShareElement) {
        if (updateKeys[element.address]?.remove(element.key) == true && updateEntry != null) {
            updateEntry = updateEntry?.run {
                val newGroups = getElementGroup(element.key, startAddress, endAddress)?.let {
                    groups + it
                }
                newGroups?.let { copy(groups = it) }
            }
        }
        if (updateEntry != null && updateKeys[element.address]?.isEmpty() == true) {
            shareStack.updateEntry(updateEntry!!)
            updateEntry = null
        }
    }

    /**
     * 移除元素
     */
    internal fun removeElement(tag: String) {
        elements.remove(tag)
    }

    /**
     * 获得共享元素状态
     */
    @Composable
    internal fun getShareState() = shareState.asStateFlow().collectAsState()

    fun addUploadElementListener(address: String, listener: UpdateElementListener) {
        onUploadElements[address] = listener
    }

    fun removeUploadElementListener(address: String) {
        onUploadElements.remove(address)
    }

    fun updateShareGroup(vararg keys: Any) {
        val oldEntry = shareStack.value.last()
        val newEntry = oldEntry.let {
            val group = it.groups.first()
            val start = group.start.address
            val end = group.end.address
            val newGroup = getElementGroups(keys, start, end)
            val keyStr = keys.makeKeys()
            if (newGroup.size == keys.size) {
                it.groups.forEach { it.start._state.value = Init }
                it.copy(groups = newGroup, keys = keyStr).apply {
                    groups.forEach { it.start._state.value = resetState.value }
                }
            } else {
                val newKeys = newGroup.map { it.key }
                val lackKeys = keys.toMutableList().run {
                    removeAll(newKeys)
                    this.toTypedArray()
                }
                updateKeys[start] = lackKeys.toMutableList()
                onUploadElements[start]?.invoke(lackKeys)
                it.groups.forEach { it.start._state.value = Init }
                updateEntry = it.copy(groups = newGroup, keys = keyStr).apply {
                    groups.forEach { it.start._state.value = resetState.value }
                }
                null
            }
        }
        newEntry?.let { shareStack.updateEntry(it) }
    }

    private fun Array<out Any>.makeKeys() = fold("") { a, b ->
        "${a.hashCode()}${b.hashCode()}|"
    }

    /**
     * 初始化共享
     */
    fun initShare(entry: PageEntry, endEntry: PageEntry) {
        val gesture = endEntry.transform.value.wrap
        if (gesture is ShareTransformWrap) {
            val keys = gesture.keys.makeKeys()
            val shareEntry = shareStack.value.lastOrNull()
            val startAddress = entry.address.path
            val endAddress = endEntry.address.path
            val groups = getElementGroups(gesture.keys, startAddress, endAddress)
            val findEntry = findEntry(keys, startAddress, endAddress)
            if (groups.isNotEmpty())
                if (findEntry != null && !findEntry.equalEntry(shareEntry)) {
                    val copy = findEntry.copy(groups = groups)
                    val index = shareStack.value.indexOf(findEntry)
                    shareStack.value = shareStack.value.subList(0, index) +
                            copy +
                            shareStack.value.subList(index + 1, shareStack.value.size)
                } else if (shareEntry != null && shareEntry.equalTag(
                        keys,
                        startAddress,
                        endAddress
                    )
                )
                    shareStack.updateEntry(shareEntry.copy(groups = groups))
                else {
                    shareStack.value += ShareEntry(
                        groups,
                        gesture.shareAnimationSpec,
                        startAddress,
                        endAddress,
                        keys
                    )
                    shareState.value = BeforeStart
                }
        }
    }

    private fun findEntry(key: String, startAddress: String, endAddress: String): ShareEntry? {
        return shareStack.value.find { it.equalTag(key, startAddress, endAddress) }
    }


    private fun MutableStateFlow<List<ShareEntry>>.updateEntry(newEntry: ShareEntry) {
        value -= value.last()
        value += newEntry
    }

    private fun getElementGroups(keys: Array<out Any>, start: String, end: String) =
        keys.mapNotNull { getElementGroup(it, start, end) }

    private fun getElementGroup(key: Any, start: String, end: String): ShareElementGroup? {
        val startTag = "${start}_${key.hashCode()}"
        val endTag = "${end}_${key.hashCode()}"
        val startEle = elements[startTag]
        val endEle = elements[endTag]
        return startEle?.let { endEle?.let { end -> ShareElementGroup(it, end, key) } }
    }

    internal fun sharing(progress: Float) {
        if (shareStack.value.isNotEmpty())
            shareState.value = Sharing(progress)
    }

    internal fun reset() {
        if (shareStack.value.isNotEmpty())
            shareState.value = ExitShare
    }

    /**
     * 后退共享过程
     */
    internal fun exitShare() {
        if (shareStack.value.isNotEmpty())
            shareState.value = BeforeEnd
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

    fun getShareStack() = shareStack.map { it.takeLast(1) }
}


@Composable
internal fun ShareElementController.Overlay() {
    val state by getShareState()
    val transition = updateTransition(state)
    if (state != Init) {
        val entry by getShareStack().collectAsState(emptyList())
        entry.lastOrNull()?.run {
            groups.forEach {
                it.start._state.value = state
                it.end._state.value = state
                val shareTransition = remember(it) {
                    ShareTransition(it.start, it.end, it.start, transition)
                }
                transition.ShareElement(it, shareTransition, shareAnimationSpec)
            }
        }
    }

    shareStack.value.lastOrNull()?.run {
        var resetState by remember { resetState }
        transition.segment.apply {
            when {
                Init isTransitioningTo BeforeStart -> {
                    resetState = ExitShare
                    shareState.value = PreShare
                }

                Init isTransitioningTo BeforeEnd -> {
                    resetState = PreShare
                    shareState.value = ExitShare
                }

                BeforeStart isTransitioningTo PreShare -> shareState.value = ExitShare
                BeforeEnd isTransitioningTo ExitShare -> shareState.value = PreShare
            }
        }
        if (transition.currentState == resetState && transition.targetState == resetState) {
            shareState.value = Init
            groups.forEach {
                it.start._state.value = if (resetState == ExitShare) resetState else Init
                it.end._state.value = Init
            }
        }
    }
}

@Composable
private fun Transition<ShareState>.ShareElement(
    group: ShareElementGroup,
    shareTransition: ShareTransition,
    shareAnimationSpec: FiniteAnimationSpec<Rect>
) {
    val density = LocalDensity.current
    val startPosition by group.start.position.collectAsState()
    val endPosition by group.end.position.collectAsState()
    val rect by animateRect(label = "", transitionSpec = { shareAnimationSpec }) {
        when (it) {
            PreShare, BeforeStart -> startPosition
            is Sharing -> it.updateRect(startPosition, endPosition)
            else -> endPosition
        }
    }
    var target by remember { mutableStateOf(group.start) }
    if (currentState is BeforeStart)
        target = group.start
    else if (currentState is BeforeEnd || currentState is Sharing)
        target = group.end
    shareTransition.targetElement = target
    Box(
        Modifier
            .size(with(density) { rect.size.toDpSize() })
            .offset {
                IntOffset(rect.topLeft.x.roundToInt(), rect.topLeft.y.roundToInt())
            }
    ) {
        target.content.invoke(shareTransition)
        //共享过程中，禁止点击
        Box(modifier = Modifier.fillMaxSize().clickable(MutableInteractionSource(), null) {})
    }
}
