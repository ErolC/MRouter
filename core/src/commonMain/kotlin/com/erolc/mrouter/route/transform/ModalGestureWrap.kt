package com.erolc.mrouter.route.transform

import androidx.compose.animation.core.animateDp
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

class ModalGestureWrap(private val proportion: Float) : GestureWrap() {
    @Composable
    override fun Wrap(modifier: Modifier, progress: (Float) -> Unit) {

        val transform = rememberTransformTransition()
        val corner by transform.animateDp {
            if (it == Resume) 10.dp else 0.dp
        }
        val (gestureModifier, pageModifier) = rememberDraggableModifier(
            Orientation.Vertical,
            progress,
            proportion
        )
        Box(modifier = modifier.clip(RoundedCornerShape(corner)) then pageModifier) {
            PageContent(Modifier)
            Box(modifier = gestureModifier)
        }
    }

    @Composable
    override fun prevPauseModifier(): Modifier {
        val transform = rememberTransformTransition()
        val corner by transform.animateDp {
            when (it) {
                Resume -> 0.dp
                PauseState -> 10.dp
                else -> ((1 - it.progress) * 10).dp

            }
        }
        return Modifier.clip(RoundedCornerShape(corner))
    }
}