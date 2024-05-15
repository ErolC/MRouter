package com.erolc.mrouter.route

import com.erolc.mrouter.model.IosRoute
import com.erolc.mrouter.model.IosRouteSource
import com.erolc.mrouter.register.RegisterBuilder
import platform.UIKit.UINavigationController
import platform.UIKit.UIViewController

/**
 * @param address 地址
 * @param target 目标VC
 * @param block 跳转的具体实现
 */
fun RegisterBuilder.platformRoute(
    address: String,
    target: UIViewController,
    block: (source: IosRouteSource, target: UIViewController) -> Unit = ::route
) = registerPlatformResource(address, IosRoute(target, block))

/**
 * 跳转的简单实现
 */
fun route(source: IosRouteSource, target: UIViewController) {
    when (val rootVC = source.rootVC) {
        is UINavigationController -> rootVC.pushViewController(target, true)
        else -> rootVC.presentViewController(target, true, null)
    }
}