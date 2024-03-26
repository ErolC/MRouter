package com.erolc.mrouter.backstack.entry

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.erolc.lifecycle.Lifecycle
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.route.SysBackPressed
import com.erolc.mrouter.scope.LocalPageScope
import com.erolc.mrouter.scope.PageScope
import com.erolc.mrouter.utils.loge

class LocalPageEntry(scope: PageScope, val entry: PanelEntry) : PageEntry(scope, Address("")) {

    override fun RealContent(): @Composable () -> Unit {
        return { entry.Content(Modifier) }
    }

    override fun handleLifecycleEvent(event: Lifecycle.Event) {
        super.handleLifecycleEvent(event)
        entry?.handleLifecycleEvent(event)
    }
}