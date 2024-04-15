package com.erolc.mrouter.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope


/**
 * @author erolc
 * @since 2024/4/15 10:27
 */
@SinceKotlin("1.0")
data class WindowMenu(val id: String, val menu: @Composable FrameWindowScope.() -> Unit)