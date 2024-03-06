package com.erolc.mrouter.route.transform

import androidx.compose.animation.core.animateDp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.erolc.mrouter.utils.loge

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
        Box(modifier = modifier then pageModifier then Modifier.clip(RoundedCornerShape(corner))) {
            PageContent(Modifier.padding(top = 15.dp))
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
                else -> {
                    val size = (1 - it.progress) * 10
                    (if (size<0) 0f else size).dp
                }

            }
        }
        return Modifier.clip(RoundedCornerShape(corner))
    }
}