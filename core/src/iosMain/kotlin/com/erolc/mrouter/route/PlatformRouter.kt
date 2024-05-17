package com.erolc.mrouter.route

import com.erolc.mrouter.MRouter
import com.erolc.mrouter.model.IosRouteSource
import com.erolc.mrouter.model.PlatformRoute
import com.erolc.mrouter.register.RegisterBuilder
import platform.UIKit.UINavigationController
import platform.UIKit.UIViewController

/**
 * @param address 地址
 * @param target 目标VC
 * @param block 跳转的具体实现
 */
fun RegisterBuilder.platformRoute(address: String, target: UIViewController) =
    registerPlatformResource(address, PlatformRoute(target))

/**
 * 注册路由代理
 */
fun MRouter.registerRouteDelegate(delegate: RouteUIViewControllerDelegate) {
    rootRouter.setPlatformRes("route_delegate", delegate)
}

interface RouteUIViewControllerDelegate {
    fun route(source: IosRouteSource, target: UIViewController)
}

/**
 * 跳转的简单实现
 */
internal object RouteDelegate : RouteUIViewControllerDelegate {
    /**
     * 一个简单的实现，这里并没有做数据传递
     */
    override fun route(source: IosRouteSource, target: UIViewController) {
        when (val rootVC = source.rootVC) {
            is UINavigationController -> rootVC.pushViewController(target, true)
            else -> rootVC.presentViewController(target, true, null)
        }
    }

}
