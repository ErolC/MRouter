package com.erolc.mrouter.route.transform

import androidx.compose.animation.core.animateDp
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.erolc.mrouter.platform.iosHasNotch
import com.erolc.mrouter.platform.safeAreaInsetsTop

/**
 * 类ios的Modal手势实现，在页面route时设置[modal]即可使用
 */
class ModalTransformWrap(private val proportion: Float, private val hasGesture: Boolean) :
    TransformWrap() {
    private val prevCorner = if (iosHasNotch) safeAreaInsetsTop() else 0f

    @Composable
    override fun Wrap(modifier: Modifier) {
        val scope = LocalTransformWrapScope.current
        val padding by scope.getGapSize(proportion)
        Box(modifier = modifier.padding(top = with(LocalDensity.current) {
            padding.toDp()
        }) then if (hasGesture) rememberDraggableModifier(Orientation.Vertical) else Modifier) {
            PageContent(Modifier.clip(RoundedCornerShape(10.dp)))
        }
    }

    @Composable
    override fun prevPageModifier(): Modifier {
        val transform = rememberTransformState()
        val corner by transform.animateDp {
            it.between(prevCorner.dp, prevCorner.dp, pause = 10.dp)
        }
        return Modifier.clip(RoundedCornerShape(corner))
    }
}