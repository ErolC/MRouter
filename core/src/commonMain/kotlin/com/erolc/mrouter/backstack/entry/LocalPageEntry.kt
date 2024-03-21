package com.erolc.mrouter.backstack.entry

import androidx.compose.runtime.Composable
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.scope.PageScope
import com.erolc.mrouter.utils.loge

class LocalPageEntry(scope: PageScope, address: Address, val entry: PanelEntry) : PageEntry(scope, address) {
    init {
        //在使用page的方式承载panel时，需要重置其内部页面路由的回退栈
        reset()
    }

    override fun RealContent(): @Composable () -> Unit {
        return { entry.PanelContent() }
    }

    internal fun reset() = entry.pageRouter.backStack.reset()
}