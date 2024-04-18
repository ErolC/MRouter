package com.erolc.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.uikit.ComposeUIViewControllerConfiguration
import androidx.compose.ui.window.ComposeUIViewController


/**
 * @author erolc
 * @since 2024/4/18 12:51
 * 如果希望自定义Delegate，可使用委托以保证lifecycleDelegate的运行
 * ```
 * class MyControllerDelegate(delegate: ComposeUIViewControllerDelegate = controllerDelegate):ComposeUIViewControllerDelegate by delegate
 * ```
 */
val controllerDelegate = UIViewControllerDelegate()

fun LifecycleUIViewController(
    configuration: ComposeUIViewControllerConfiguration.() -> Unit = {
        delegate = controllerDelegate
    },
    content: @Composable () -> Unit
) = ComposeUIViewController(configuration) {
    CompositionLocalProvider(LocalLifecycleDelegate provides controllerDelegate.lifecycleDelegate) {
        content()
    }
}