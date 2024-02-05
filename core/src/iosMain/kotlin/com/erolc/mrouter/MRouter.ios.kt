package com.erolc.mrouter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController
import com.erolc.lifecycle.UIApplicationBackgroundDelegate
import com.erolc.lifecycle.localLifecycleDelegate
import com.erolc.lifecycle.UIViewControllerDelegate

fun MRouterUIViewController(
    backgroundDelegate: UIApplicationBackgroundDelegate,
    content: @Composable () -> Unit
) = ComposeUIViewController({
    delegate = UIViewControllerDelegate(backgroundDelegate, backgroundDelegate.lifecycleDelegate)
}) {
    CompositionLocalProvider(localLifecycleDelegate provides backgroundDelegate.lifecycleDelegate) {
        content()
    }
}