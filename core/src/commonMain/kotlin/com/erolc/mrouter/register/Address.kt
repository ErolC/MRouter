package com.erolc.mrouter.register

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import com.erolc.mrouter.model.PageConfig
import com.erolc.mrouter.scope.PageScope

/**
 * @param path
 */
open class Address(open val path: String, open val config: PageConfig = emptyConfig,open val content: @Composable () -> Unit = {})
