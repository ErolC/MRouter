package com.erolc.mrouter.platform

import androidx.core.bundle.Bundle
import com.erolc.mrouter.getRootViewController
import com.erolc.mrouter.model.IosRouteSource
import com.erolc.mrouter.model.PlatformRoute
import com.erolc.mrouter.route.ResourcePool
import com.erolc.mrouter.route.ResultCallBack
import com.erolc.mrouter.route.RouteDelegate
import com.erolc.mrouter.route.RouteUIViewControllerDelegate
import com.erolc.mrouter.route.router.WindowRouter
import com.erolc.mrouter.topViewController
import platform.UIKit.UIViewController

internal actual fun WindowRouter.route(
    route: PlatformRoute,
    args: Bundle,
    callBack: ResultCallBack?
) {
    val rootVC = getRootViewController() ?: topViewController
    val target = route.routerDispatcher as UIViewController
    val delegate = ResourcePool.getPlatformRes()["route_delegate"] as? RouteUIViewControllerDelegate
        ?: RouteDelegate
    delegate.route(IosRouteSource(rootVC, args, callBack), target)
}
