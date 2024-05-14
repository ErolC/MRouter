package com.erolc.mrouter

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.bundle.Bundle
import com.erolc.mrouter.model.PlatformRoute
import com.erolc.mrouter.register.RegisterBuilder
import com.erolc.mrouter.route.ActivityRouterDispatcher
import com.erolc.mrouter.route.ActivityRouter
import com.erolc.mrouter.route.router.WindowRouter
import com.erolc.mrouter.utils.loge
import kotlin.reflect.KClass

/**
 * context
 */
internal fun WindowRouter.getContext() = platformRes[Constants.CONTEXT] as Context
internal actual fun WindowRouter.route(
    route: PlatformRoute,
    args: Bundle,
    onResult: (Bundle) -> Unit
) {
    val activity = getContext() as ComponentActivity
    (route.routerDispatcher as? ActivityRouterDispatcher<*, *>)?.launch(activity, args, onResult)
}
