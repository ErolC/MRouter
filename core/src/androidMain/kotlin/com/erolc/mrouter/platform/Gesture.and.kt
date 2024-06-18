package com.erolc.mrouter.platform

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.erolc.mrouter.route.router.PanelRouter
import com.erolc.mrouter.route.transform.shouldHasGesture
import com.erolc.mrouter.scope.LocalPageScope

@Composable
actual fun BoxScope.GestureContent(modifier: Modifier) {
    val should = shouldHasGesture()
    val isPanel = LocalPageScope.current.router.parentRouter.parentRouter is PanelRouter
    if (should) {
        if (isPanel)
            Box(modifier = modifier.fillMaxHeight())
        else
            Box(
                modifier.height(200.dp).align(Alignment.CenterStart)
                    .run { systemGestureExclusion() })
    }
}