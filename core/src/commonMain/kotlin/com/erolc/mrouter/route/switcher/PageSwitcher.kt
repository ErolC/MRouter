package com.erolc.mrouter.route.switcher

import androidx.compose.runtime.mutableStateOf


/**
 * @author erolc
 * @since 2024/2/18 15:07
 * 页面切换器，每个页面都有一个，负责自身的页面切换。以及记录自身的状态。
 * 但自身的页面切换也会影响上一个页面。但最多只会影响百分之10到20（这个数值需要测试调整）
 * ，于是到80视为打开，待下一个页面打开是将继续影响当前页面到100。
 */
@SinceKotlin("1.0")
class PageSwitcher {
    //切换进度，0-100，视为打开，100-0，视为关闭
    val progress = mutableStateOf(0)
}