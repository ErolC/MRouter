package com.erolc.mrouter.backstack

import com.erolc.mrouter.utils.loge
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


/**
 * 后退栈，
 */
open class BackStack(val name: String) {

    private val _backstack: MutableStateFlow<List<StackEntry>> = MutableStateFlow(listOf())

    val backStack: StateFlow<List<StackEntry>> = _backstack.asStateFlow()

    /**
     * 阈值
     */
    internal var threshold = 1

    fun addEntry(entry: StackEntry) {
        _backstack.value += entry
    }

    val size: Int get() = _backstack.value.size

    fun isBottom() = size == 1

    fun isEmpty() = size == 0

    /**
     * 后退
     * @return 是否后退成功，只有在无法后退时才会后退失败，也就是下面没有过更多的页面可以后退了。
     */
    fun pop(): Boolean {
        return if (_backstack.value.size > threshold) {
            _backstack.value -= _backstack.value.last().apply { destroy() }
            true
        } else false
    }

    fun remove(id: String): Boolean {
        return if (_backstack.value.size > threshold) {
            _backstack.value = _backstack.value.filter {
                it.address.path != id
            }
            true
        } else false
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