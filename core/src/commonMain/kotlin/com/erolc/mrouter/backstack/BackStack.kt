package com.erolc.mrouter.backstack

import com.erolc.mrouter.backstack.entry.LocalPageEntry
import com.erolc.mrouter.backstack.entry.PageEntry
import com.erolc.mrouter.backstack.entry.StackEntry
import com.erolc.mrouter.route.transform.PostExit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first


/**
 * 后退栈，
 */
open class BackStack(val name: String) {

    private val _backstack: MutableStateFlow<List<StackEntry>> = MutableStateFlow(listOf())

    val backStack: StateFlow<List<StackEntry>> = _backstack.asStateFlow()

    /**
     * 阈值，这个值将指示后退栈到达底部的时机
     */
    var threshold = 1
    private var isPreBack = false

    fun addEntry(entry: StackEntry) {
        _backstack.value += entry
    }

    fun addEntryWithFirst(entry: StackEntry) {
        _backstack.value = listOf(entry) + _backstack.value
    }

    val size: Int get() = _backstack.value.size

    fun isBottom() = size == threshold

    fun isEmpty() = size == 0

    /**
     * 后退
     * @return 是否后退成功，只有在无法后退时才会后退失败，也就是下面没有过更多的页面可以后退了。
     */
    internal fun pop(): Boolean {
        return if (_backstack.value.size > threshold) {
            _backstack.value -= _backstack.value.last().apply {
                if (this is LocalPageEntry && isPreBack) {
                    isPreBack = false
                    entry.pageRouter.backStack.reset()
                }
                destroy()
            }
            true
        } else false
    }

    internal fun reset() {
        _backstack.value = listOf(_backstack.value.first())
    }

    internal fun clear() {
        _backstack.value = listOf()
    }

    /**
     * 预后退，page在后退时不可以直接[pop]，因为[pop]是无法做动画的。这里需要先预后退，通知框架需要后退。
     * 待切换动画完成之后再[pop]
     */
    fun preBack(): Boolean {
        return if (_backstack.value.size > threshold) {
            isPreBack = true
            val resumePage = _backstack.value.last() as PageEntry
            resumePage.transformState.value = PostExit
            true
        } else {
            _backstack.value.forEach {
                (it as? PageEntry)?.isExit?.value = true
            }
            false
        }
    }

    /**
     * 找对应的条目
     */
    fun findEntry(address: String) = _backstack.value.find { it.address.path == address }

    /**
     * 找顶部的条目
     */
    fun findTopEntry() = _backstack.value.lastOrNull()
}