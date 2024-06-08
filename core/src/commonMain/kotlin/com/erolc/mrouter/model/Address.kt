package com.erolc.mrouter.model

import androidx.compose.runtime.Composable
import com.erolc.mrouter.register.emptyConfig

/**
 * 地址，用于定义一个页面地址
 * @param path 该地址的路径
 * @param config 页面的配置
 * @param content 页面内容本体
 * @param matchKey 用于匹配的key
 */
data class Address(
    val path: String,
    val config: PageConfig = emptyConfig,
    val content: @Composable () -> Unit = {},
    val matchKey:String = path
){
    /**
     * 匹配地址
     */
    fun match(address:String) = matchKey.toRegex().matches(address)
}
