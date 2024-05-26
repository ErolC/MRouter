package com.erolc.mrouter.route.transform

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import com.erolc.mrouter.platform.loge

/**
 * 普通的默认手势实现，在页面route时设置[normal]即可使用
 */
class NormalTransformWrap : TransformWrap() {
    @Composable
    override fun Wrap(modifier: Modifier) {
        val gestureModifier = rememberDraggableModifier(Orientation.Horizontal)
        Box(modifier = modifier) {
            PageContent(Modifier) // 页面内容
            Box(modifier = gestureModifier.background(Color.Red)) // 手势触发部分
        }
    }
}