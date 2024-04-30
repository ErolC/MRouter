package com.erolc.mrouter.route.transform

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf

val LocalTransformWrapScope = compositionLocalOf { TransformWrapScope() }

/**
 * 手势包裹层的作用域，该类主要是管理一些工作，比如：让页面内容也可以控制页面的手势
 */
class TransformWrapScope {
    /**
     * 控制手势的进度，0-1:关闭的进度
     */
    val progress = mutableStateOf(0f)
}