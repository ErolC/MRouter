package com.erolc.mrouter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.erolc.mrouter.model.WindowOptions
import com.erolc.mrouter.register.RegisterBuilder

/**
 * 路由起点
 * @param startRoute 是路由的第一个页面
 * @param startWindowOptions 第一个window的配置，只有桌面端才有用
 * @param builder 用于注册的构建方法。[startRoute]对应的页面需要也在其中
 */
@Composable
fun RouteHost(
    startRoute: String,
    startWindowOptions: WindowOptions = WindowOptions(Constants.DEFAULT_WINDOW, ""),
    builder: RegisterBuilder.() -> Unit
) {
    remember(startRoute, builder, startWindowOptions) {
        MRouter.build(startRoute, startWindowOptions, builder)
    }
    val backStack by MRouter.getRootBlackStack()
    backStack.forEach { it.Content(Modifier) }
}