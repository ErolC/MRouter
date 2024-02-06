package com.erolc.mrouter.register

import androidx.compose.runtime.Composable
import com.erolc.mrouter.Constants
import com.erolc.mrouter.backstack.BackStack
import com.erolc.mrouter.model.PageConfig
import com.erolc.mrouter.model.WindowOptions
import com.erolc.mrouter.route.Router
import com.erolc.mrouter.route.WindowRouter
import com.erolc.mrouter.route.routeBuild
import com.erolc.mrouter.scope.GroupScope

val emptyConfig = PageConfig()

/**
 * 注册普通页面
 * @param path 页面的地址，是一个不包含‘/’的字符串
 * @param config 页面配置
 * @param content 页面
 */
fun RegisterBuilder.page(
    path: String,
    config: PageConfig = emptyConfig,
    content: @Composable () -> Unit
) {
    require(!path.contains("/")) {
        "path can not have '/'"
    }
    addAddress(Address(path, config, content))
}

/**
 * 注册页面组，页面组也是一个页面，和普通页面相比，其页面内部的[GroupScope]拥有[GroupScope.layout]方法。使用该方法可以在页面中占位。有助于实现一些效果，比如：动态页面
 * @param path 页面的地址，是一个不包含‘/’的字符串
 * @param config 页面配置
 * @param content 页面
 */
fun RegisterBuilder.groupPage(
    path: String,
    config: PageConfig = emptyConfig,
    content: @Composable () -> Unit
) {
    require(!path.contains("/")) {
        "path can not have '/'"
    }
    addAddress(GroupAddress(path, config, content))
}


/**
 * @author erolc
 * @since 2023/11/6 16:15
 * 注册范围，在注册范围中可以对页面进行注册
 */
@SinceKotlin("1.0")
class RegisterBuilder internal constructor() {
    private val addresses = mutableListOf<Address>()

    init {
        addAddress(Address(Constants.defaultPage, emptyConfig) {
            //todo default page
        })
    }

    /**
     * 添加地址，需要注意的是相同path的地址会被覆盖
     */
    internal fun addAddress(address: Address) =
        addEntryToList(addresses, address) { it.path == address.path }


    private fun <T> addEntryToList(list: MutableList<T>, entry: T, body: (T) -> Boolean) {
        val index = list.indexOfFirst(body)
        if (index == -1) list += entry else list[index] = entry
    }


    internal fun builder(startRoute: String, options: WindowOptions): WindowRouter {
        //构建路由器并路由到初始页面
        return WindowRouter(addresses).apply {
            dispatchRoute(routeBuild(startRoute).copy(windowOptions = options))
        }
    }

}