package com.erolc.mrouter.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.window.SecureFlagPolicy
import com.erolc.mrouter.backstack.entry.WindowEntry
import com.erolc.mrouter.dialog.DialogOptions
import com.erolc.mrouter.model.WindowOptions
import com.erolc.mrouter.window.calculateWindowSizeClass

@Composable
actual fun PlatformWindow(
    options: WindowOptions, entry: WindowEntry, content: @Composable () -> Unit
) {
    val context = LocalContext.current
    entry.scope.windowSize.value = calculateWindowSizeClass(context = context)
    content()
}

fun DialogOptions.asProperties(): DialogProperties {
    return DialogProperties(
        dismissOnBackPress, dismissOnClickOutside, when (secureFlagPolicy) {
            true -> SecureFlagPolicy.SecureOn
            false -> SecureFlagPolicy.SecureOff
            else -> SecureFlagPolicy.Inherit
        }, false, decorFitsSystemWindows
    )
}


@Composable
actual fun PlatformDialog(
    onDismissRequest: () -> Unit,
    options: DialogOptions,
    content: @Composable () -> Unit
) {

    Dialog(onDismissRequest, options.asProperties()) {
        (LocalView.current.parent as? DialogWindowProvider)?.window?.let { window ->
            window.setWindowAnimations(-1)
            window.setDimAmount(0f)
        }
        content()

    }
}


actual fun getPlatform(): Platform = Android