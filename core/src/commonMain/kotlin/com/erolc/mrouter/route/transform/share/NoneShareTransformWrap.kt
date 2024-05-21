package com.erolc.mrouter.route.transform.share

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Transition
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import com.erolc.mrouter.utils.ShareState

/**
 * 无手势的共享元素变换包装
 */
class NoneShareTransformWrap(
    shareAnimationSpec: FiniteAnimationSpec<Rect>,
    vararg keys: String
) : ShareTransformWrap(shareAnimationSpec,*keys) {

    @Composable
    override fun Wrap(modifier: Modifier) {
        Box(modifier) {
            PageContent(Modifier)
        }
    }
}