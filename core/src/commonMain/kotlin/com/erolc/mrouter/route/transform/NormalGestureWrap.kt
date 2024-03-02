package com.erolc.mrouter.route.transform

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

object NormalGestureWrap : GestureWrap() {
    @Composable
    override fun Wrap(modifier: Modifier, progress: (Float) -> Unit) {
        val (gestureModifier, pageModifier) = rememberDraggableModifier(
            0.dp,
            Orientation.Horizontal,
            progress
        )
        Box(modifier = modifier then pageModifier) {
            PageContent(Modifier)
            Box(modifier = gestureModifier)
        }
    }
}