package com.erolc.mrouter.dialog.toast

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class ToastOptions(val msg: String, val duration: Duration = 1.seconds)