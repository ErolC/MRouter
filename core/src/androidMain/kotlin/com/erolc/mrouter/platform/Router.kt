package com.erolc.mrouter.platform

import androidx.activity.ComponentActivity
import androidx.core.bundle.Bundle
import com.erolc.mrouter.getContext
import com.erolc.mrouter.model.PlatformRoute
import com.erolc.mrouter.route.ActivityRouterDispatcher
import com.erolc.mrouter.route.router.WindowRouter

internal actual fun WindowRouter.route(
    route: PlatformRoute,
    args: Bundle,
    onResult: (Bundle) -> Unit
) {
    val activity = getContext() as ComponentActivity
    (route.routerDispatcher as? ActivityRouterDispatcher<*, *>)?.launch(activity, args, onResult)
}