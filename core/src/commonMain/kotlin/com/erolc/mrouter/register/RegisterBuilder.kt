package com.erolc.mrouter.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.erolc.mrouter.Constants
import com.erolc.mrouter.model.PageConfig
import com.erolc.mrouter.model.WindowOptions
import com.erolc.mrouter.route.router.WindowRouter
import com.erolc.mrouter.route.routeBuild
import com.erolc.mrouter.utils.logi

/**
 * 空白的页面配置
 */
val emptyConfig = PageConfig()

/**
 * 注册页面
 * @param path 页面的地址
 * @param config 页面配置
 * @param content 页面
 */
fun RegisterBuilder.page(
    path: String,
    config: PageConfig = emptyConfig,
    content: @Composable () -> Unit
) {
    addAddress(Address(path, config, content))
}

/**
 * @author erolc
 * @since 2023/11/6 16:15
 * 注册范围构建，在注册范围中可以对页面进行注册
 */
@SinceKotlin("1.0")
class RegisterBuilder internal constructor() {
    private val addresses = mutableListOf<Address>()

    init {
        addAddress(Address(Constants.defaultPage, emptyConfig) {
            Box(Modifier.background(Color.White).fillMaxSize())
        })
    }

    /**
     * 添加地址，需要注意的是相同path的address会被覆盖
     */
    internal fun addAddress(address: Address) =
        addEntryToList(addresses, address) { it.path == address.path }


    private fun  addEntryToList(list: MutableList<Address>, entry: Address, body: (Address) -> Boolean) {
        val index = list.indexOfFirst(body)
        if (index == -1) list += entry else {
            logi("route","${entry.path}的address已经覆盖更新，请知悉")
            list[index] = entry
        }
    }

    /**
     * 构建，window路由器，并分配第一个路由
     */
    internal fun builder(startRoute: String, options: WindowOptions): WindowRouter {
        //构建路由器并路由到初始页面
        return WindowRouter(addresses).apply {
            dispatchRoute(routeBuild(startRoute).copy(windowOptions = options))
        }
    }

}