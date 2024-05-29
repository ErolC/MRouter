package com.erolc.mrouter.model

/**
 * @param launchMode 启动模式,只有当该页面当作普通页面时才有效
 */
data class PageConfig(
    val launchMode: LaunchMode = Standard
)

sealed interface LaunchMode

data object Standard:LaunchMode
data object SingleTop:LaunchMode

//data object SingleTask:LaunchMode