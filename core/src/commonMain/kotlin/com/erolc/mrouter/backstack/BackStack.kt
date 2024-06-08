package com.erolc.mrouter.backstack

import com.erolc.mrouter.backstack.entry.PageEntry
import com.erolc.mrouter.backstack.entry.StackEntry
import com.erolc.mrouter.model.LaunchMode
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.model.SingleTop
import com.erolc.mrouter.route.ClearTaskFlag
import com.erolc.mrouter.route.ReplaceFlag
import com.erolc.mrouter.route.RouteFlag
import com.erolc.mrouter.route.StackFlag
import com.erolc.mrouter.route.router.Router
import com.erolc.mrouter.route.router.WindowRouter
import com.erolc.mrouter.route.shareelement.ShareElementController
import com.erolc.mrouter.route.transform.ExitState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


/**
 * 后退栈，保存着一个路由器中有关的所有元素
 */
open class BackStack(val name: String) {

    private val _backstack: MutableStateFlow<List<StackEntry>> = MutableStateFlow(listOf())

    val backStack: StateFlow<List<StackEntry>> = _backstack.asStateFlow()

    /**
     * 阈值，这个值将指示后退栈到达底部的时机
     */
    var threshold = 1

    //是否预后退
    private var isPreBack = false

    /**
     * 给回退栈增加一个元素
     */
    fun addEntry(entry: StackEntry) {
        if (entry is PageEntry)
            _backstack.value.takeLast(2).also {
                if (it.size == 2) {
                    (it.first() as PageEntry).isFrozen = true
                }
            }

        _backstack.value += entry
    }

    /**
     * 清空回退栈
     */
    private fun clearTask() {
        _backstack.value.take(_backstack.value.size - 1).forEach { (it as PageEntry).destroy() }
        _backstack.value = listOf(_backstack.value.last())
    }

    /**
     * 回退栈的大小
     */
    val size: Int get() = _backstack.value.size

    /**
     * 回退栈是否到达底部
     */
    fun isBottom() = size == threshold

    /**
     * 回退栈是否为空
     */
    fun isEmpty() = size == 0

    /**
     * 后退
     * @return 是否后退成功，只有在无法后退时才会后退失败，也就是下面没有过更多的页面可以后退了。
     */
    internal fun pop(isDestroy: Boolean = true): Boolean {
        return if (_backstack.value.size > threshold) {
            _backstack.value -= _backstack.value.last().apply {
                if (isPreBack)
                    isPreBack = false

                if (isDestroy) destroy()
            }
            _backstack.value.takeLast(2).also {
                it.map { (it as? PageEntry)?.isFrozen = false }
            }

            true
        } else false
    }

    internal fun execute(flag: RouteFlag) {
        flag.decode().filterIsInstance<StackFlag>().forEach {
            when (it) {
                is ClearTaskFlag, is ReplaceFlag -> clearTask()
            }
        }
    }

    /**
     * 预后退，page在后退时不可以直接[pop]，因为[pop]是无法做动画的。这里需要先预后退，通知框架需要后退。
     * 待切换动画完成之后再[pop]
     */
    fun preBack(parentRouter: Router): Boolean {
        return if (_backstack.value.size > threshold) {
            isPreBack = true
            val resumePage = _backstack.value.last() as PageEntry
            resumePage.transformState.value = ExitState
            ShareElementController.exitShare()
            true
        } else if (parentRouter is WindowRouter) {
            _backstack.value.forEach {
                (it as? PageEntry)?.isExit?.value = true
            }
            false
        } else false
    }

    /**
     * 找对应的条目，并不严谨，该方法不可用于寻找pageEntry
     */
    fun findEntry(address: String): StackEntry? =
        _backstack.value.find { it.address.match(address) }

    fun updateEntry(route: Route, launchMode: LaunchMode): Unit? {
        return if (launchMode == SingleTop) {
            findTopEntry()?.let {
                if (it.address.match(route.address)) {
                    _backstack.value -= it
                    val newEntry = PageEntry(it as PageEntry, route.args)
                    _backstack.value += newEntry
                } else null
            }
        } else null
    }

    /**
     * 找顶部的条目
     */
    fun findTopEntry() = _backstack.value.lastOrNull()

    fun updateEntries(oldEntries: List<StackEntry>, newEntries: List<StackEntry>) {
        _backstack.value -= oldEntries
        _backstack.value += newEntries
    }
}