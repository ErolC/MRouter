package com.erolc.mrouter.route.transform

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Transition
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import com.erolc.mrouter.route.shareele.ShareState

/**
 * 共享手势，目前共享过程中不支持手势
 * @param keys 在该次页面转换过程中共享的控件的key
 */
open class ShareGestureWrap(vararg val keys: String,val transitionSpec: @Composable Transition.Segment<ShareState>.() -> FiniteAnimationSpec<Rect>) : TransformWrap() {

    @Composable
    override fun Wrap(modifier: Modifier, progress: (Float) -> Unit) {
        Box(modifier) {
            PageContent(Modifier)
        }
    }
}