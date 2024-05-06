package com.erolc.mrouter.route.transform

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * 普通的默认手势实现，在页面route时设置[normal]即可使用
 */
class NormalTransformWrap : TransformWrap() {
    @Composable
    override fun Wrap(modifier: Modifier, progress: (Float) -> Unit) {
        val gestureModifier = rememberDraggableModifier(
            Orientation.Horizontal,
            progress
        )
        Box(modifier = modifier) {
            PageContent(Modifier) // 页面内容
            Box(modifier = gestureModifier) // 手势触发部分
        }
    }
}