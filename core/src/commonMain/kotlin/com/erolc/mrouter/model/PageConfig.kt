package com.erolc.mrouter.model

/**
 * @param size 当前页面的极限尺寸，first is min;second is max
 * [size]在局部页面中有用
 * @param launchMode 启动模式
 */
data class PageConfig(
    val size: Pair<Int, Int> = 0 to 0,
    val launchSingleTop: Boolean = false
)