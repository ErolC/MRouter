package com.erolc.mrouter.route

import androidx.compose.runtime.*
import com.erolc.mrouter.lifecycle.rememberPageCoroutineScope
import com.erolc.mrouter.scope.LocalPageScope
import com.erolc.mrouter.utils.loge
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds

/**
 * 平台的后退实现
 */
@Composable
expect fun SysBackPressed(body: () -> Unit)

/**
 * 平台的退出实现
 */
@Composable
internal expect fun ExitImpl()

/**
 * 后退拦截器
 * @param enabled 是否开启拦截
 * @param onBack 拦截时的回调，需要注意的是[onBack]方法的接受者是[BackPressedHandler]，该对象有[BackPressedHandler.backPressed]方法可以跳过拦截直接后退。
 * ```
 * var enable by remember { mutableStateOf(true) }
 * BackInterceptor(enable) {
 *     enable = false // backPressed()
 * }
 * ```
 * 可以在若干次拦截之后改变enable的值以致下次后退不拦截，也可以在一次拦截中处理完某些事情之后调用[BackPressedHandler.backPressed]方法直接后退。
 * 和android的BackHandler的差别就是[BackPressedHandler.backPressed]方法。
 */
@Composable
fun BackInterceptor(enabled: Boolean = true, onBack: BackPressedHandler.() -> Unit) {
    val scope = LocalPageScope.current
    val currentOnBack by rememberUpdatedState(onBack)

    val interceptor = remember {
        object : BackInterceptor(enabled) {
            override fun onIntercept(callBack: BackPressedHandler) {
                currentOnBack(callBack)
            }
        }
    }

    SideEffect {
        interceptor.isEnabled = enabled
    }

    DisposableEffect(scope, interceptor) {
        scope.addBackInterceptor(interceptor)
        onDispose {}
    }

}

abstract class BackInterceptor(internal var isEnabled: Boolean) {
    abstract fun onIntercept(callBack: BackPressedHandler)
}


interface BackPressedHandler {

    /**
     * 后退，该后退事件将不会再拦截
     */
    fun backPressed()

}

internal class BackPressedHandlerImpl(private val onBack: () -> Unit) : BackPressedHandler {
    override fun backPressed() {
        onBack()
    }
}

/**
 * 退出
 * @param enable 是否启动拦截
 * @param delayTime 延迟若干时间后重新拦截监听，如果为[Duration.ZERO]那么将不会重新拦截，除非手动将enable重置为true
 * @param block 当进入非拦截期间，会显现该compose。即在该compose显现时再次出发回退，将执行退出App行为。
 */
@Composable
fun Exit(
    enable: MutableState<Boolean> = mutableStateOf(true),
    delayTime: Duration = 1000.milliseconds,
    block: @Composable () -> Unit = {}
) {
    val scope = rememberPageCoroutineScope()
    var interceptEnable by remember(enable) { enable }

    BackInterceptor(interceptEnable) {
        scope.launch {
            interceptEnable = false
            if (delayTime != ZERO) {
                delay(delayTime)
                interceptEnable = true
            }
        }
    }
    if (!interceptEnable) block()
}