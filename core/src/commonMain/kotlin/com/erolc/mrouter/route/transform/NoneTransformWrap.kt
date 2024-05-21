package com.erolc.mrouter.route.transform

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * 无手势实现
 */
class NoneTransformWrap : TransformWrap() {
    @Composable
    override fun Wrap(modifier: Modifier) {
        Box(modifier) {
            PageContent(Modifier)
        }
    }
}