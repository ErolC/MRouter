package com.erolc.mrouter.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.erolc.mrouter.backstack.entry.PageEntry
import com.erolc.mrouter.route.router.PageRouter
import com.erolc.mrouter.route.shareelement.ShareElementController
import com.erolc.mrouter.route.transform.ResumeState
import com.erolc.mrouter.scope.HostScope
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


@Composable
internal fun PageRouter.HostContent(
    modifier: Modifier,
    hostScope: HostScope,
    lifecycleOwner: LifecycleOwner,
    block: @Composable () -> Unit = {}
) {
    DisposableEffect(lifecycleOwner) {
        // Setup the pageRouter with proper owners
        setLifecycleOwner(lifecycleOwner)
        onDispose { }
    }
    Box(modifier.fillMaxSize().onGloballyPositioned {
        hostScope.size.value = it.boundsInRoot().size
    }) {
        val stack by getPlayStack()
            .collectAsState(getBackStack().value.map { it as PageEntry })

        if (stack.size == 1) {
            stack.first().run {
                transformState.value = ResumeState
                shareTransform(null)
            }
        } else
            stack.last().shareTransform(stack.first())

        stack.forEach { it.Content(Modifier) }

        if (stack.size == 2)
            ShareElementController.initShare(stack.first(), stack.last())

        block()
    }

}