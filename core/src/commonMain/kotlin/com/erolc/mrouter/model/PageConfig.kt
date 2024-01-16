package com.erolc.mrouter.model

/**
 * @param size 当前页面的极限尺寸，first is min;second is max
 *
 * @param launchMode 启动模式
 */
data class PageConfig(
    val size: Pair<Int, Int> = 0 to 0,
    val launchSingleTop: Boolean = false
)