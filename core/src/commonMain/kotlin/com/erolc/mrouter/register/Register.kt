package com.erolc.mrouter.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.erolc.mrouter.Constants
import com.erolc.mrouter.model.Address
import com.erolc.mrouter.model.PageConfig
import com.erolc.mrouter.platform.logi
import com.erolc.mrouter.route.ResourcePool

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
fun Register.page(
    path: String,
    config: PageConfig = emptyConfig,
    content: @Composable () -> Unit
) = addAddress(Address(path = path, config = config, content = content))

/**
 * 构建一个模块
 */
fun Register.module(
    module: String,
    content: RegisterModuleBuilder.() -> Unit
) = RegisterModuleBuilder(this, module).apply(content)


class RegisterModuleBuilder internal constructor(
    private val builder: Register,
    private val module: String
) {

    /**
     * 注册页面
     * @param path 页面的地址
     * @param config 页面配置
     * @param content 页面
     */
    fun page(
        path: String,
        config: PageConfig = emptyConfig,
        content: @Composable () -> Unit
    ) {
        builder.addAddress(Address("$module/$path", config, content))
    }
}


/**
 * @author erolc
 * @since 2023/11/6 16:15
 * 注册范围构建，在注册范围中可以对页面进行注册
 */
@SinceKotlin("1.0")
class Register internal constructor() {

    private val addresses = mutableListOf<Address>()

    //用于注册平台的compose资源，比如desktop的Menu
    private val platformRes = mutableMapOf<String, Any>()

    init {
        addAddress(Address(path = Constants.DEFAULT_PAGE, config = emptyConfig, {
            Box(Modifier.background(Color.White).fillMaxSize())
        }))
    }

    /**
     * 注册平台资源
     */
    fun addPlatformResource(key: String, target: Any) {
        platformRes[key] = target
    }

    /**
     * 添加地址，需要注意的是相同path的address会被覆盖
     */
    internal fun addAddress(address: Address) {
        addEntryToList(addresses, address) { it.path == address.path }
    }


    private fun addEntryToList(
        list: MutableList<Address>,
        entry: Address,
        body: (Address) -> Boolean
    ) {
        val index = list.indexOfFirst(body)
        if (index == -1) list += entry else {
            logi("MRouter", "${entry.path}的页面已经覆盖更新，请知悉")
            list[index] = entry
        }
    }


    internal fun register() {
        ResourcePool.addAll(addresses, platformRes)
    }

}