package com.erolc.mrouter.route.transform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


/**
 * 手势包裹层，是用于给页面的外部包裹一层手势
 */
abstract class GestureWrap {
    internal var isUseContent = false
    internal var pauseModifierPost = PauseModifierPost { prevPauseModifier() }

    /**
     * 这是内容部分，应当被包裹的部分，必须调用
     */
    var content: @Composable () -> Unit = {}
        internal set
        get() {
            isUseContent = true
            return field
        }

    /**
     * 用于包裹[content]和手势操作的
     * @param progress 进度，当产生手势操作时务必改变进度，以便更新界面，该进度为关闭页面进度，范围是[0-1]。
     */
    @Composable
    abstract fun Wrap(modifier: Modifier, progress: (Float) -> Unit)

    /**
     * 前一个页面在暂停时的modifier,用于控制在跳转过程中，上一个页面的页面变化
     */
    @Composable
    open fun prevPauseModifier(): Modifier = Modifier

    internal class PauseModifierPost(private val body: @Composable () -> Modifier) {
        @Composable
        fun getModifier(): Modifier = body()
    }

    internal fun updatePauseModifier(pauseModifierPost: PauseModifierPost) {
        this.pauseModifierPost = pauseModifierPost
    }
}

