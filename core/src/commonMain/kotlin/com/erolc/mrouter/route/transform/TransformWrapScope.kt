package com.erolc.mrouter.route.transform

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size

val LocalTransformWrapScope = compositionLocalOf { TransformWrapScope() }

/**
 * 手势包裹层的作用域，该类主要是管理一些工作，比如：让页面内容也可以控制页面的手势
 */
class TransformWrapScope {
    /**
     * 页面大小
     */
    private val pageSize = mutableStateOf(Size.Zero)

    /**
     * proportion造成的间隙大小
     */
    val gapSize = mutableStateOf(0f)

    internal fun setSize(rect: Rect) {
        pageSize.value = rect.size
    }

    fun getPageSize(): State<Size> = pageSize

    /**
     * @param proportion 百分比，即该手势范围占页面的多少。
     * @param progress 手势的进度，0-1
     * @param orientation 手势的方向
     */
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun TransformWrap.rememberDraggableState(
        proportion: Float = 1f,
        progress: (Float) -> Unit,
        orientation: Orientation = Orientation.Horizontal
    ): AnchoredDraggableState<Float> {
        val size by getPageSize()
        val squareSize = if (orientation == Orientation.Horizontal) size.width else size.height
        val _gapSize = (1 - proportion) * squareSize
        gapSize.value = _gapSize
        val max = squareSize - _gapSize
        val anchors = remember(max) {
            DraggableAnchors {
                1f at max
                0f at 0f
            }
        }
        val anchoredDraggableState = rememberAnchoredDraggableState(0f, anchors)
        var offset = anchoredDraggableState.offset
        if (offset.isNaN())
            offset = 0f
        val offsetProgress = if (max == 0f) 0f else offset / max //0-1
        remember(offsetProgress) {
            //1-postExit;0-resume
            progress(offsetProgress)
        }
        return anchoredDraggableState
    }
}