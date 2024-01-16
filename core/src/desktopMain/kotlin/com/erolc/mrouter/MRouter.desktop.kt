package com.erolc.mrouter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.application

internal val LocalApplication: ProvidableCompositionLocal<ApplicationScope?> = staticCompositionLocalOf { null }


fun mRouterApplication(content: @Composable () -> Unit) {
//    FlatIntelliJLaf.setup()
    application {
        CompositionLocalProvider(LocalApplication provides this) {
            content()
        }
    }
}