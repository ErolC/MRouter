package com.erolc.mrouter.route.transform

import androidx.compose.animation.core.animateDp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp

object ModalGestureWrap : GestureWrap() {
    @Composable
    override fun Wrap(modifier: Modifier, progress: (Float) -> Unit) {
        val transform = rememberTransform()
        val corner by transform.animateDp { if (it == Resume) 10.dp else 0.dp }
        Surface(modifier = modifier.padding(top = 47.dp), RoundedCornerShape(corner)) {
            content()
        }
    }

    @Composable
    override fun prevPauseModifier(): Modifier {
        val transform = rememberTransform()
        val corner by transform.animateDp { if (it == PauseState) 10.dp else 0.dp }
        return Modifier.clip(RoundedCornerShape(corner))
    }
}