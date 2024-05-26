package com.erolc.mrouter.scope

import androidx.compose.ui.geometry.Size
import com.erolc.mrouter.PanelState
import com.erolc.mrouter.route.transform.EnterState
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * 根域，代表window或者panel作为根的范围
 */
internal class HostScope {
    /**
     * 根界面的大小
     */
    val size = MutableStateFlow(Size.Zero)
    var panelState: PanelState? = null
}