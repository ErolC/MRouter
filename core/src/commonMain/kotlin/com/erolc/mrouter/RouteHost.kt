package com.erolc.mrouter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.erolc.mrouter.model.WindowOptions
import com.erolc.mrouter.register.Register

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
    builder: Register.() -> Unit
) {
    val viewModelStoreOwner = LocalViewModelStoreOwner.current ?: rememberViewModelStoreOwner()
    MRouter.setViewModelStore(viewModelStoreOwner.viewModelStore)
    remember(startRoute, builder, startWindowOptions) {
        MRouter.build(startRoute, startWindowOptions, builder)
    }
    val backStack by MRouter.getRootBlackStack()
    backStack.forEach { it.Content(Modifier) }
}


private class ComposeViewModelStoreOwner: ViewModelStoreOwner {
    override val viewModelStore: ViewModelStore = ViewModelStore()
    fun dispose() { viewModelStore.clear() }
}

/**
 * Return remembered [ViewModelStoreOwner] with the scope of current composable.
 *
 * TODO: Consider to move it to `lifecycle-viewmodel-compose` and upstream this to AOSP.
 */
@Composable
private fun rememberViewModelStoreOwner(): ViewModelStoreOwner {
    val viewModelStoreOwner = remember { ComposeViewModelStoreOwner() }
    DisposableEffect(viewModelStoreOwner) {
        onDispose { viewModelStoreOwner.dispose() }
    }
    return viewModelStoreOwner
}
