package com.erolc.mrouter

import com.erolc.mrouter.register.RegisterBuilder
import com.erolc.mrouter.route.router.WindowRouter
import platform.UIKit.UIViewController

/**
 * 设置ViewController，只有设置了根ViewController才能从compose跳转到对应的VC上。
 */
fun RegisterBuilder.setViewController(rootViewController: UIViewController) {
    registerPlatformResource("root_vc", rootViewController)
}

internal fun WindowRouter.getRootViewController() = platformRes["root_vc"] as? UIViewController