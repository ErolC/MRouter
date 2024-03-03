package com.erolc.mrouter.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.erolc.lifecycle.coroutineScope
import com.erolc.mrouter.scope.LocalPageScope
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberPageCoroutineScope(): CoroutineScope{
    val scope = LocalPageScope.current
    return remember(scope.lifecycle) { scope.lifecycle.coroutineScope }
}