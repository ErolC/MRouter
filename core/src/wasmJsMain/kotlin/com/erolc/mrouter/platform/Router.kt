package com.erolc.mrouter.platform

import androidx.core.bundle.Bundle
import com.erolc.mrouter.model.PlatformRoute
import com.erolc.mrouter.model.WebRoute
import com.erolc.mrouter.route.ResultCallBack
import com.erolc.mrouter.route.router.WindowRouter
import kotlinx.browser.window


internal actual fun WindowRouter.route(
    route: PlatformRoute,
    args: Bundle,
    callBack: ResultCallBack?
) {
    val webRoute = route.routerSource as WebRoute
    val url = if (args.isEmpty()) webRoute.url else {
        val str = StringBuilder()
        args.keySet().forEach {
            if (str.isNotEmpty())
                str.append("&")
            val data = args[it]
            str.append("$it=$data")
        }
        "${webRoute.url}?$str"
    }
    window.open(url, webRoute.target, webRoute.features)
}