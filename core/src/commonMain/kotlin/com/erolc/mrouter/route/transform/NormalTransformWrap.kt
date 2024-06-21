package com.erolc.mrouter.route.transform

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * 普通的默认手势实现，在页面route时设置[normal]即可使用
 */
class NormalTransformWrap(gestureModel: GestureModel) : TransformWrap(gestureModel) {
    @Composable
    override fun Wrap(modifier: Modifier) {
        val gestureModifier = rememberDraggableModifier(Orientation.Horizontal)
        Box(matchModifier(modifier, gestureModifier)) {
            PageContent(Modifier) // 页面内容
            Gesture(gestureModifier) // 手势触发部分
        }
    }
}