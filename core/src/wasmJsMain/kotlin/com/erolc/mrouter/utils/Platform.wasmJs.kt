package com.erolc.mrouter.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.erolc.mrouter.backstack.entry.LocalWindowScope
import com.erolc.mrouter.backstack.entry.WindowEntry
import com.erolc.mrouter.dialog.DialogOptions
import com.erolc.mrouter.model.WindowOptions
import com.erolc.mrouter.window.WindowSize
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.flow.MutableStateFlow
import org.w3c.dom.events.Event

@Composable
actual fun PlatformWindow(
    options: WindowOptions,
    entry: WindowEntry,
    content: @Composable () -> Unit
) {
    var size by remember {
        mutableStateOf(DpSize(window.innerWidth.dp, window.innerWidth.dp))
    }
    window.addEventListener("resize") {
        size = DpSize(window.innerWidth.dp, window.innerWidth.dp)
    }
    LocalWindowScope.current.windowSize.value = WindowSize.calculateFromSize(size)
    content()
}

@Composable
actual fun PlatformDialog(
    onDismissRequest: () -> Unit,
    options: DialogOptions,
    content: @Composable () -> Unit
) {

}


actual fun getPlatform(): Platform = Web