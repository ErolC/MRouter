package com.erolc.mrouter.backstack.entry

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import com.erolc.lifecycle.Lifecycle
import com.erolc.mrouter.Constants
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.route.SysBackPressed
import com.erolc.mrouter.route.router.PanelRouter
import com.erolc.mrouter.route.transform.GestureWrap
import com.erolc.mrouter.scope.LocalPageScope
import com.erolc.mrouter.scope.PageScope
import com.erolc.mrouter.utils.loge
import com.erolc.mrouter.utils.logi

class LocalPageEntry(scope: PageScope) : PageEntry(scope.apply { isLocalPageEntry = true }, Address("LocalPageEntry")) {

    val panel: PanelEntry get() = (scope.router as PanelRouter).getPanel(Constants.defaultLocal).apply { isLocalPageEntry = true }

    override fun RealContent(): @Composable () -> Unit {
        return { panel.Content(Modifier) }
    }

    override fun handleLifecycleEvent(event: Lifecycle.Event) {
        super.handleLifecycleEvent(event)
        if (event > Lifecycle.Event.ON_CREATE) panel.handleLifecycleEvent(event)
    }

    override fun destroy() {
        super.destroy()
        panel.destroy()
        panel.isLocalPageEntry = false
    }
}