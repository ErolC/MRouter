package com.erolc.mrouter.route.transform

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.erolc.mrouter.backstack.LocalWindowScope
import kotlin.math.roundToInt

object NormalGestureWrap : GestureWrap() {
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Wrap(modifier: Modifier, progress: (Float) -> Unit) {
        val squareSize = LocalWindowScope.current.windowSize.value.width.size
        val size = with(LocalDensity.current) { squareSize.toPx() }

        val anchorsDraggableState = rememberAnchoredDraggableState(0f, size, DraggableAnchors {
            1f at size
            0f at 0f
        })

        val offset = anchorsDraggableState.requireOffset()
        remember(offset) { progress(offset / size) }
        Box(
            modifier = modifier
                .offset { IntOffset(offset.roundToInt(), 0) }) {

            PageContent(Modifier)

            Box(
                modifier = Modifier.fillMaxHeight().width(15.dp)
                    .anchoredDraggable(
                        state = anchorsDraggableState,
                        orientation = Orientation.Horizontal,
                    )
            )
        }
    }
}