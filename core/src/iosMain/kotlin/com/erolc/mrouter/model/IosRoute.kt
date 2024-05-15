package com.erolc.mrouter.model

import platform.UIKit.UIViewController

data class IosRoute(
    val target: UIViewController,
    val block: (source: IosRouteSource, target: UIViewController) -> Unit
)