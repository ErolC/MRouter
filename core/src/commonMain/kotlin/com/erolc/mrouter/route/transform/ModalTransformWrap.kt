package com.erolc.mrouter.route.transform

import androidx.compose.animation.core.animateDp
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * 类ios的Modal手势实现，在页面route时设置[modal]即可使用
 */
class ModalTransformWrap(private val proportion: Float) : TransformWrap() {
    @Composable
    override fun Wrap(modifier: Modifier, progress: (Float) -> Unit) {

        val transform = rememberTransformTransition()

        val corner by transform.animateDp {
            when (it) {
                EnterState -> 0.dp
                else -> it.between(10f, 0f).dp
            }
        }
        val scope = LocalTransformWrapScope.current
        val gestureModifier = rememberDraggableModifier(
            Orientation.Vertical,
            progress,
            proportion
        )
        val padding by scope.gapSize
        Box(modifier = modifier.padding(top = padding.dp)) {
            PageContent(Modifier.clip(RoundedCornerShape(Dp(abs(corner.value)))))
            Box(modifier = gestureModifier)
        }
    }

    @Composable
    override fun prevPauseModifier(): Modifier {
        val transform = rememberTransformTransition()
        val corner by transform.animateDp {
            when (it) {
                EnterState -> 0.dp
                else -> it.between(0f, 10f).dp
            }
        }
        return Modifier.clip(RoundedCornerShape(Dp(abs(corner.value))))
    }
}