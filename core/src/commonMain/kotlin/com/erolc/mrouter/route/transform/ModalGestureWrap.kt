package com.erolc.mrouter.route.transform

import androidx.compose.animation.core.animateDp
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.erolc.mrouter.backstack.LocalWindowScope
import kotlin.math.roundToInt

object ModalGestureWrap : GestureWrap() {
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Wrap(modifier: Modifier, progress: (Float) -> Unit) {

        val transform = rememberTransform()
        val corner by transform.animateDp {
            if (it == Resume) 10.dp else 0.dp
        }
        val squareSize = LocalWindowScope.current.windowSize.value.height.size
        val paddingTop = 47.dp
        val size = with(LocalDensity.current) { squareSize.toPx() }
        val top = with(LocalDensity.current) { paddingTop.toPx() }
        val max = size - top
        val anchorsDraggableState = rememberAnchoredDraggableState(0f, max, DraggableAnchors {
            1f at max
            0f at 0f
        })
        val offset = anchorsDraggableState.requireOffset()
        val offsetProgress = rememberProgress(offset / max) //0-1
        //todo 这里还有个小问题
        remember(offsetProgress) {
            //1-postExit;0-resume
            progress(offsetProgress)
        }

        Box(
            modifier = modifier.padding(top = paddingTop).clip(RoundedCornerShape(corner))
                .offset { IntOffset(0, (max * offsetProgress).roundToInt()) }) {

            PageContent(Modifier)

            Box(
                modifier = Modifier.fillMaxWidth().height(15.dp)
                    .anchoredDraggable(
                        state = anchorsDraggableState,
                        orientation = Orientation.Vertical,
                    )
            )
        }
    }

    @Composable
    override fun prevPauseModifier(): Modifier {
        val transform = rememberTransform()
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