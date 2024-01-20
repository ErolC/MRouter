package com.erolc.mrouter.backstack

import androidx.compose.runtime.Composable
import com.erolc.mrouter.lifecycle.Lifecycle
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.scope.PageScope

class PageEntry internal constructor(scope: PageScope, address: Address) :
    StackEntry(scope, address) {
    override val lifecycle: Lifecycle
        get() = registry
}

