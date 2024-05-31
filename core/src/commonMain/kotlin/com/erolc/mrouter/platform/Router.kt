package com.erolc.mrouter.platform

import androidx.core.bundle.Bundle
import com.erolc.mrouter.model.PlatformRoute
import com.erolc.mrouter.route.ResultCallBack
import com.erolc.mrouter.route.router.WindowRouter


internal expect fun WindowRouter.route(
    route: PlatformRoute,
    args: Bundle,
    callBack: ResultCallBack?
)