package com.erolc.mrouter.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import com.erolc.mrouter.lifecycle.rememberPageCoroutineScope
import com.erolc.mrouter.scope.LocalPageScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


@Composable
expect fun SysBackPressed(body: () -> Unit)

/**
 * 执行到立马退出
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

    val interceptor = remember(enabled) {
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
 * @param doubleConfirm 为true时开启双重确认。目前android和desktop已生效
 */
@Composable
fun Exit(
    doubleConfirm: Boolean = false,
    delayTime: Duration = 1000.milliseconds,
    msg: String = ""
) {
    val scope = rememberPageCoroutineScope()
//    val scope = rememberCoroutineScope()
    var enable by remember { mutableStateOf(false) }
    var preEnable by remember { mutableStateOf(false) }
    BackInterceptor {
        if (doubleConfirm) {
            if (preEnable) enable = true
            preEnable = true
            scope?.launch {
                delay(delayTime)
                //todo toast
                preEnable = false
            }
        } else enable = true
    }
    if (enable) ExitImpl()

}