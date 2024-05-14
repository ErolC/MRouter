package com.erolc.mrouter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.application
import androidx.core.bundle.Bundle
import com.erolc.mrouter.model.PlatformRoute
import com.erolc.mrouter.route.router.WindowRouter

internal val LocalApplicationScope: ProvidableCompositionLocal<ApplicationScope> = staticCompositionLocalOf {
    throw RuntimeException("请使用mRouterApplication替代原本的application")
}


fun mRouterApplication(content: @Composable ApplicationScope.() -> Unit) {
    application {
        CompositionLocalProvider(LocalApplicationScope provides this) {
            content()
        }
    }
}

internal actual fun WindowRouter.route(
    route: PlatformRoute,
    args: Bundle,
    onResult: (Bundle) -> Unit
) {

}