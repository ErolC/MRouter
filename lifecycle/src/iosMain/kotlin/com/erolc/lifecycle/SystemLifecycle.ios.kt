package com.erolc.lifecycle

import androidx.compose.runtime.Composable

@Composable
actual fun SystemLifecycle(call: (Lifecycle.Event) -> Unit) {
    LocalLifecycleDelegate.current.onCall(call)
}