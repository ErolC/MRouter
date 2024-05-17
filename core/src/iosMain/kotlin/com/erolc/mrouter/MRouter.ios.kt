package com.erolc.mrouter

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ComposeUIViewController
import com.erolc.mrouter.route.router.WindowRouter
import platform.UIKit.UIViewController

/**
 * 设置ViewController，只有设置了根ViewController才能从compose跳转到对应的VC上。
 */
fun MRouter.setRootViewController(rootViewController: UIViewController) {
    rootRouter.setPlatformRes("root_vc", rootViewController)
}

internal fun WindowRouter.getRootViewController() = platformRes["root_vc"] as? UIViewController


fun mRouterComposeUIViewController(content: @Composable () -> Unit) =
    ComposeUIViewController { content() }.also { MRouter.setRootViewController(it) }
