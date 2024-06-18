package com.erolc.mrouter.platform

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun BoxScope.GestureContent(modifier: Modifier) = Box(modifier = modifier.fillMaxHeight())