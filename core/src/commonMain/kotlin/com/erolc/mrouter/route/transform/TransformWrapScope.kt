package com.erolc.mrouter.route.transform

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.erolc.mrouter.scope.LocalPageScope
import kotlin.reflect.KClass

val LocalTransformWrapScope = compositionLocalOf { TransformWrapScope() }

/**
 * 手势包裹层的作用域，该类主要是管理一些工作，比如：让页面内容也可以控制页面的手势
 */
class TransformWrapScope {
    private val _pageSize = mutableStateOf(Size.Zero)
    lateinit var wrap: TransformWrap
        internal set

    /**
     * 页面大小
     */
    val pageSize: State<Size> get() = _pageSize
    private val _gapSize = mutableStateOf(0f)

    /**
     * proportion造成的间隙大小
     */
    val gapSize: State<Float> get() = _gapSize
    lateinit var progress: (Float) -> Unit
        internal set

    internal fun setSize(rect: Rect) {
        _pageSize.value = rect.size
    }

    /**
     * 用于判断是否是属于你的包装
     */
    inline fun <reified T:TransformWrap> wrapIsInstance(): Boolean {
        return wrap is T
    }

    @Composable
    fun getGapSize(proportion: Float, orientation: Orientation = Orientation.Vertical): State<Float> {
        val size by _pageSize
        val squareSize = if (orientation == Orientation.Horizontal) size.width else size.height
        _gapSize.value = (1 - proportion) * squareSize
        return gapSize
    }

    /**
     * @param proportion 百分比，即该手势范围占页面的多少。
     * @param progress 手势的进度，0-1
     * @param orientation 手势的方向
     */
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun rememberDraggableState(
        progress: (Float) -> Unit,
        orientation: Orientation = Orientation.Horizontal
    ): AnchoredDraggableState<Float> {
        val size by pageSize
        val gapSize by gapSize
        val squareSize = if (orientation == Orientation.Horizontal) size.width else size.height
        val max = squareSize - gapSize
        val anchors = remember(max) {
            DraggableAnchors {
                1f at max
                0f at 0f
            }
        }
        val pageScope = LocalPageScope.current
        val anchoredDraggableState = rememberAnchoredDraggableState(pageScope, 0f, anchors)
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