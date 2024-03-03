package com.erolc.mrouter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.application

internal val LocalApplicationScope: ProvidableCompositionLocal<ApplicationScope> = staticCompositionLocalOf {
    throw RuntimeException("请使用mRouterApplication替代原本的application")
}


fun mRouterApplication(content: @Composable ApplicationScope.() -> Unit) {
//    FlatIntelliJLaf.setup()
    application {
        CompositionLocalProvider(LocalApplicationScope provides this) {
            content()
        }
    }
}