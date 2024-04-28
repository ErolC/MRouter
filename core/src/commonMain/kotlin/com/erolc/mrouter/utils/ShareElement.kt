package com.erolc.mrouter.utils

import androidx.compose.runtime.*
import com.erolc.mrouter.route.shareelement.ShareElementController
import com.erolc.mrouter.scope.LocalPageScope

typealias UpdateElementListener = (Array<out String>) -> Unit


/**
 * 更新元素，在使用共享元素动画跳转到目标页面之后，可以通过该方法更新共享的元素，（必须是原页面和当前界面都有的元素），那么在退出的时候，就可以使用新的共享元素实现动画。
 * 重申一遍，必须要在当前界面存在该元素时，才可更新，如果原页面不存在该元素，则会通过[onUpdateElement]进行通知，如果更新失败，那么将按照原共享元素执行。
 */
fun updateElement(vararg keys: String) {
    ShareElementController.updateShareGroup(*keys)
}

/**
 * 正在更新元素，当调用[updateElement]方法时，共享元素控制器就会在已有的共享元素中查找新的用来实现动画的元素，如果找不到则会触发该回调，用户需要在原有界面准备新元素，以便更新共享元素
 * @param block 该回调函数的参数是缺少的元素的key集合
 */
@Composable
fun onUpdateElement(block: (Array<out String>) -> Unit) {
    val scope = LocalPageScope.current
    DisposableEffect(scope) {
        ShareElementController.addUploadElementListener(scope.name, block)
        onDispose {
            ShareElementController.removeUploadElementListener(scope.name)
        }
    }
}


/**
 * 定义共享控件的状态
 */
sealed interface ShareState{

    /**
     * current between target
     * 需要注意的是，target需要和目标页面对应的共享元素的current一致
     */
    infix fun <T> T.between(target: T): T {
        return if (!preShare) this else target
    }
}

/**
 * 无状态，显示原本的控件
 */
internal data object Init : ShareState

/**
 * 共享之前，在共享之前，需要显示共享控件，但是原本的控件也不能隐藏
 */
internal data object BeforeStart : ShareState

internal data object BeforeEnd : ShareState

/**
 *开始共享
 */
internal data object PreShare : ShareState

/**
 * 共享结束
 */
internal data object ExitShare : ShareState

val ShareState.preShare get() = this is PreShare



