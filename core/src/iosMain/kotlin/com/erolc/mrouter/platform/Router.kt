package com.erolc.mrouter.platform

import androidx.core.bundle.Bundle
import com.erolc.mrouter.getRootViewController
import com.erolc.mrouter.model.IosRouteSource
import com.erolc.mrouter.model.PlatformRoute
import com.erolc.mrouter.route.RouteDelegate
import com.erolc.mrouter.route.RouteUIViewControllerDelegate
import com.erolc.mrouter.route.router.WindowRouter
import platform.UIKit.UIViewController

internal actual fun WindowRouter.route(
    route: PlatformRoute,
    args: Bundle,
    onResult: (Bundle) -> Unit
) {
    val rootVC = getRootViewController()
    val target = route.routerDispatcher as UIViewController
    val delegate = platformRes["route_delegate"] as? RouteUIViewControllerDelegate ?: RouteDelegate
    rootVC?.let {
        delegate.route(IosRouteSource(it, args, onResult), target)
    } ?: loge("MRouter", "缺少RootVC，请设置RootVC")
}
