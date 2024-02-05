package com.erolc.lifecycle

import androidx.compose.runtime.Composable

@Composable
expect fun SystemLifecycle(call:(Lifecycle.Event)->Unit)