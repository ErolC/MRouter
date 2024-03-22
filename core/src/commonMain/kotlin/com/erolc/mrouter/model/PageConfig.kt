package com.erolc.mrouter.model

/**
 * @param launchSingleTop 启动模式,只有当该页面当作普通页面时才有效
 */
data class PageConfig(
    val launchSingleTop: Boolean = false
)