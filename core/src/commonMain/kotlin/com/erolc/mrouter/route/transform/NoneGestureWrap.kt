package com.erolc.mrouter.route.transform

import androidx.compose.animation.core.animateDp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.erolc.mrouter.utils.loge

/**
 * 无手势实现
 */
class NoneGestureWrap : GestureWrap() {
    @Composable
    override fun Wrap(modifier: Modifier, progress: (Float) -> Unit) {
        Box(modifier) {
            PageContent(Modifier)
        }
    }
}