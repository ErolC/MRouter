package com.erolc.mrouter.route.transform

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow

val LocalGestureWrapScope = staticCompositionLocalOf { GestureWrapScope() }

/**
 * 手势包裹层的作用域，该类主要是管理一些工作，比如：让页面内容也可以控制页面的手势
 */
class GestureWrapScope {
    /**
     * 控制手势的进度，0-1
     */
    val progress = MutableStateFlow(0f)
}