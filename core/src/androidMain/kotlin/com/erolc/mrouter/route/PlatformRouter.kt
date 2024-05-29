package com.erolc.mrouter.route

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.bundle.Bundle
import com.erolc.mrouter.model.PlatformRoute
import com.erolc.mrouter.register.Register
import kotlin.reflect.KClass


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
fun <I, O> Register.platformRoute(
    address: String,
    contract: ActivityResultContract<I, O>,
    block: (ActivityRouter<I, O>) -> Unit
) {
    addPlatformResource(address, PlatformRoute(ActivityRouterLauncher(contract, block)))
}

fun <I, O> ActivityRouter<I, O>.route(input: I, block: Bundle.(O) -> Unit = {}) {
    invoke(input) { result ->
        setResult { block(result) }
    }
}

/**
 * 设置跳转setting界面
 */
fun Register.setting(
    address: String,
    setting: String,
    intentBody: Intent.(args: Bundle) -> Unit = {}
) {
    platformRoute(address, ActivityResultContracts.StartActivityForResult()) {
        it(Intent(setting).also { intent ->
            intent.intentBody(it.args)
        }) { result ->
            it.setResult {
                result.data?.extras?.let { putAll(it) }
            }
        }
    }
}

/**
 * 打开activity
 * ```
 * MRouter.registerBuilder {
 *    startActivity("test_activity",TestActivity::class)
 * }
 * ```
 */
fun Register.startActivity(
    address: String,
    activityKClass: KClass<out Activity>,
    intentBody: Intent.(args: Bundle) -> Unit = {},
    block: Bundle.(ActivityResult) -> Unit = { it.data?.extras?.let { putAll(it) } }
) {
    platformRoute(address, ActivityResultContracts.StartActivityForResult()) {
        it.route(it.intent(activityKClass) { intentBody(it.args) }) {
            block(it)
        }
    }
}

