package com.erolc.mrouter.backstack.entry

import androidx.compose.runtime.Composable
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.scope.PageScope

class LocalPanelEntry(scope: PageScope, address: Address, val entry: PanelEntry) : PageEntry(scope, address) {

    override fun RealContent(): @Composable () -> Unit {
        return { entry.PanelContent() }
    }

    override fun destroy() {
        super.destroy()
        entry.destroy()
    }
}