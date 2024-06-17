package com.erolc.mrouter.route.transform.share

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Transition
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import com.erolc.mrouter.route.transform.rememberDraggableModifier
import com.erolc.mrouter.utils.ShareState

/**
 * 普通的（手势区在页面左侧）共享元素变换包装
 * 需要注意的是，共享元素的基础是两个确定位置大小的元素，所以不可以改变两个页面的位置
 */
class NormalShareTransformWrap(
    shareAnimationSpec: FiniteAnimationSpec<Rect>,
    vararg keys: Any
) : ShareTransformWrap(shareAnimationSpec, *keys) {

    @Composable
    override fun Wrap(modifier: Modifier) {
        val gestureModifier = rememberDraggableModifier(Orientation.Horizontal)
        Box(modifier) {
            PageContent(Modifier)
            Box(modifier = gestureModifier) // 手势触发部分
        }
    }
}