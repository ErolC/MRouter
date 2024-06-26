package com.erolc.mrouter.model

import androidx.core.bundle.Bundle
import androidx.core.bundle.bundleOf
import com.erolc.mrouter.route.ResultCallBack
import com.erolc.mrouter.route.RouteFlag
import com.erolc.mrouter.scope.PageScope
import com.erolc.mrouter.route.transform.Transform

/**
 * 路由，由[PageScope.route]方法触发并构建，其中包含：
 * 路由代表前往一个页面的方式以及相关。
 * @param path 原始的路径
 * @param address 下一个页面的地址
 * @param args 携带到下一个页面的数据
 * @param windowOptions 页面负载到对应window的参数
 * @param callback 页面回退时可将参数从方法传回
 * @param panelOptions 局部面板的配置
 * @param transform 变换，页面跳转时的动画以及手势
 */
data class Route internal constructor(
    val path: String,
    val address: String,
    val flag: RouteFlag,
    val windowOptions: WindowOptions,
    val args: Bundle = bundleOf(),
    val callback: ResultCallBack?,
    val panelOptions: PanelOptions? = null,
    val transform: Transform = Transform.None,
)