package com.erolc.mrouter

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
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

/**
 * 获取[Intent]
 */
fun ActivityRouter<Intent, ActivityResult>.intent(
    activityKClass: KClass<out Activity>,
    block: Intent.() -> Unit = {}
): Intent {
    return Intent(context, activityKClass.java).also(block)
}

/**
 * 简化单Activity跳转以及其返回值配置
 */
fun ActivityRouter<Intent, ActivityResult>.route(
    activityKClass: KClass<out Activity>,
    block: Intent.() -> Unit = {}
) {
    route(intent(activityKClass) {
        putExtras(this@route.args)
        block()
    }) {
        setResult { putAll(it.data?.extras) }
    }
}

/**
 * 注册路由到activity的部分
 * ```
 * MRouter.registerBuilder {
 *             routeActivity("test_activity", ActivityResultContracts.StartActivityForResult()) {
 *                 it.route(TestActivity::class)
 *             }
 *         }
 * ```
 * @param address 地址
 * @param contract ActivityResultContract
 * @param block 配置路由过程
 */
fun <I, O> RegisterBuilder.routeActivity(
    address: String,
    contract: ActivityResultContract<I, O>,
    block: (ActivityRouter<I, O>) -> Unit
) {
    registerPlatformResource(address, PlatformRoute(ActivityRouterDispatcher(contract, block)))
}

fun <I, O> ActivityRouter<I, O>.route(input: I, block: Bundle.(O) -> Unit = {}) {
    invoke(input) { result ->
        setResult { block(result) }
    }
}

internal actual fun WindowRouter.route(
    route: PlatformRoute,
    args: Bundle,
    onResult: (Bundle) -> Unit
) {
    val activity = getContext() as ComponentActivity
    loge("tag", "${args.getBoolean("return")} -- ")
    (route.routerDispatcher as? ActivityRouterDispatcher<*, *>)?.launch(activity, args, onResult)
}