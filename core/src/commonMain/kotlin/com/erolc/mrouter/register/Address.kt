package com.erolc.mrouter.register

import androidx.compose.runtime.Composable
import com.erolc.mrouter.model.PageConfig

/**
 * 地址，用于定义一个页面地址
 * @param path
 */
data class Address(
    val path: String,
    val config: PageConfig = emptyConfig,
    val content: @Composable () -> Unit = {}
)
