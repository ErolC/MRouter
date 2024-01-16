package com.erolc.mrouter.scope

import androidx.compose.runtime.Composable
import com.erolc.mrouter.Constants

class GroupScope : PageScope() {

    @Composable
    fun layout(key: String = Constants.defaultKey, defaultPath: String = Constants.defaultPage) {
        require(!key.contains("/")) {
            "key can't has '/'"
        }
    }
}