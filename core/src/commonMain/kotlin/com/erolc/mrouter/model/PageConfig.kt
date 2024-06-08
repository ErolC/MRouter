package com.erolc.mrouter.model

/**
 * @param launchMode 启动模式,只有当该页面当作普通页面时才有效
 */
data class PageConfig(
    val launchMode: LaunchMode = Standard
)

sealed interface LaunchMode

/**
 * 标准，启动每一个页面都是一个新的实例
 */
data object Standard:LaunchMode

/**
 * 如果后退栈顶部是这个页面，那么将不创建页面，其他情况下和标准无异
 */
data object SingleTop:LaunchMode