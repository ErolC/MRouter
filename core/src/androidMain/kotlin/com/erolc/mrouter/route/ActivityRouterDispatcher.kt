package com.erolc.mrouter.route

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContract
import java.util.concurrent.atomic.AtomicInteger

/**
 * Activity路由器的分配器
 */
class ActivityRouterDispatcher<I, O>(
    private val contract: ActivityResultContract<I, O>,
    private val block: (ActivityRouter<I, O>) -> Unit
) {
    private val router = ActivityRouter<I, O>()
    private val nextLocalRequestCode = AtomicInteger()

    fun launch(activity: ComponentActivity, args: Bundle, onResult: (Bundle) -> Unit) {
        router.context = activity
        router.args = args
        block(router)
        val launcher = activity.activityResultRegistry.register(
            "mrouter_rq#${nextLocalRequestCode.getAndDecrement()}",
            contract
        ) {
            router.onResult?.invoke(it)
            onResult(router.result)
        }
        router.input?.let { launcher.launch(it) }
    }
}