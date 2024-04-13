package com.erolc.mrouter.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Rect
import kotlinx.coroutines.flow.StateFlow

/**
 * 一个共享元素
 * @param name 元素的名称
 * @param content 元素的compose
 * @param address 元素所在地址
 */
@Immutable
data class ShareElement(
    val name: String,
    val content: @Composable () -> Unit,
    val address: String,
    val position: StateFlow<Rect>
) {
    val tag get() = "${address}_$name"
}