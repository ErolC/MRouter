package com.erolc.mrouter.platform

import androidx.core.bundle.Bundle
import androidx.core.bundle.bundleOf
import com.erolc.mrouter.getRootViewController
import com.erolc.mrouter.model.IosRoute
import com.erolc.mrouter.model.IosRouteSource
import com.erolc.mrouter.model.PlatformRoute
import com.erolc.mrouter.route.router.WindowRouter
import com.erolc.mrouter.utils.loge

internal actual fun WindowRouter.route(
    route: PlatformRoute,
    args: Bundle,
    onResult: (Bundle) -> Unit
) {
    val rootVC = getRootViewController()
    val iosRoute = route.routerDispatcher as IosRoute
    rootVC?.let {
        val result = bundleOf()
        iosRoute.block(IosRouteSource(it, args, result), iosRoute.target)
        onResult(result)
    } ?: loge("MRouter", "缺少RootVC，请设置RootVC")
}
