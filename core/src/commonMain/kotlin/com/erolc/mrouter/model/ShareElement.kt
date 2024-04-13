package com.erolc.mrouter.model

import androidx.compose.runtime.Composable

/**
 * 一个共享元素
 * @param name 元素的名称
 * @param content 元素的compose
 * @param address 元素所在地址
 */
data class Element(val name: String, val content: @Composable () -> Unit, val address: String) {
    val tag get() = "${address}_$name"
}