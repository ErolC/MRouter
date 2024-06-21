package com.erolc.mrouter.route.transform.share

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import com.erolc.mrouter.route.transform.GestureModel
import com.erolc.mrouter.route.transform.rememberDraggableModifier

/**
 * 普通的共享元素变换包装
 * 需要注意的是，共享元素的基础是两个确定位置大小的元素，所以不可以改变两个页面的位置
 */
internal class NormalShareTransformWrap(
    shareAnimationSpec: FiniteAnimationSpec<Rect>,
    gestureModel: GestureModel,
    private val orientation: Orientation,
    vararg keys: Any
) : ShareTransformWrap(shareAnimationSpec, gestureModel, *keys) {

    @Composable
    override fun Wrap(modifier: Modifier) {
        val gestureModifier = rememberDraggableModifier(orientation)
        Box(matchModifier(modifier, gestureModifier)) {
            PageContent(Modifier)
            Gesture(gestureModifier)
        }
    }
}