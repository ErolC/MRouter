package com.erolc.mrouter.dialog

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

/**
 * dialog 选项
 * @param alignment 位置；与哪里对齐
 * @param dismissOnBackPress 是否可观察后退进而关闭
 * @param dismissOnClickOutside 是否可观察点击dialog外部进而关闭
 * @param usePlatformDefaultWidth 使用平台默认的宽度
 * @param usePlatformInsets 使用平台默认的insets
 * @param scrimColor dialog 外部背景颜色
 * @param enter 打开dialog的动画
 * @param exit 关闭dialog的动画
 * @param isShowDialog
 */
data class DialogOptions(
    val alignment: Alignment = Alignment.Center,
    val dismissOnBackPress: Boolean = true,
    val dismissOnClickOutside: Boolean = true,
    val usePlatformDefaultWidth: Boolean = true,
    val usePlatformInsets: Boolean = true,
    val scrimColor: Color,
    val decorFitsSystemWindows: Boolean = true,
    val secureFlagPolicy:Boolean? = null,
    val enter:EnterTransition,
    val exit:ExitTransition,
    val isShowDialog: MutableState<Boolean> = mutableStateOf(false)
)