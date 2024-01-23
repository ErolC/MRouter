package com.erolc.mrouter.backstack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.erolc.mrouter.lifecycle.Lifecycle
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.route.DialogRouter
import com.erolc.mrouter.scope.PageScope

class PageEntry internal constructor(
    scope: PageScope,
    address: Address,
    internal val dialogRouter: DialogRouter
) :
    StackEntry(scope, address) {

    override val lifecycle: Lifecycle
        get() = registry

    @Composable
    override fun Content(modifier: Modifier) {
        super.Content(modifier)
        dialogRouter.getBackStack().collectAsState().let {
            val stack by remember { it }
            stack.forEach {
                it.Content(modifier)
            }
        }
    }
}

