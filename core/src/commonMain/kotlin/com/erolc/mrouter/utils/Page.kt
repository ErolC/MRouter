package com.erolc.mrouter.utils

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.erolc.mrouter.scope.LocalPageScope
import com.erolc.mrouter.scope.PageScope

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
fun Page(block: @Composable PageScope.() -> Unit) {
    val scope = LocalPageScope.current
    Surface {
        block(scope)
    }
}