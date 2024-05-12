package com.erolc.mrouter.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.erolc.mrouter.scope.LocalPageScope
import com.erolc.mrouter.scope.PageScope
import kotlinx.coroutines.CoroutineScope

/**
 * 用于定义一个composable为页面
 * ```kotlin
 * @Composable
 * fun TestPage() = Page{
 * // code...
 * }
 * ```
 */
@Composable
fun Page(
    modifier: Modifier = Modifier.background(Color.White).safeContentPadding(),
    block: @Composable PageScope.() -> Unit
) {
    Box(modifier) {
        val scope = LocalPageScope.current
        block(scope)
    }
}

suspend fun LifecycleOwner.repeatOnLifecycle(
    state: Lifecycle.State,
    block: suspend CoroutineScope.() -> Unit
): Unit = lifecycle.repeatOnLifecycle(state, block)
