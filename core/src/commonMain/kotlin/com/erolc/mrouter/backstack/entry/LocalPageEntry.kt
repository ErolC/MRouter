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

/**
 * 用于单独承载局部页面的pageEntry，当存在于页面中的局部页面由于页面大小的原因需要隐藏时，当再次导航到该局部页面时将会以一个独立页面存在。该类就是载体
 * 比如：存在一个列表+详情的复合页面，详情是局部页面，这时点击列表将不会跳转，而是会替换局部页面，那么当界面足够小时，该复合页面将会变成只有列表的页面，
 * 此时再点击列表进行跳转时，将会打开一个详情的独立界面。
 */
class LocalPageEntry(scope: PageScope) : PageEntry(scope.apply { isLocalPageEntry = true }, Address("LocalPageEntry")) {
    //局部页面
    val panel: PanelEntry
        get() = (scope.router as PanelRouter).getPanel(Constants.defaultLocal).apply { isLocalPageEntry = true }

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