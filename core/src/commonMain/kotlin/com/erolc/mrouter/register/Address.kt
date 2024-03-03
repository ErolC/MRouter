package com.erolc.mrouter.register

import androidx.compose.runtime.Composable
import com.erolc.mrouter.model.PageConfig

/**
 * 地址，用于定义一个页面地址
 * @param path
 */
open class Address(
    open val path: String,
    open val config: PageConfig = emptyConfig,
    open val content: @Composable () -> Unit = {}
)
