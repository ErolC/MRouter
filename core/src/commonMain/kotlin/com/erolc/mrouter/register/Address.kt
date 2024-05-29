package com.erolc.mrouter.register

import androidx.compose.runtime.Composable
import com.erolc.mrouter.Constants
import com.erolc.mrouter.model.PageConfig

/**
 * 地址，用于定义一个页面地址
 * @param path 该地址的路径
 * @param config 页面的配置
 * @param content 页面内容本体
 */
data class Address(
    val path: String,
    val config: PageConfig = emptyConfig,
    val content: @Composable () -> Unit = {},
    val matchKey:String = path
){
    fun match(address:String) = matchKey.toRegex().matches(address)
}
