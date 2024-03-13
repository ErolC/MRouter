package com.erolc.mrouter.model

/**
 * @param launchSingleTop 启动模式,只有当该页面当作普通页面时才有效，如果是当作dialog或者其他介质的显示则无效
 */
data class PageConfig(
    val launchSingleTop: Boolean = false
)