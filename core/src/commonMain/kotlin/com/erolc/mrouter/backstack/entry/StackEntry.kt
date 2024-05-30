package com.erolc.mrouter.backstack.entry

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.erolc.mrouter.model.Address


/**
 * 后退栈的一个条目。代表一个页面/window
 */
interface StackEntry {
    /**
     * 该条目的地址
     */
    val address: Address

    /**
     * 界面内容
     */
    @Composable
    fun Content(modifier: Modifier)

    /**
     * 销毁方法
     */
    fun destroy()
}
