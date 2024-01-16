package com.erolc.mrouter.backstack

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WindowStack {

    internal val _backstackMap: MutableStateFlow<Map<String, StackEntry>> = MutableStateFlow(mapOf())

    val backStackMap: StateFlow<Map<String, StackEntry>> = _backstackMap.asStateFlow()

    fun addEntry(entry: StackEntry) {
        _backstackMap.value += entry.address.path to entry
    }

    val size: Int get() = _backstackMap.value.size

    fun isBottom() = size == 1

    /**
     * 后退
     * @return 是否后退成功，只有在无法后退时才会后退失败，也就是下面没有过更多的页面可以后退了。
     */
    fun pop(id: String = ""): Boolean {
        return if (_backstackMap.value.size > 1) {
            _backstackMap.value = _backstackMap.value.filter { it.key != id }
            true
        } else false
    }

    /**
     * 找对应的条目
     */
    fun findEntry(address: String) = _backstackMap.value.containsKey(address)
}