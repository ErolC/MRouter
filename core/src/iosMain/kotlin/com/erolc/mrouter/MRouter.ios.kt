package com.erolc.mrouter
import com.erolc.mrouter.route.ResourcePool
import platform.UIKit.UIApplication
import platform.UIKit.UINavigationController
import platform.UIKit.UITabBarController
import platform.UIKit.UIViewController

/**
 * 设置ViewController，只有设置了根ViewController才能从compose跳转到对应的VC上。
 */
fun MRouter.setRootViewController(rootViewController: UIViewController) {
    setPlatformRes("root_vc", rootViewController)
}

internal fun getRootViewController() = ResourcePool.getPlatformRes()["root_vc"] as? UIViewController

val topViewController: UIViewController get() = topViewController()!!

private fun topViewController(currentVC: UIViewController? = UIApplication.sharedApplication.keyWindow()?.rootViewController): UIViewController? {
    return (currentVC as? UINavigationController)?.let {
        topViewController(it.visibleViewController)
    } ?: (currentVC as? UITabBarController)?.let {
        it.selectedViewController?.let(::topViewController) ?: currentVC
    } ?: currentVC?.presentedViewController?.let { topViewController(it) } ?: currentVC
}


