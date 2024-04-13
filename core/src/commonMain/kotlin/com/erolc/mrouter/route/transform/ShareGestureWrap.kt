package com.erolc.mrouter.route.transform

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * 共享手势
 */
open class ShareGestureWrap(vararg val keys: String) : GestureWrap() {

    @Composable
    override fun Wrap(modifier: Modifier, progress: (Float) -> Unit) {
        Box(modifier) {
            PageContent(Modifier)
        }
    }
}