package com.erolc.mrouter.model

import androidx.compose.runtime.Immutable
import androidx.core.bundle.Bundle

/**
 * @param key panel的key
 * @param clearTask 是否清空panel的栈
 */
@Immutable
data class PanelOptions(
    val key: String,
    val clearTask: Boolean = true,
//    val block: (Bundle) -> Unit = {}
)