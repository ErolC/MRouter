package com.erolc.mrouter.lifecycle

import androidx.compose.runtime.Composable

@Composable
expect fun SystemLifecycle(call:(Lifecycle.Event)->Unit)