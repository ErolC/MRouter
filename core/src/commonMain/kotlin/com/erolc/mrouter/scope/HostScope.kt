package com.erolc.mrouter.scope

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.erolc.mrouter.PanelState
import com.erolc.mrouter.route.transform.EnterState
import com.erolc.mrouter.window.DefHostSize
import com.erolc.mrouter.window.DefWindowSize
import com.erolc.mrouter.window.HostSize
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * 根域，代表window或者panel作为根的范围
 */
open class HostScope {
    /**
     * 根界面的大小
     */
    val size = MutableStateFlow(Size.Zero)
    var panelState: PanelState? = null

    private val _hostSize = mutableStateOf(DefHostSize)
    val hostSize: State<HostSize> = _hostSize

    internal open fun setHostSize(size: HostSize) {
        _hostSize.value = size
    }
}