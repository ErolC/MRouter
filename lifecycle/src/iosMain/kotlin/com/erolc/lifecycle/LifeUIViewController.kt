package com.erolc.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController


/**
 * @author erolc
 * @since 2024/4/18 12:51
 */

fun LifeUIViewController(
    lifecycleDelegate: LifecycleDelegate = LifecycleDelegate.lifecycleDelegate,
    content: @Composable () -> Unit
) = ComposeUIViewController({
    delegate = UIViewControllerDelegate(lifecycleDelegate)
}) {
    CompositionLocalProvider(LocalLifecycleDelegate provides lifecycleDelegate) {
        content()
    }
}