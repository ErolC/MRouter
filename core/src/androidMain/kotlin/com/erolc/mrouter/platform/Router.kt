package com.erolc.mrouter.platform

import androidx.activity.ComponentActivity
import androidx.core.bundle.Bundle
import com.erolc.mrouter.getContext
import com.erolc.mrouter.model.PlatformRoute
import com.erolc.mrouter.route.ActivityRouterLauncher
import com.erolc.mrouter.route.ResultCallBack
import com.erolc.mrouter.route.router.WindowRouter

internal actual fun WindowRouter.route(
    route: PlatformRoute,
    args: Bundle,
    callBack: ResultCallBack?
) {
    val activity = getContext() as ComponentActivity
    (route.routerDispatcher as? ActivityRouterLauncher<*, *>)?.launch(activity, args, callBack)
}