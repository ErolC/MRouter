package com.erolc.mrouter.utils


import androidx.compose.animation.VectorConverter
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.erolc.mrouter.route.shareelement.ShareElementController
import com.erolc.mrouter.scope.LocalPageScope

typealias UpdateElementListener = (Array<out Any>) -> Unit

typealias ShareAnim<T> = (ShareState) -> T

typealias ShareAnimBody<T> = @Composable ShareAnim<T>.() -> State<T>


/**
 * 更新元素，在使用共享元素动画跳转到目标页面之后，可以通过该方法更新共享的元素，（必须是原页面和当前界面都有的元素），那么在退出的时候，就可以使用新的共享元素实现动画。
 * 重申一遍，必须要在当前界面存在该元素时，才可更新，如果原页面不存在该元素，则会通过[onUpdateElement]进行通知，如果更新失败，那么将按照原共享元素执行。
 */
fun updateElement(vararg keys: Any) {
    ShareElementController.updateShareGroup(*keys)
}

/**
 * 正在更新元素，当调用[updateElement]方法时，共享元素控制器就会在已有的共享元素中查找新的用来实现动画的元素，如果找不到则会触发该回调，用户需要在原有界面准备新元素，以便更新共享元素
 * @param block 该回调函数的参数是缺少的元素的key集合
 */
@Composable
fun onUpdateElement(block: (Array<out Any>) -> Unit) {
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
sealed interface ShareState {

    infix fun Float.with(target: Float): Float {
        return if (!preShare) {
            if (this@ShareState is Sharing) {
                this between target
            } else this
        } else target
    }

}

/**
 * 无状态，显示原本的控件
 */
data object Init : ShareState

/**
 * 共享之前，在共享之前，需要显示共享控件，但是原本的控件也不能隐藏
 */
data object BeforeStart : ShareState

data object BeforeEnd : ShareState

/**
 *开始共享
 */
data object PreShare : ShareState

/**
 * sharing 手势触发时，将由手势控制共享的过程，[progress]则是手势的进度，
 * 目前手势只能控制后退时的共享元素变化
 */
data class Sharing(val progress: Float) : ShareState {
    /**
     * 将是当前界面元素的值[between]到上一个页面元素的值
     * @param pre 上一个页面元素的值
     */
    infix fun Float.between(pre: Float): Float {
        return pre - (pre - this) * progress
    }
}

/**
 * 共享结束
 */
data object ExitShare : ShareState

val ShareState.preShare get() = this is PreShare


internal fun Sharing.updateRect(startRect: Rect, endRect: Rect): Rect {
    val sTop = startRect.top
    val sLeft = startRect.left
    val sRight = startRect.right
    val sBottom = startRect.bottom
    val eTop = endRect.top
    val eLeft = endRect.left
    val eRight = endRect.right
    val eBottom = endRect.bottom
    return Rect(
        eLeft between sLeft,
        eTop between sTop,
        eRight between sRight,
        eBottom between sBottom
    )
}

/**
 *
 * @param preValue 上一页面的值
 * @param currentValue 当前页面的值
 * @param nextValue 下一页面的值
 * @param sharing 手势触发时,由该函数控制共享元素的变化过程
 */
@Composable
fun <T> updateValue(
    preValue: T,
    currentValue: T,
    nextValue: T,
    sharing: Sharing.(preValue: T, currentValue: T) -> T = { _, current -> current },
    anim: @Composable (function: (ShareState) -> T) -> State<T>
): State<T> {
    var pre by remember { mutableStateOf(preValue) }
    var exit by remember { mutableStateOf(currentValue) }
    return anim { it: ShareState ->
        if (it is BeforeStart) {
            pre = preValue
            exit = nextValue
        } else if (it is BeforeEnd) {
            pre = preValue
            exit = currentValue
        }
        when (it) {
            PreShare -> pre
            ExitShare -> exit
            is Sharing -> it.sharing(preValue, currentValue)
            else -> currentValue
        }
    }
}


internal fun Sharing.between(pre: Int, current: Int) =
    run { pre - (pre - current) * progress }.toInt()

fun sharing(sharing: Sharing, pre: Float, current: Float) = sharing.run { current between pre }
fun sharing(sharing: Sharing, pre: Int, current: Int): Int = sharing.between(pre, current)
fun sharing(sharing: Sharing, pre: Dp, current: Dp) =
    sharing.run { (current.value between pre.value).dp }

fun sharing(sharing: Sharing, pre: Rect, current: Rect) = sharing.updateRect(pre, current)
fun sharing(sharing: Sharing, pre: Offset, current: Offset) =
    pre - (pre - current) * sharing.progress

fun sharing(sharing: Sharing, pre: IntOffset, current: IntOffset) =
    pre - (pre - current) * sharing.progress

fun sharing(sharing: Sharing, pre: Size, current: Size) = sharing.run {
    Size(pre.width between current.width, pre.height between current.height)
}

fun sharing(sharing: Sharing, pre: IntSize, current: IntSize) = sharing.run {
    IntSize(between(pre.width, current.width), between(pre.height, current.height))
}

fun sharing(sharing: Sharing, pre: Color, current: Color) = sharing.run {
    val converter = Color.VectorConverter(pre.colorSpace)
    val vector = converter.convertToVector
    val pre4D = vector.invoke(pre)
    val current4D = vector.invoke(current)
    val target = AnimationVector4D(
        current4D.v1 between pre4D.v1,
        current4D.v2 between pre4D.v2,
        current4D.v3 between pre4D.v3,
        current4D.v4 between pre4D.v4
    )
    converter.convertFromVector.invoke(target)
}
