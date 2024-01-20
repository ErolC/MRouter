package com.erolc.mrouter.utils

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.window.SecureFlagPolicy
import com.erolc.mrouter.model.WindowOptions
import com.erolc.mrouter.window.calculateWindowSizeClass
import com.erolc.mrouter.backstack.WindowEntry
import com.erolc.mrouter.dialog.DialogOptions


@Composable
actual fun Platform(content: @Composable () -> Unit) {

}

@Composable
actual fun PlatformWindow(
    options: WindowOptions, entry: WindowEntry, content: @Composable () -> Unit
) {
    val context = LocalContext.current
    entry.getScope().windowSize.value = calculateWindowSizeClass(context = context)
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