package com.erolc.mrouter.route.transform

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 普通的默认手势实现，在页面route时设置[normal]即可使用
 */
class NormalGestureWrap : GestureWrap() {
    @Composable
    override fun Wrap(modifier: Modifier, progress: (Float) -> Unit) {
        val (gestureModifier, pageModifier) = rememberDraggableModifier(
            Orientation.Horizontal,
            progress
        )
        Box(modifier = modifier then pageModifier) {
            PageContent(Modifier)
            Box(modifier = gestureModifier)
        }
    }
}